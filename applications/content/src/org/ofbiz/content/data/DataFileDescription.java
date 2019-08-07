package org.ofbiz.content.data;

/**
 * Created by galaxypan on 16.8.27.
 */
public class DataFileDescription {
    private String fileId;//原始id
    private boolean overwrite = true;//是否覆盖
    private String fileName;
    private Long fileSize;
    private String fileType;
    private byte[] content;
    private Boolean readOnly;

    public DataFileDescription(){

    }

    public DataFileDescription(String fileName, Long fileSize, String fileType, byte[] content) {
        this(fileName, fileSize, fileType, content, null, true);
    }

    public DataFileDescription(String fileName, Long fileSize, String fileType, byte[] content, String fileId, boolean overwrite) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.content = content;
        this.fileId = fileId;
        this.overwrite = overwrite;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public String getFileId() {
        return fileId;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}
