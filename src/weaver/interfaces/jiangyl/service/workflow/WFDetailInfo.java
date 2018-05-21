package weaver.interfaces.jiangyl.service.workflow;

import java.util.ArrayList;
import java.util.List;

public class WFDetailInfo {
	private List<DetailRow> list = new ArrayList<DetailRow>();

	public List<DetailRow> getList() {
		return list;
	}

	public void setList(List<DetailRow> list) {
		this.list = list;
	}
}
