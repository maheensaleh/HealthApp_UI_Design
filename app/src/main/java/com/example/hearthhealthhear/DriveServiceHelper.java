package com.example.hearthhealthhear;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
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

            return myFile.getId();

        });

    }


}
