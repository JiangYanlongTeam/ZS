package weaver.interfaces.jiangyl.service.workflow;

import java.util.ArrayList;
import java.util.List;

public class AttachmentInfo {
    private String columnname;
    private List<FileInfo> list = new ArrayList<FileInfo>();
    private String docid;

    public String getColumnname() {
        return columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    public List<FileInfo> getList() {
        return list;
    }

    public void setList(List<FileInfo> list) {
        this.list = list;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }
}
