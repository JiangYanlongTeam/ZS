package weaver.interfaces.jiangyl.project;

import org.tempuri.ns_xsd.F161031;
import org.tempuri.ns_xsd.SOAPDTSHEAD;
import org.tempuri.ns_xsd.Service_wsdl.ServicePortTypeProxy;
import org.tempuri.ns_xsd.T16103IN;
import org.tempuri.ns_xsd.T16103OUT;
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
 * 项目进度反馈
 */
public class PMO_RWJDFK extends BaseBean implements Action {
    @Override
    public String execute(RequestInfo request) {
        String currentdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String currenttime = new SimpleDateFormat("HHmmss").format(new Date());
        String requestid = Util.null2String(request.getRequestid());
        int formid = request.getRequestManager().getFormid();
        Map<String, Object> requestDataMap = ECUtil.getrequestdatamap(requestid, formid);

        Map<String, String> map = (Map<String, String>) requestDataMap.get("maindatamap");
        // 项目编号
        String rwbh = Util.null2String(map.get("rwbh"));
        // 任务进度反馈
        String rwfk = Util.null2String(map.get("rwfk"));
        // 任务附件
        String rwfj = Util.null2String(map.get("rwfj"));
        // 完成日期
        String wcrq = Util.null2String(map.get("wcrq"));

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

        T16103IN t16103IN = new T16103IN();
        t16103IN.setSPARFLD2(getEncode2(rwbh));
        t16103IN.setSPARFLD4(getEncode2(rwfk));
        t16103IN.setTFINSTDT(dealDate(wcrq));
        t16103IN.setODFUJISU(0);


        String[] xgfjs = rwfj.split(",");
        F161031[] F161031s = new F161031[xgfjs.length];
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
            F161031 F161031 = new F161031();
            writeLog("文档名称:" + filename + ",文件随机名字："+uuid);
            F161031.setFILENAME(getEncode2(datetime + "/" + uuid));
            F161031.setTRREMARK(getEncode2(filename));
            F161031s[i] = F161031;
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

        t16103IN.setVF161031(F161031s);

        try {
            T16103OUT result = servicePortTypeProxy.t16103(head,t16103IN);
            String RESPCODE = Util.null2String(getEncode(result.getRESPCODE()));
            String RESPINFO = Util.null2String(getEncode(result.getRESPINFO()));
            String PRMPINFO = Util.null2String(getEncode(result.getPRMPINFO()));
            writeLog("调用任务进度反馈接口返回RESPCODE：" + RESPCODE);
            writeLog("调用任务进度反馈接口返回RESPINFO：" + RESPINFO);
            writeLog("调用任务进度反馈接口返回PRMPINFO：" + PRMPINFO);

            if (!"ZSUCCESS".equals(RESPCODE)) {
                request.getRequestManager().setMessagecontent("提交到PM系统任务进度反馈失败：[RESPCODE:" + RESPCODE + ",RESPINFO:"
                        + RESPINFO + ",PRMPINFO:" + PRMPINFO + "]");
                return Action.FAILURE_AND_CONTINUE;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            request.getRequestManager().setMessagecontent("调用PMO接口16103失败："+e.getMessage());
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
