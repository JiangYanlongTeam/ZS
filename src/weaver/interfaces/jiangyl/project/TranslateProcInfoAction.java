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
		int formid = ECUtil.getFormidByRequestID(requestid);
		String tableName = "formtable_main_" + Math.abs(formid);
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
		// 任务责任人文本
		String rwzrrwb = Util.null2String(map.get("rwzrrwb"));

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

		if (!"".equals(rwzrrwb)) {
			String zrr = getWorkCode(rwzrrwb);
			if (!"".equals(zrr)) {
				writeLog("更新sqr字段SQL：" + "update " + tableName + " set rwzrr = '" + zrr + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set rwzrr = '" + zrr + "' where requestid = '" + requestid + "'");
			}
		}

		if (!"".equals(zbbmwb)) {
			String zbbm = getDepartmentCode(zbbmwb);
			if (!"".equals(zbbm)) {
				writeLog("更新zbbm字段SQL：" + "update " + tableName + " set zbbm = '" + zbbm + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set zbbm = '" + zbbm + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xmjlwb)) {
			String xmjl = getWorkCode(xmjlwb);
			if (!"".equals(xmjl)) {
				writeLog("更新xmjl字段SQL：" + "update " + tableName + " set xmjl = '" + xmjl + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set xmjl = '" + xmjl + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xbbmwb)) {
			String xbbm = getDepartmentCode2(xbbmwb);
			if (!"".equals(xbbm)) {
				writeLog("更新xbbm字段SQL：" + "update " + tableName + " set xbbm = '" + xbbm + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set xbbm = '" + xbbm + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(fgldwb)) {
			String fgld = getWorkCode2(fgldwb);
			if (!"".equals(fgld)) {
				writeLog("更新fgld字段SQL：" + "update " + tableName + " set fgld = '" + fgld + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set fgld = '" + fgld + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if (!"".equals(xmcywb)) {
			String xmcy = getWorkCode2(xmcywb);
			if (!"".equals(xmcy)) {
				writeLog("更新xmcy字段SQL：" + "update " + tableName + " set xmcy = '" + xmcy + "' where requestid = '"
						+ requestid + "'");
				rs.execute("update " + tableName + " set xmcy = '" + xmcy + "' where requestid = '"
						+ requestid + "'");
			}
		}

		if(dt1 != null) {
			for (int i = 0; i < dt1.size(); i++) {
				Map<String, String> dt1_map = dt1.get(i);
				String dt_id = Util.null2String(dt1_map.get("id"));
				String dt1_fzrwb = Util.null2String(dt1_map.get("fzrwb"));
				if (!dt1_fzrwb.equals("")) {
					String fzr = getWorkCode(dt1_fzrwb);
					writeLog("更新明细表1的fzr字段SQL：" + "update " + tableName + "_dt1 set fzr = '" + fzr + "' where id = '"
							+ dt_id + "'");
					rs.execute("update " + tableName + "_dt1 set fzr = '" + fzr + "' where id = '"
							+ dt_id + "'");
				}
			}
		}

		return SUCCESS;
	}

	public String getWorkCode(String hrmid) {
		RecordSet rs = new RecordSet();
		rs.execute("select id from hrmresource where workcode = '" + hrmid + "'");
		rs.next();
		String workcode = Util.null2String(rs.getString("id"));
		return workcode;
	}

	public String getWorkCode2(String hrmid) {
		StringBuffer sb = new StringBuffer(",");
		RecordSet rs = new RecordSet();
		StringBuffer stringBuffer = new StringBuffer();
		if(hrmid.contains(",")) {
			String[] strs = hrmid.split(",");
			for(int i = 0; i < strs.length; i++) {
				stringBuffer.append("'");
				stringBuffer.append(strs[i]);
				stringBuffer.append("'");
				if(i != strs.length -1) {
					stringBuffer.append(",");
				}
			}
		}
		if(hrmid.contains(",")) {
			rs.execute("select id from hrmresource where workcode in (" + stringBuffer.toString() + ")");
		} else {
			rs.execute("select id from hrmresource where workcode = '"+hrmid+"'");
		}
		while (rs.next()) {
			String wid = Util.null2String(rs.getString("id"));
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
		rs.execute("select id from hrmdepartment where departmentcode = '" + depid + "'");
		rs.next();
		String departmentcode = Util.null2String(rs.getString("id"));
		return departmentcode;
	}

	public String getDepartmentCode2(String depid) {
		StringBuffer sb = new StringBuffer(",");
		RecordSet rs = new RecordSet();
		StringBuffer stringBuffer = new StringBuffer();
		if(depid.contains(",")) {
			String[] strs = depid.split(",");
			for(int i = 0; i < strs.length; i++) {
				stringBuffer.append("'");
				stringBuffer.append(strs[i]);
				stringBuffer.append("'");
				if(i != strs.length -1) {
					stringBuffer.append(",");
				}
			}
		}
		if(depid.contains(",")) {
			rs.execute("select id from hrmdepartment where departmentcode in (" + stringBuffer.toString() + ")");
		} else {
			rs.execute("select id from hrmdepartment where departmentcode = '"+depid+"'");
		}
		while (rs.next()) {
			String dpid = Util.null2String(rs.getString("id"));
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
		rs.execute("select id from hrmsubcompany where subcompanycode = '" + comid + "'");
		rs.next();
		String subcompanycode = Util.null2String(rs.getString("id"));
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
