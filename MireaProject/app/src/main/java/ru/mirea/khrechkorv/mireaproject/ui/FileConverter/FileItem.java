package ru.mirea.khrechkorv.mireaproject.ui.FileConverter;

import java.io.File;

public class FileItem  {
    private String fileName;
    private long fileSize;
    private long modifiedDate;
    private File file;

    public FileItem(String fileName, long fileSize, long modifiedDate, File file) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.modifiedDate = modifiedDate;
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public File getFile() {
        return file;
    }
}
