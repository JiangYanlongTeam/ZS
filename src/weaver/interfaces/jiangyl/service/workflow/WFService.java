package weaver.interfaces.jiangyl.service.workflow;

import weaver.soa.workflow.request.DetailTableInfo;

public interface WFService {

	/**
	 * 创建工作流程
	 *
	 * @param workflowid
	 * @param userid
	 * @param title
	 * @return
	 */
	public WFResponse doCreateRequest(int workflowid,String userid,String title,MainInfo[] mainInfos, DetailTableInfo detailTableInfo, AttachmentInfo[] attachmentInfos);
}
