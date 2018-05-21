package weaver.interfaces.jiangyl.service.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowDetailTableInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;
import weaver.workflow.webservices.WorkflowService;
import weaver.workflow.webservices.WorkflowServiceImpl;

public class WFServiceImpl extends BaseBean implements WFService {

	@Override
	public WFResponse Createrequest(String workflowid, String loginid, String title, Map<Object, Object> map,
			List<WFDetailInfo> detailinfo) {

		WFResponse response = new WFResponse();
		if ("".equals(Util.null2String(workflowid))) {
			response.setRequestid("-100");
			response.setMessage("workflowid不能为空");
			return response;
		}
		if ("".equals(Util.null2String(loginid))) {
			response.setRequestid("-100");
			response.setMessage("userid不能为空");
			return response;
		}
		if ("".equals(Util.null2String(title))) {
			response.setRequestid("-100");
			response.setMessage("title不能为空");
			return response;
		}
		RecordSet rs = new RecordSet();
		String sql = "select id from hrmresource where loginid = '" + loginid + "'";
		rs.execute(sql);
		rs.next();
		String userid = Util.null2String(rs.getString("id"));
		if ("".equals(userid)) {
			response.setRequestid("-100");
			response.setMessage("登录账号" + loginid + "在泛微OA中不存在");
			return response;
		}
		
		if(Util.null2String(map).equals("")) {
			map = new HashMap<Object, Object>();
		}
		
		WorkflowRequestTableField[] wrti = new WorkflowRequestTableField[map.size()]; // 字段信息
		int count = 0;
		for (Entry<Object, Object> entry : map.entrySet()) {
			String key = Util.null2String(entry.getKey());
			String val = Util.null2String(entry.getValue());
			wrti[count] = new WorkflowRequestTableField();
			wrti[count].setFieldName(key);//
			wrti[count].setFieldValue(val);//
			wrti[count].setView(true);// 字段是否可见
			wrti[count].setEdit(true);// 字段是否可编辑
			count++;
		}

		WorkflowRequestTableRecord[] wrtri = new WorkflowRequestTableRecord[1];// 主字段只有一行数据
		wrtri[0] = new WorkflowRequestTableRecord();
		wrtri[0].setWorkflowRequestTableFields(wrti);
		WorkflowMainTableInfo wmi = new WorkflowMainTableInfo();
		wmi.setRequestRecords(wrtri);
		
		if(Util.null2String(detailinfo).equals("")) {
			detailinfo = new ArrayList<WFDetailInfo>();
		}
		
		// 明细字段
		WorkflowDetailTableInfo wdti[] = new WorkflowDetailTableInfo[detailinfo.size()];// 两个明细表0明细表1,1明细表2
		for (int i = 0; i < detailinfo.size(); i++) {
			WFDetailInfo info = detailinfo.get(i);
			List<DetailRow> rows = info.getList();
			if (rows.isEmpty()) {
				continue;
			}
			WorkflowRequestTableRecord[] dt_rows = new WorkflowRequestTableRecord[rows.size()];// 数据 行数，假设添加2行明细数据
			for (int j = 0; j < rows.size(); j++) {
				DetailRow row = rows.get(j);
				DetailCell cell = row.getCell();
				Map<String, String> demap = cell.getMap();
				WorkflowRequestTableField[] cells = new WorkflowRequestTableField[demap.size()];
				int c = 0;
				for (Entry<String, String> entry : demap.entrySet()) {
					String key = Util.null2String(entry.getKey());
					String val = Util.null2String(entry.getValue());
					cells[c] = new WorkflowRequestTableField();
					cells[c].setFieldName(key);//
					cells[c].setFieldValue(val);//
					cells[c].setView(true);// 字段是否可见
					cells[c].setEdit(true);// 字段是否可编辑
					c++;
				}
				dt_rows[j] = new WorkflowRequestTableRecord();
				dt_rows[j].setWorkflowRequestTableFields(cells);
			}
			wdti[i] = new WorkflowDetailTableInfo();
			wdti[i].setWorkflowRequestTableRecords(dt_rows);// 加入明细表1的数据
		}

		WorkflowBaseInfo wbi = new WorkflowBaseInfo();
		wbi.setWorkflowId(workflowid);// workflowid 5 代表内部留言

		WorkflowRequestInfo wri = new WorkflowRequestInfo();// 流程基本信息
		wri.setCreatorId(userid);// 创建人id
		wri.setRequestLevel("0");// 0 正常，1重要，2紧急
		wri.setRequestName(title);// 流程标题
		wri.setWorkflowMainTableInfo(wmi);// 添加主字段数据
		wri.setWorkflowBaseInfo(wbi);
		wri.setWorkflowDetailTableInfos(wdti);
		// 执行创建流程接口
		WorkflowService WorkflowServicePortTypeProxy = new WorkflowServiceImpl();
		String requestid = WorkflowServicePortTypeProxy.doCreateWorkflowRequest(wri, 111);
		if (Integer.parseInt(requestid) > 0) {
			response.setMessage("创建成功");
		} else {
			if ("-1".equals(requestid)) {
				response.setMessage("创建流程失败");
			} else if ("-2".equals(requestid)) {
				response.setMessage("用户没有流程创建权限");
			} else if ("-3".equals(requestid)) {
				response.setMessage("创建流程基本信息失败");
			} else if ("-4".equals(requestid)) {
				response.setMessage("保存表单主表信息失败");
			} else if ("-5".equals(requestid)) {
				response.setMessage("更新紧急程度失败");
			} else if ("-6".equals(requestid)) {
				response.setMessage("流程操作者失败");
			} else if ("-7".equals(requestid)) {
				response.setMessage("流转至下一节点失败");
			} else if ("-8".equals(requestid)) {
				response.setMessage("节点附加操作失败");
			} else {
				response.setMessage("未知错误");
			}
		}
		response.setRequestid(requestid);
		return response;
	}
}
