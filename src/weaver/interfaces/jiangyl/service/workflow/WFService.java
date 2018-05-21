package weaver.interfaces.jiangyl.service.workflow;

import java.util.List;
import java.util.Map;

public interface WFService {

	/**
	 * 创建工作流程
	 * 
	 * @param workflowid
	 * @param userid
	 * @param title
	 * @param map
	 * @param detailinfo
	 * @return
	 */
	public WFResponse Createrequest(String workflowid, String userid, String title, Map<Object, Object> map,
			List<WFDetailInfo> detailinfo);
}
