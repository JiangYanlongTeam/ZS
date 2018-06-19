package weaver.interfaces.jiangyl.project;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.util.HashMap;
import java.util.Map;

public class CheckPercentAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        String lcbTableName = getPropValue("zs_project","lcbTableName");
        String requestid = request.getRequestid();
        String tablename = request.getRequestManager().getBillTableName();
        RecordSet recordSet = new RecordSet();
        String mainsql = "select c.LCBBH from " + tablename + " a, "+lcbTableName+" b, "+lcbTableName+"_dt1 c where b.id = c.mainid and b.xmlx = a.xmlx and a.requestid = '"+requestid+"'";
        writeLog("根据RequestID:"+requestid+" 查询里程碑编号SQL:"+mainsql);
        recordSet.execute(mainsql);
        Map<String,String> lcbMap = new HashMap<String,String>();
        while (recordSet.next()) {
            String LCBBH = Util.null2String(recordSet.getString("LCBBH"));
            lcbMap.put(LCBBH,LCBBH);
        }
        writeLog("获取浏览按钮中对应的所有里程碑编号："+lcbMap.toString());
        Map<String,String> lcMap = new HashMap<String, String>();
        Map<String,String> lcPercentMap = new HashMap<String, String>();
        String sql = "select * from " + tablename + " a, " + tablename + "_dt1 b where a.id = b.mainid and a.requestid = '"+requestid+"' ";
        recordSet.execute(sql);
        while(recordSet.next()) {
            String lcbbh = Util.null2String(recordSet.getString("lcbbh"));
            String rwzbb = Util.null2o(recordSet.getString("rwzb"));
            lcMap.put(lcbbh,lcbbh);
            if(lcPercentMap.containsKey(lcbbh)) {
                int sum = Util.getIntValue(lcPercentMap.get(lcbbh),0);
                sum += Util.getIntValue(rwzbb,0);
                lcPercentMap.put(lcbbh,String.valueOf(sum));
            } else {
                int sum = Util.getIntValue(rwzbb,0);
                lcPercentMap.put(lcbbh,String.valueOf(sum));
            }
        }
        writeLog("获取流程中对应的所有里程碑编号："+lcbMap.toString());
        if(lcbMap.size() != lcMap.size()) {
            request.getRequestManager().setMessagecontent("里程碑占比不足100% ! 请完善里程碑任务信息！");
            return FAILURE_AND_CONTINUE;
        }
        for(Map.Entry<String,String> entry : lcPercentMap.entrySet()) {
            String val = entry.getValue();
            if(!"100".equals(val)) {
                request.getRequestManager().setMessagecontent("里程碑下的任务占比不足100% ! 请完善任务信息！");
                return FAILURE_AND_CONTINUE;
            }

        }
        return SUCCESS;
    }
}
