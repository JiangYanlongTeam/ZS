package weaver.interfaces.jiangyl.service.workflow;

import java.util.ArrayList;
import java.util.List;

public class DetainInfo {

    private List<DetailRow> rowList = new ArrayList<DetailRow>();

    public List<DetailRow> getRowList() {
        return rowList;
    }

    public void setRowList(List<DetailRow> rowList) {
        this.rowList = rowList;
    }
}
