package weaver.interfaces.jiangyl.project;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

public class DealDtInfoAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        RecordSet rs = new RecordSet();
        RecordSet rs1 = new RecordSet();
        String requestid = Util.null2String(request.getRequestid());
        int formid = ECUtil.getFormidByRequestID(requestid);
        String tableName = "formtable_main_" + Math.abs(formid);
        String mainsql = "select id from " + tableName + " a where a.requestid = '"
                + requestid + "'";
        writeLog("根据requestid：" + requestid + " 获取主表信息SQL：" + mainsql);
        rs.execute(mainsql);
        rs.next();
        String mainID = Util.null2String(rs.getString("id"));
        writeLog("主表流程对应ID：" + mainID);
        String sql = "select * from " + tableName + "_dt1 where mainid = '" + mainID + "' order by id";
        writeLog("获取明细表1数据：" + sql);
        rs.execute(sql);
        int count = rs.getCounts();
        writeLog("查询到明细表1数据条数：" + count);
        while (rs.next()) {
            String fzrwb = Util.null2String(rs.getString("fzrwb"));
            String dtid = Util.null2String(rs.getString("id"));
            writeLog("明细表fzrwb字段值：" + fzrwb);
            writeLog("明细表ID字段值：" + dtid);
            String fzr = getWorkCode(fzrwb);
            String updateFZR = "update " + tableName + "_dt1 set fzr = '" + fzr + "' where ID = '" + dtid + "'";
            writeLog("更新负责人字段SQL：" + updateFZR);
            rs1.execute(updateFZR);

            String lcb = Util.null2String(rs.getString("lcb"));
            String lcbjd = Util.null2String(rs.getString("lcbjd"));
            String lcbbh = Util.null2String(rs.getString("lcbbh"));
            String rwmc = Util.null2String(rs.getString("rwmc"));
            String rw = Util.null2String(rs.getString("rw"));
            String jhks = Util.null2String(rs.getString("jhks"));
            String jsjs = Util.null2String(rs.getString("jsjs"));
            String rwzt = Util.null2String(rs.getString("rwzt"));
            String rwbh = Util.null2String(rs.getString("rwbh"));
            String rwzb = Util.null2String(rs.getString("rwzb"));
            String lcbzb = Util.null2String(rs.getString("lcbzb"));

            String insertSQL = "insert into " + tableName + "_dt2 (mainid,lcb,lcbjd,lcbbh,rwmc,rw,jhks,jsjs,rwzt,rwbh,rwzb,lcbzb,fzr,fzrwb) values ('" + mainID + "'," +
                    "'" + lcb + "','" + lcbjd + "','" + lcbbh + "','" + rwmc + "','" + rw + "','" + jhks + "','" + jsjs + "','" + rwzt + "','" + rwbh + "','" + rwzb + "','" + lcbzb + "','" + fzr + "','" + fzrwb + "')";
            writeLog("插入明细表2：" + insertSQL);
            rs1.execute(insertSQL);
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
}
