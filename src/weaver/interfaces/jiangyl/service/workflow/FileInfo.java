package weaver.interfaces.jiangyl.service.workflow;

public class FileInfo {
    private String filename;
    private String fileremark;
    private String filepath = "/home/ecology/filesystem/";

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileremark() {
        return fileremark;
    }

    public void setFileremark(String fileremark) {
        this.fileremark = fileremark;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
}
