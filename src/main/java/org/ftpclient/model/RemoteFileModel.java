package org.ftpclient.model;

import org.apache.commons.net.ftp.FTPFile;

public class RemoteFileModel{
    private FTPFile file;
    private String name;
    private String path = "";

    public RemoteFileModel(FTPFile file, String name, String path) {
        this.file = file;
        this.name = name;
        this.path = path;
    }

    public RemoteFileModel(FTPFile file, String name) {
        this.file = file;
        this.name = name;
    }

    public FTPFile getFile() {
        return file;
    }

    public void setFile(FTPFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath(){
        if(path.equals("/")) return path;
        if(path.lastIndexOf('/') == path.length() - 1){
            return path + name;
        }else {
            return path + "/" + name;
        }

    }

    @Override
    public String toString() {
        return this.name;
    }
}
