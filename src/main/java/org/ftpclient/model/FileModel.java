package org.ftpclient.model;

import java.io.File;

/**
 * Created by Administrator on 2019/3/30.
 */
public class FileModel {
    private File file;
    private String name;

    public FileModel(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
