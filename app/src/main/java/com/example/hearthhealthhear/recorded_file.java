package com.example.hearthhealthhear;

public class recorded_file {

    String file_name,file_path;

    public recorded_file() {
    }

    public recorded_file(String file_name, String file_path) {
        this.file_name = file_name;
        this.file_path = file_path;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }


}
