package Servisofts.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFile {
    private String name;
    private String fileName;
    private String contentType;
    private byte[] content;
    private long size;

    public MultipartFile(String name, String fileName, String contentType, byte[] content) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.size = content != null ? content.length : 0;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public long getSize() {
        return size;
    }

    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    public void transferTo(File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }

    @Override
    public String toString() {
        return "MultipartFile{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }
}
