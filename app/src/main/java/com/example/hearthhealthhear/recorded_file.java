package com.example.hearthhealthhear;

import android.location.Address;

import java.util.List;

public class recorded_file {

    String file_name,file_path;
    String addr;

    public recorded_file(){


    }

    public recorded_file(String file_name, String file_path,String addr) {
        this.file_name = file_name;
        this.file_path = file_path;
        this.addr = addr;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


}
