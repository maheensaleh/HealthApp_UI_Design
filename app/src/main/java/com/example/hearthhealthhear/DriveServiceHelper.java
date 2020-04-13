package com.example.hearthhealthhear;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.security.acl.Permission;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService){

        this.mDriveService = mDriveService;

    }

    public Task<String> createfile(String filepath,String filename){

        return Tasks.call(mExecutor,()->{

            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(filename);
            File file = new File(filepath);
            FileContent mediaContent = new FileContent("Audio/mp3",file);
            com.google.api.services.drive.model.File myFile = null;

            try{
                myFile = mDriveService.files().create(fileMetaData,mediaContent).execute();

            }catch (Exception e){
                e.printStackTrace();
            }

            if (myFile==null){

                throw new IOException("null results in file creation");

            }


//            com.google.api.services.drive.model.File mfileMetaData = new com.google.api.services.drive.model.File();
//            mfileMetaData.setName("Invoices4");
//            mfileMetaData.setMimeType("application/vnd.google-apps.folder");
//
//            com.google.api.services.drive.model.File mfile = mDriveService.files().create(mfileMetaData)
//                    .setFields("id")
//                    .execute();
//            System.out.println("Folder ID: " + mfile.getId());


//
            com.google.api.services.drive.model.File appfile_meta = new com.google.api.services.drive.model.File();
            appfile_meta.setName(filename);
            appfile_meta.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File(filepath);
            FileContent appmediaContent = new FileContent("Audio/mp3", filePath);
            com.google.api.services.drive.model.File app_file = mDriveService.files().create(appfile_meta, appmediaContent)
                    .setFields("id,parents")
                    .execute();
            System.out.println("File ID:----------- " + app_file.getId());


////            String folderId = appfile.getId();
//            com.google.api.services.drive.model.File mmfileMetaData = new com.google.api.services.drive.model.File();
//            mmfileMetaData.setName(filename);
//            mmfileMetaData.setParents(Collections.singletonList("appdataFolder"));
//            File mmfilepath = new File(filepath);
//            FileContent mmmediaContent = new FileContent("Audio/mp3", mmfilepath);
//            com.google.api.services.drive.model.File mmfile = mDriveService.files().create(mmfileMetaData, mmmediaContent)
//                    .setFields("id, parents")
//                    .execute();
//            System.out.println("File ID:----------- " + mmfile.getId());




            String fileId = myFile.getId();

            System.out.println("thisssssss=============== "+fileId);

            JsonBatchCallback<com.google.api.services.drive.model.Permission> callback = new JsonBatchCallback<com.google.api.services.drive.model.Permission>() {
                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                    System.err.println(e.getMessage());

                }

                @Override
                public void onSuccess(com.google.api.services.drive.model.Permission permission, HttpHeaders responseHeaders) throws IOException {
                    System.out.println("Permission ID: " + permission.getId());

                }
            };

//            JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
//                @Override
//                public void onFailure(GoogleJsonError e,
//                                      HttpHeaders responseHeaders)
//                        throws IOException {
//                    // Handle error
//                    System.err.println(e.getMessage());
//                }
//
//                @Override
//                public void onSuccess(Permission permission,
//                                      HttpHeaders responseHeaders)
//                        throws IOException {
////                    System.out.println("Permission ID: " + permission.getId());
//                }
//            };
            BatchRequest batch = mDriveService.batch();
            com.google.api.services.drive.model.Permission userPermission = new com.google.api.services.drive.model.Permission()
                    .setType("user").setRole("writer").setEmailAddress("samarjahan01n1965@gmail.com");

//            Permission userPermission = new Permission()
//                    .setType("user")
//                    .setRole("writer")
//                    .setValue("user@example.com");
            mDriveService.permissions().create(fileId,userPermission).setFields("id").queue(batch,callback);

//            mDriveService.permissions().insert(fileId, userPermission)
//                    .setFields("id")
//                    .queue(batch, callback);

            com.google.api.services.drive.model.Permission domainPermission = new com.google.api.services.drive.model.Permission()
                    .setType("user").setRole("reader").setEmailAddress("samarjahan01n1965@gmail.com");

//            Permission domainPermission = new Permission()
//                    .setType("user")
//                    .setRole("reader")
//                    .setValue("example.com");

            mDriveService.permissions().create(fileId,domainPermission).setFields("id").queue(batch,callback);
//            mDriveService.permissions().insert(fileId, domainPermission)
//                    .setFields("id")
//                    .queue(batch, callback);
//            batch.execute();
            batch.execute();



            mDriveService.files().delete(myFile.getId()).execute();
            return fileId;

        });

    }


}
