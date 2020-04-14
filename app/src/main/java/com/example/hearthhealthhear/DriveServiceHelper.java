package com.example.hearthhealthhear;

import android.content.res.AssetManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService){

        this.mDriveService = mDriveService;
//        this.service = service

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



            com.google.api.services.drive.model.File appfile_meta = new com.google.api.services.drive.model.File();
            appfile_meta.setName(filename);
            appfile_meta.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File(filepath);
            FileContent appmediaContent = new FileContent("Audio/mp3", filePath);
            com.google.api.services.drive.model.File app_file = mDriveService.files().create(appfile_meta, appmediaContent)
                    .setFields("id,parents")
                    .execute();
            System.out.println("File ID:----------- " + app_file.getId());

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

            BatchRequest batch = mDriveService.batch();
            com.google.api.services.drive.model.Permission userPermission = new com.google.api.services.drive.model.Permission()
                    .setType("user").setRole("writer").setEmailAddress("samarjahan01n1965@gmail.com");
            mDriveService.permissions().create(fileId,userPermission).setFields("id").queue(batch,callback);
            com.google.api.services.drive.model.Permission domainPermission = new com.google.api.services.drive.model.Permission()
                    .setType("user").setRole("reader").setEmailAddress("samarjahan01n1965@gmail.com");
            mDriveService.permissions().create(fileId,domainPermission).setFields("id").queue(batch,callback);
            batch.execute();
            mDriveService.files().delete(myFile.getId()).execute();




















            return fileId;



        });

    }


}
