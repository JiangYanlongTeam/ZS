package weaver.interfaces.jiangyl.project;

import org.tempuri.ns_xsd.F160921;
import org.tempuri.ns_xsd.SOAPDTSHEAD;
import org.tempuri.ns_xsd.Service_wsdl.ServicePortTypeProxy;
import org.tempuri.ns_xsd.T16092IN;
import org.tempuri.ns_xsd.T16092OUT;
import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 项目基本信息变更
 */
public class PMO_XMJBXXBG extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        String currentdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String currenttime = new SimpleDateFormat("HHmmss").format(new Date());
        RecordSet rs = new RecordSet();
        String requestid = Util.null2String(request.getRequestid());
        int formid = request.getRequestManager().getFormid();
        Map<String, Object> requestDataMap = ECUtil.getrequestdatamap(requestid, formid);
        // 获取主表信息
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) requestDataMap.get("maindatamap");
        // 项目编号
        String xmcode = Util.null2String(map.get("xmcode"));
        // 项目名称_新
        String xmmc1 = Util.null2String(map.get("xmmc1"));
        // 项目内容介绍_新
        String xmnr1 = Util.null2String(map.get("xmnr1"));
        // 项目主办部门_新 TODO 传编号
        String zbbm1 = Util.null2String(map.get("zbbm1"));
        // 项目协办部门_新 TODO 传编号，多部门
        String xbbm1 = Util.null2String(map.get("xbbm1"));
        // 项目分管领导_新 TODO 多人力资源 工号
        String fgld1 = Util.null2String(map.get("fgld1"));
        // 项目经理_新 TODO 人力资源 工号
        String xmjl1 = Util.null2String(map.get("xmjl1"));
        // 项目成员 TODO 多人力资源 工号
        String xmcy1 = Util.null2String(map.get("xmcy1"));
        // 项目绩效承诺_新
        String xmjx1 = Util.null2String(map.get("xmjx1"));
        // 相关附件
        String xgfj1 = Util.null2String(map.get("xgfj1"));

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

        writeLog("项目编号："+xmcode);
        writeLog("项目名称："+xmmc1);
        writeLog("主办部门："+getDepartmentCode(zbbm1));
        writeLog("协办部门："+getDepartmentCode2(xbbm1));
        writeLog("项目内容介绍："+xmnr1);
        writeLog("项目绩效承诺："+xmjx1);
        writeLog("分管领导："+getWorkCode2(fgld1));
        writeLog("项目经理："+getWorkCode(xmjl1));

        T16092IN stIn = new T16092IN();
        stIn.setPROCCODE(getEncode2(xmcode));
        stIn.setOCRMQYAM(getEncode2(xmmc1));
        stIn.setADDCONDT(getEncode2(getDepartmentCode(zbbm1)));
        stIn.setADDCOND1(getEncode2(getDepartmentCode2(xbbm1)));
        stIn.setBODYMESG(getEncode2(xmnr1));
        stIn.setSENDMESG(getEncode2(getWorkCode2(xmcy1)));
        stIn.setOCRMFZJG(getEncode2(xmjx1));
        stIn.setODTRNOTE(getEncode2(getWorkCode2(fgld1)));
        stIn.setSUBMUSER(getEncode2(getWorkCode(xmjl1)));
        String[] xgfjs = xgfj1.split(",");
        stIn.setODFUJISU(xgfjs.length);
        F160921[] F160921s = new F160921[xgfjs.length];
        List<String> filenames = new ArrayList<String>();
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
            F160921 F160921 = new F160921();
            writeLog("文档名称:" + filename + ",文件随机名字："+uuid);
            F160921.setFILENAME(getEncode2(datetime + "/" + uuid));
            F160921.setTRREMARK(getEncode2(filename));
            F160921s[i] = F160921;
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
        stIn.setVF160921(F160921s);

        try {
            T16092OUT result = servicePortTypeProxy.t16092(head,stIn);
            String RESPCODE = Util.null2String(getEncode(result.getRESPCODE()));
            String RESPINFO = Util.null2String(getEncode(result.getRESPINFO()));
            String PRMPINFO = Util.null2String(getEncode(result.getPRMPINFO()));
            String PROCCODE1 = Util.null2String(getEncode(result.getPROCCODE()));
            writeLog("调用变更项目接口返回RESPCODE：" + RESPCODE);
            writeLog("调用变更项目接口返回RESPINFO：" + RESPINFO);
            writeLog("调用变更项目接口返回PRMPINFO：" + PRMPINFO);
            writeLog("调用变更项目接口返回PROCCODE：" + PROCCODE1);

            if (!"ZSUCCESS".equals(RESPCODE)) {
                request.getRequestManager().setMessagecontent("提交到PM系统项目变更失败：[RESPCODE:" + RESPCODE + ",RESPINFO:"
                        + RESPINFO + ",PRMPINFO:" + PRMPINFO + ",PROCCODE:" + PROCCODE1 + "]");
                return Action.FAILURE_AND_CONTINUE;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            request.getRequestManager().setMessagecontent("调用PMO接口16092失败："+e.getMessage());
            return FAILURE_AND_CONTINUE;
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
