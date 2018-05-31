package weaver.interfaces.jiangyl.service.workflow;

import java.util.ArrayList;
import java.util.List;

public class DetailRow {
    List<DetailCell> cells = new ArrayList<DetailCell>();

    public List<DetailCell> getCells() {
        return cells;
    }

    public void setCells(List<DetailCell> cells) {
        this.cells = cells;
    }
}
