package weaver.interfaces.jiangyl.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tempuri.ns_xsd.F160901;
import org.tempuri.ns_xsd.SOAPDTSHEAD;
import org.tempuri.ns_xsd.T16090IN;
import org.tempuri.ns_xsd.T16090OUT;
import org.tempuri.ns_xsd.T16091IN;
import org.tempuri.ns_xsd.T16091OUT;
import org.tempuri.ns_xsd.Service_wsdl.ServiceLocator;
import org.tempuri.ns_xsd.Service_wsdl.ServicePortTypeProxy;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;

/**
 * 项目立项审批流程/创建项目/创建任务
 * 
 * @author jiangyanlong
 *
 */
public class PM017018Action extends BaseBean implements Action {

	@Override
	public String execute(RequestInfo request) {
		String currentdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String currenttime = new SimpleDateFormat("HHmmss").format(new Date());
		String USERCODE_COL = "sqr";
		String USERCODE_VAL = "";
		String SSTARTDT_COL = "riqi";
		String SSTARTDT_VAL = "";
		String BM_COL = "bumen";
		String BM_VAL = "";
		String DW_COL = "danwei";
		String DW_VAL = "";
		String XMMC_COL = "xmmc";
		String XMMC_VAL = "";
		String XMCODE_COL = "xmcode";
		String XMCODE_VAL = "";
		String XMLX_COL = "xmlx";
		String XMLX_VAL = "";
		String XMLXBH_COL = "xmlxbh";
		String XMLXBH_VAL = "";
		String XMNR_COL = "xmnr";
		String XMNR_VAL = "";
		String XMJX_COL = "xmjx";
		String XMJX_VAL = "";
		String ZBBM_COL = "zbbm";
		String ZBBM_VAL = "";
		String XBBM_COL = "xbbm";
		String XBBM_VAL = "";
		String FGLD_COL = "fgld";
		String FGLD_VAL = "";
		String XMJL_COL = "xmjl";
		String XMJL_VAL = "";
		String XMCY_COL = "xmcy";
		String XMCY_VAL = "";
		String WYHYJ_COL = "wyhyj";
		String WYHYJ_VAL = "";
		String xgfj_COL = "xgfj";
		String xgfj_value = "";

		Property[] properties = request.getMainTableInfo().getProperty();// 获取表单主字段信息
		for (int i = 0; i < properties.length; i++) {
			String name = properties[i].getName();// 主字段名称
			String value = Util.null2String(properties[i].getValue());// 主字段对应的值
			if (name.equalsIgnoreCase(USERCODE_COL)) {
				USERCODE_VAL = value;
			}
			if (name.equalsIgnoreCase(SSTARTDT_COL)) {
				SSTARTDT_VAL = value;
			}
			if (name.equalsIgnoreCase(BM_COL)) {
				BM_VAL = value;
			}
			if (name.equalsIgnoreCase(DW_COL)) {
				DW_VAL = value;
			}
			if (name.equalsIgnoreCase(XMMC_COL)) {
				XMMC_VAL = value;
			}
			if (name.equalsIgnoreCase(XMCODE_COL)) {
				XMCODE_VAL = value;
			}
			if (name.equalsIgnoreCase(XMLX_COL)) {
				XMLX_VAL = value;
			}
			if (name.equalsIgnoreCase(XMLXBH_COL)) {
				XMLXBH_VAL = value;
			}
			if (name.equalsIgnoreCase(XMNR_COL)) {
				XMNR_VAL = value;
			}
			if (name.equalsIgnoreCase(XMJX_COL)) {
				XMJX_VAL = value;
			}
			if (name.equalsIgnoreCase(ZBBM_COL)) {
				ZBBM_VAL = value;
			}
			if (name.equalsIgnoreCase(XBBM_COL)) {
				XBBM_VAL = value;
			}
			if (name.equalsIgnoreCase(FGLD_COL)) {
				FGLD_VAL = value;
			}
			if (name.equalsIgnoreCase(XMJL_COL)) {
				XMJL_VAL = value;
			}
			if (name.equalsIgnoreCase(XMCY_COL)) {
				XMCY_VAL = value;
			}
			if (name.equalsIgnoreCase(WYHYJ_COL)) {
				WYHYJ_VAL = value;
			}
			if (name.equalsIgnoreCase(xgfj_COL)) {
				xgfj_value = Util.null2String(value);
			}
		}
		String[] xgfjs = xgfj_value.split(",");

		String USERCODE = getWorkCode(USERCODE_VAL);
		String SSTARTDT = SSTARTDT_VAL;
		String BANKNAME = getDepartmentCode(BM_VAL);
		String ODTRNOTE = getSubCompanyCode(DW_VAL);
		String OCRMQYAM = XMMC_VAL;
		String PROCCODE = XMCODE_VAL;
		String BUSITYPE = XMLX_VAL;
		String SPARFLD2 = XMLXBH_VAL;
		String BODYMESG = XMNR_VAL;
		String OCRMFZJG = XMJX_VAL;
		String ADDCONDT = getDepartmentCode(ZBBM_VAL);
		String ADDCOND1 = getDepartmentCode2(XBBM_VAL);
		String SPARFLD1 = getWorkCode2(FGLD_VAL);
		String SUBMUSER = getWorkCode(XMJL_VAL);
		String SENDMESG = getWorkCode2(XMCY_VAL);
		String ESUBJECT = WYHYJ_VAL;

		ServicePortTypeProxy projectProxy = new ServicePortTypeProxy();
		ServiceLocator serviceLocator = new ServiceLocator();
		String address = serviceLocator.getServiceAddress();
		writeLog("调用PM系统接口地址:" + address);

		SOAPDTSHEAD head = new SOAPDTSHEAD();
		head.setZJSYSTCD("OA1");
		head.setZJAUTHOR("");
		head.setZJBRANCH("320188990");
		head.setZJCHANEL("Z10");
		head.setZJTRANLX("0");
		head.setZJTRANNO(currentdate + currenttime);
		head.setZJTRDATE(currentdate);
		head.setZJTRTIME(currenttime);
		head.setZJUSERCD("990100013");
		head.setZJPASSWD("OVoxSjlSMUMwQjExMTAxMDEwMTM=");
		head.setZJAUTHIF("");

		T16090IN stIn = new T16090IN();
		stIn.setUSERCODE(getEncode2(USERCODE));
		stIn.setSSTARTDT(getEncode2(SSTARTDT));
		stIn.setBANKNAME("");
		stIn.setODTRNOTE(getEncode2(SPARFLD1)); // SPARFLD1 ODTRNOTE
		stIn.setOCRMQYAM(getEncode2(OCRMQYAM));
		stIn.setOCRMFZJG(getEncode2(OCRMFZJG));
		stIn.setSUBMUSER(getEncode2(SUBMUSER));
		stIn.setSENDMESG(getEncode2(SENDMESG));
		stIn.setPROCCODE(getEncode2(PROCCODE));
		stIn.setBUSITYPE(getEncode2(XMLXBH_VAL));
		stIn.setSPARFLD2("");
		stIn.setBODYMESG(getEncode2(BODYMESG));
		stIn.setADDCONDT(getEncode2(ADDCONDT));
		stIn.setADDCOND1(getEncode2(ADDCOND1));
		stIn.setSPARFLD1(getEncode2(ODTRNOTE));
		stIn.setESUBJECT(getEncode2(ESUBJECT));
		stIn.setSYSTDATE(getEncode2(currentdate));
		stIn.setODFUJISU(xgfjs.length);
		List<String> filenames = new ArrayList<String>();
		F160901[] F160901s = new F160901[xgfjs.length];
		String datetime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		for (int i = 0; i < xgfjs.length; i++) {
			String docid = xgfjs[i];
			writeLog("相关附件docid:" + docid);
			DocBean bean = new ECUtil().getDocBean(docid, "/home/ecology/ftp");
			try {
				new ECUtil().writeFile(bean);
			} catch (IOException e) {
				writeLog("写文件异常：" + e.getMessage());
				request.getRequestManager().setMessagecontent("写文件异常：" + e.getMessage());
				return Action.FAILURE_AND_CONTINUE;
			} catch (Exception e) {
				writeLog("写文件异常：" + e.getMessage());
				e.printStackTrace();
				request.getRequestManager().setMessagecontent("写文件异常：" + e.getMessage());
				return Action.FAILURE_AND_CONTINUE;
			}
			String uuid = bean.getUUID();
			String filename = bean.getFILENAME();
			F160901 F160901 = new F160901();
			writeLog("文档名称:" + filename + ",文件随机名字："+uuid);
			F160901.setFILENAME(getEncode2(datetime + "/" + uuid));
			F160901.setTRREMARK(getEncode2(filename));
			F160901s[i] = F160901;
			filenames.add(uuid);
		}

		if (!filenames.isEmpty()) {
			for (String name : filenames) {
				String response = "";
				StringBuffer sb = new StringBuffer();
				Process process;
				try {
					writeLog("执行命令：tftclient -dup -h0 -r" + datetime + "/" + name + " " + name + "");
					process = Runtime.getRuntime().exec(
							"/home/ecology/tftcli/bin/tftclient -dup -h0 -r" + datetime + "/" + name + " " + name + "");
					// process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c",
					// "tftclient -dup -h0 -r" + datetime + "/" + name + " " + name + "" });

					int code = process.waitFor();
					writeLog("执行命令返回code:" + code);
					if (code == 0) {
						BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
						while ((response = br.readLine()) != null) {
							sb.append(response);
						}
						response = sb.toString();
					}
					writeLog("执行命令传送文件[" + name + "]返回结果:" + response);
					process.destroy();
				} catch (IOException e) {
					writeLog("IO流异常：" + e.getMessage());
					e.printStackTrace();
				} catch (InterruptedException e) {
					writeLog("异常：" + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		stIn.setVF160901(F160901s);
		stIn.setBUSIPROP("");
		stIn.setBANKCODE(getEncode2(BANKNAME));
		stIn.setBUSITYP2("");
		stIn.setNUMBEREC(0);
		stIn.setSSTARTDT("");
		stIn.setSFINSTDT("");
		stIn.setTSTARTDT("");
		stIn.setTFINSTDT("");
		stIn.setOCRMJTAD("");

		writeLog("USERCODE:" + USERCODE);
		writeLog("SSTARTDT:" + SSTARTDT);
		writeLog("BANKNAME:" + BANKNAME);
		writeLog("ODTRNOTE:" + ODTRNOTE);
		writeLog("OCRMQYAM:" + OCRMQYAM);
		writeLog("SUBMUSER:" + SUBMUSER);
		writeLog("SENDMESG:" + SENDMESG);
		writeLog("PROCCODE:" + PROCCODE);
		writeLog("BUSITYPE:" + BUSITYPE);
		writeLog("SPARFLD2:" + SPARFLD2);
		writeLog("BODYMESG:" + BODYMESG);
		writeLog("ADDCONDT:" + ADDCONDT);
		writeLog("ADDCOND1:" + ADDCOND1);
		writeLog("SPARFLD1:" + SPARFLD1);
		writeLog("ESUBJECT:" + ESUBJECT);

		try {
			T16090OUT result = projectProxy.t16090(head, stIn);
			String RESPCODE = Util.null2String(getEncode(result.getRESPCODE()));
			String RESPINFO = Util.null2String(getEncode(result.getRESPINFO()));
			String PRMPINFO = Util.null2String(getEncode(result.getPRMPINFO()));
			String PROCCODE1 = Util.null2String(getEncode(result.getPROCCODE()));
			writeLog("调用创建项目接口返回RESPCODE：" + RESPCODE);
			writeLog("调用创建项目接口返回RESPINFO：" + RESPINFO);
			writeLog("调用创建项目接口返回PRMPINFO：" + PRMPINFO);
			writeLog("调用创建项目接口返回PROCCODE：" + PROCCODE1);

			if (!"ZSUCCESS".equals(RESPCODE)) {
				request.getRequestManager().setMessagecontent("提交到PM系统创建项目失败：[RESPCODE:" + RESPCODE + ",RESPINFO:"
						+ RESPINFO + ",PRMPINFO:" + PRMPINFO + ",PROCCODE:" + PROCCODE1 + "]");
				return Action.FAILURE_AND_CONTINUE;
			}
		} catch (RemoteException e) {
			writeLog("调用创建项目接口异常：" + e.getMessage());
			e.printStackTrace();
			request.getRequestManager().setMessagecontent("提交到PM系统创建项目失败：" + e.getMessage());
			return Action.FAILURE_AND_CONTINUE;
		}

		String lcbjd_col = "lcbjd";
		String lcbjd_val = "";
		String lcbbh_col = "lcbbh";
		String lcbbh_val = "";
		String lcbzb_col = "lcbzb";
		String lcbzb_val = "";
		String rwmc_col = "rwmc";
		String rwmc_val = "";
		String rwbh_col = "rwbh";
		String rwbh_val = "";
		String rw_col = "rw";
		String rw_val = "";
		String rwzb_col = "rwzb";
		String rwzb_val = "";
		String fzr_col = "fzr";
		String fzr_val = "";
		String jhks_col = "jhks";
		String jhks_val = "";
		String jhjs_col = "jhjs";
		String jhjs_val = "";

		writeLog("即将进入到明细表中>>>>>>>");
		DetailTable[] detailtable = request.getDetailTableInfo().getDetailTable();// 获取所有明细表
		DetailTable dt = detailtable[0];// 指定明细表 0表示明细表1
		Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
		writeLog("明细表1行数:" + s.length);
		for (int j = 0; j < s.length; j++) {
			writeLog("明细表第" + j + 1 + "条记录开始>>>>>>>");
			Row r = s[j];// 指定行
			Cell c[] = r.getCell();// 每行数据再按列存储
			for (int k = 0; k < c.length; k++) {
				Cell c1 = c[k];// 指定列
				String name = c1.getName();// 明细字段名称（对应明细表表单字段名称，如：mx_name）
				String value = c1.getValue();// 明细字段的值（对应明细表表单中的mx_name的值）
				if (lcbjd_col.equals(name)) {
					lcbjd_val = value;
				}
				if (lcbbh_col.equals(name)) {
					lcbbh_val = value;
				}
				if (lcbzb_col.equals(name)) {
					lcbzb_val = value;
				}
				if (rwmc_col.equals(name)) {
					rwmc_val = value;
				}
				if (rwbh_col.equals(name)) {
					rwbh_val = value;
				}
				if (rw_col.equals(name)) {
					rw_val = value;
				}
				if (rwzb_col.equals(name)) {
					rwzb_val = value;
				}
				if (fzr_col.equals(name)) {
					fzr_val = value;
				}
				if (jhks_col.equals(name)) {
					jhks_val = value;
				}
				if (jhjs_col.equals(name)) {
					jhjs_val = value;
				}
			}

			T16091IN t = new T16091IN();
			t.setPROCCODE(getEncode2(PROCCODE));
			t.setADDCONDT(getEncode2(lcbjd_val));
			t.setSPARFLD1(getEncode2(lcbbh_val));
			Long numberec = 0l;
			try {
				numberec = Long.parseLong(lcbzb_val);
			} catch (Exception e) {
				writeLog("转换明细字段lcbzb异常:" + e.getMessage());
			}
			t.setNUMBEREC(numberec);
			t.setTASKNAME(getEncode2(rwmc_val));
			t.setSPARFLD2(getEncode2(rwbh_val));
			t.setBODYMESG(getEncode2(rw_val));
			Long tasknumb = 0l;
			try {
				tasknumb = Long.parseLong(rwzb_val);
			} catch (Exception e) {
				writeLog("转换明细字段rwzb异常:" + e.getMessage());
			}
			t.setTASKNUMB(tasknumb);
			String usercode = getWorkCode(fzr_val);
			t.setUSERCODE(getEncode2(usercode));
			t.setSSTARTDT(getEncode2(dealDate(jhks_val)));
			t.setSFINSTDT(getEncode2(dealDate(jhjs_val)));
			t.setBUSITYPE("");
			t.setBUSIPROP("");
			t.setBANKCODE("");
			t.setBANKNAME("");
			t.setESUBJECT("");
			t.setTSTARTDT("");
			t.setTFINSTDT("");
			t.setBUSITYP2("");
			t.setODFUJISU(0);
			t.setCOMMENTS("");
			t.setSPARFLD3("");
			t.setSPARFLD4("");
			t.setADDCOND1("");

			writeLog("PROCCODE:" + PROCCODE);
			writeLog("ADDCONDT:" + lcbjd_val);
			writeLog("SPARFLD1:" + lcbbh_val);
			writeLog("NUMBEREC:" + numberec);
			writeLog("TASKNAME:" + rwmc_val);
			writeLog("SPARFLD2:" + rwbh_val);
			writeLog("BODYMESG:" + rw_val);
			writeLog("TASKNUMB:" + tasknumb);
			writeLog("USERCODE:" + usercode);
			writeLog("SSTARTDT:" + jhks_val);
			writeLog("SFINSTDT:" + jhjs_val);

			try {
				T16091OUT task = projectProxy.t16091(head, t);
				String task_RESPCODE = Util.null2String(getEncode(task.getRESPCODE()));
				String task_RESPINFO = Util.null2String(getEncode(task.getRESPINFO()));
				String task_PRMPINFO = Util.null2String(getEncode(task.getPRMPINFO()));
				writeLog("调用创建任务接口返回RESPCODE：" + task_RESPCODE);
				writeLog("调用创建任务接口返回RESPINFO：" + task_RESPINFO);
				writeLog("调用创建任务接口返回PRMPINFO：" + task_PRMPINFO);
				if (!"ZSUCCESS".equals(task_RESPCODE)) {
					request.getRequestManager().setMessagecontent("提交到PM系统创建任务失败：[RESPCODE:" + task_RESPCODE
							+ ",RESPINFO:" + task_RESPINFO + ",PRMPINFO:" + task_PRMPINFO + "]");
					return Action.FAILURE_AND_CONTINUE;
				}

			} catch (RemoteException e) {
				writeLog("调用创建任务接口异常：" + e.getMessage());
				e.printStackTrace();
				request.getRequestManager().setMessagecontent("提交到PM系统创建任务失败：" + e.getMessage());
				return Action.FAILURE_AND_CONTINUE;
			}
		}
		return Action.SUCCESS;
	}

	public static void main(String[] args) {
		String datetime = new SimpleDateFormat("yyyyMM").format(new Date());
		System.out.println(datetime);
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
