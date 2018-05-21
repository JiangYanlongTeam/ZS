package weaver.interfaces.jiangyl.project;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class TranslateProcInfoAction extends BaseBean implements Action {

	@Override
	public String execute(RequestInfo request) {
		RecordSet rs = new RecordSet();
		String requestid = Util.null2String(request.getRequestid());
		int formid = request.getRequestManager().getFormid();
		String tableName = request.getRequestManager().getBillTableName();
		Map<String, Object> requestDataMap = ECUtil.getrequestdatamap(requestid, formid);
		// 获取主表信息
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) requestDataMap.get("maindatamap");
		// 申请人文本
		String sqrwb = Util.null2String(map.get("sqrwb"));
		// 主办部门文本
		String zbbmwb = Util.null2String(map.get("zbbmwb"));
		// 项目经理文本
		String xmjlwb = Util.null2String(map.get("xmjlwb"));

		// 协办部门文本
		String xbbmwb = Util.null2String(map.get("xbbmwb"));
		// 分管领导文本
		String fgldwb = Util.null2String(map.get("fgldwb"));
		// 项目成员文本
		String xmcywb = Util.null2String(map.get("xmcywb"));

		@SuppressWarnings("unchecked")
		List<Map<String, String>> dt1 = (List<Map<String, String>>) requestDataMap.get("dt1");

		if (!"".equals(sqrwb)) {
			String sqr = getWorkCode(sqrwb);
			if (!"".equals(sqr)) {
				writeLog("更新sqr字段SQL：" + "update " + tableName + " set sqr = '" + sqr + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set sqr = '" + sqr + "' where requestid = '" + requestid + "'");
			}
		}

		if (!"".equals(zbbmwb)) {
			String zbbm = getDepartmentCode(zbbmwb);
			if (!"".equals(zbbm)) {
				writeLog("更新zbbm字段SQL：" + "update " + tableName + " set zbbm = '" + zbbm + "' where requestid = '"
						+ requestid + "'");
				rs.execute("更新zbbm字段SQL：" + "update " + tableName + " set zbbm = '" + zbbm + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xmjlwb)) {
			String xmjl = getWorkCode(xmjlwb);
			if (!"".equals(xmjl)) {
				writeLog("更新xmjl字段SQL：" + "update " + tableName + " set xmjl = '" + xmjl + "' where requestid = '"
						+ requestid + "'");
				rs.execute("更新xmjl字段SQL：" + "update " + tableName + " set xmjl = '" + xmjl + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xbbmwb)) {
			String xbbm = getDepartmentCode2(xbbmwb);
			if (!"".equals(xbbm)) {
				writeLog("更新xbbm字段SQL：" + "update " + tableName + " set xbbm = '" + xbbm + "' where requestid = '"
						+ requestid + "'");
				rs.execute("更新xbbm字段SQL：" + "update " + tableName + " set xbbm = '" + xbbm + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(fgldwb)) {
			String fgld = getWorkCode2(fgldwb);
			if (!"".equals(fgld)) {
				writeLog("更新fgld字段SQL：" + "update " + tableName + " set fgld = '" + fgld + "' where requestid = '"
						+ requestid + "'");
				rs.execute("更新fgld字段SQL：" + "update " + tableName + " set fgld = '" + fgld + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xmcywb)) {
			String xmcy = getWorkCode2(xmcywb);
			if (!"".equals(xmcy)) {
				writeLog("更新xmcy字段SQL：" + "update " + tableName + " set xmcy = '" + xmcy + "' where requestid = '"
						+ requestid + "'");
				rs.execute("更新xmcy字段SQL：" + "update " + tableName + " set xmcy = '" + xmcy + "' where requestid = '"
						+ requestid + "'");
			}
		}

		for (int i = 0; i < dt1.size(); i++) {
			Map<String, String> dt1_map = dt1.get(i);
			String dt_id = Util.null2String(dt1_map.get("id"));
			String dt1_fzrwb = Util.null2String(dt1_map.get("fzrwb"));
			if (!dt1_fzrwb.equals("")) {
				String fzr = getWorkCode(dt1_fzrwb);
				writeLog("更新明细表1的fzr字段SQL：" + "update " + tableName + "_dt1 set fzr = '" + fzr + "' where id = '"
						+ dt_id + "'");
				rs.execute("更新明细表1的fzr字段SQL：" + "update " + tableName + "_dt1 set fzr = '" + fzr + "' where id = '"
						+ dt_id + "'");
			}
		}

		return null;
	}

	public String getWorkCode(String hrmid) {
		RecordSet rs = new RecordSet();
		rs.execute("select workcode from hrmresource where id = '" + hrmid + "'");
		rs.next();
		String workcode = Util.null2String(rs.getString("workcode"));
		return workcode;
	}

	public String getWorkCode2(String hrmid) {
		StringBuffer sb = new StringBuffer(",");
		RecordSet rs = new RecordSet();
		rs.execute("select workcode from hrmresource where id in (" + hrmid + ")");
		while (rs.next()) {
			String wid = Util.null2String(rs.getString("workcode"));
			sb.append(wid);
			sb.append(",");
		}
		if (",".equals(sb.toString())) {
			return "";
		}
		String sbs = sb.toString();
		String sbsss = sbs.substring(1, sbs.length() - 1);
		return sbsss;
	}

	public String getDepartmentCode(String depid) {
		RecordSet rs = new RecordSet();
		rs.execute("select departmentcode from hrmdepartment where id = '" + depid + "'");
		rs.next();
		String departmentcode = Util.null2String(rs.getString("departmentcode"));
		return departmentcode;
	}

	public String getDepartmentCode2(String depid) {
		StringBuffer sb = new StringBuffer(",");
		RecordSet rs = new RecordSet();
		rs.execute("select departmentcode from hrmdepartment where id in (" + depid + ")");
		while (rs.next()) {
			String dpid = Util.null2String(rs.getString("departmentcode"));
			sb.append(dpid);
			sb.append(",");
		}
		if (",".equals(sb.toString())) {
			return "";
		}
		String sbs = sb.toString();
		String sbsss = sbs.substring(1, sbs.length() - 1);
		return sbsss;
	}

	public String getSubCompanyCode(String comid) {
		RecordSet rs = new RecordSet();
		rs.execute("select subcompanycode from hrmsubcompany where id = '" + comid + "'");
		rs.next();
		String subcompanycode = Util.null2String(rs.getString("subcompanycode"));
		return subcompanycode;
	}

	public String getEncode2(String content) {
		String c = "";
		try {
			c = new String(content.getBytes("utf8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return c;
	}

	public String getEncode(String content) {
		String c = "";
		try {
			c = new String(content.getBytes("ISO-8859-1"), "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return c;
	}

	public String dealDate(String date) {
		if (!"".equals(Util.null2String(date))) {
			date = date.replaceAll("-", "");
		}
		return date;
	}

}
