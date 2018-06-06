package weaver.interfaces.jiangyl.project;

import org.tempuri.ns_xsd.SOAPDTSHEAD;
import org.tempuri.ns_xsd.Service_wsdl.ServicePortTypeProxy;
import org.tempuri.ns_xsd.T16093IN;
import org.tempuri.ns_xsd.T16093OUT;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 里程碑任务信息变更
 *
 */
public class PMO_LCBRWBG extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        String currentdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String currenttime = new SimpleDateFormat("HHmmss").format(new Date());
        RecordSet rs = new RecordSet();
        String requestid = Util.null2String(request.getRequestid());
        int formid = request.getRequestManager().getFormid();
        Map<String, Object> requestDataMap = ECUtil.getrequestdatamap(requestid, formid);

        Map<String, String> map = (Map<String, String>) requestDataMap.get("maindatamap");
        // 项目编号
        String xmcode = Util.null2String(map.get("xmcode"));

        ServicePortTypeProxy servicePortTypeProxy = new ServicePortTypeProxy();
        SOAPDTSHEAD head = new SOAPDTSHEAD();
        head.setZJSYSTCD("OA");
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

        T16093IN t16093IN = new T16093IN();

        List<Map<String, String>> dt2 = (List<Map<String, String>>) requestDataMap.get("dt2");
        for(int i = 0; i < dt2.size(); i++) {
            Map<String,String> dt2Map = dt2.get(i);
            String lcbjd = Util.null2String(dt2Map.get("lcbjd"));
            String lcbbh = Util.null2String(dt2Map.get("lcbbh"));
            String lcbzb = Util.null2String(dt2Map.get("lcbzb"));
            String rwmc = Util.null2String(dt2Map.get("rwmc"));
            String rw = Util.null2String(dt2Map.get("rw"));
            String rwzb = Util.null2String(dt2Map.get("rwzb"));
            String fzr = Util.null2String(dt2Map.get("fzr"));
            String jhks = Util.null2String(dt2Map.get("jhks"));
            String jsjs = Util.null2String(dt2Map.get("jsjs"));
            String rwzt = Util.null2String(dt2Map.get("rwzt"));
            String rwbh = Util.null2String(dt2Map.get("rwbh"));

            t16093IN.setPROCCODE(getEncode2(xmcode));
            t16093IN.setADDCONDT(getEncode2(lcbjd));
            t16093IN.setSPARFLD1(getEncode2(lcbbh));
            t16093IN.setTASKNAME(getEncode2(rwmc));
            t16093IN.setSPARFLD2(getEncode2(rwbh));
            if("".equals(lcbzb)) {
                lcbzb = "0";
            }
            t16093IN.setNUMBEREC(Long.parseLong(lcbzb));
            t16093IN.setBODYMESG(getEncode2(rw));
            if("".equals(rwzb)) {
                rwzb = "0";
            }
            t16093IN.setTASKNUMB(Long.parseLong(rwzb));
            t16093IN.setUSERCODE(getEncode2(getWorkCode(fzr)));
            t16093IN.setSSTARTDT(getEncode2(dealDate(jhks)));
            t16093IN.setSFINSTDT(getEncode2(dealDate(jsjs)));
            t16093IN.setBUSIPROP(getEncode2(rwzt));
            t16093IN.setBUSITYPE("");
            t16093IN.setBANKCODE("");
            t16093IN.setBANKNAME("");
            t16093IN.setESUBJECT("");
            t16093IN.setTSTARTDT("");
            t16093IN.setTFINSTDT("");
            t16093IN.setCOMMENTS("");





            try {
                T16093OUT result = servicePortTypeProxy.t16093(head,t16093IN);
                String RESPCODE = Util.null2String(getEncode(result.getRESPCODE()));
                String RESPINFO = Util.null2String(getEncode(result.getRESPINFO()));
                String PRMPINFO = Util.null2String(getEncode(result.getPRMPINFO()));
                writeLog("调用里程碑任务信息变更接口返回RESPCODE：" + RESPCODE);
                writeLog("调用里程碑任务信息变更接口返回RESPINFO：" + RESPINFO);
                writeLog("调用里程碑任务信息变更接口返回PRMPINFO：" + PRMPINFO);

                if (!"ZSUCCESS".equals(RESPCODE)) {
                    request.getRequestManager().setMessagecontent("提交到PM系统里程碑任务信息变更失败：[RESPCODE:" + RESPCODE + ",RESPINFO:"
                            + RESPINFO + ",PRMPINFO:" + PRMPINFO + "]");
                    return Action.FAILURE_AND_CONTINUE;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                request.getRequestManager().setMessagecontent("调用PMO接口16093失败："+e.getMessage());
                return FAILURE_AND_CONTINUE;
            }
        }
        return SUCCESS;
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
