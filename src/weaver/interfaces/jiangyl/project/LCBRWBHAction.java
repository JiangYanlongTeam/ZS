package weaver.interfaces.jiangyl.project;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.jiangyl.util.ECUtil;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

import java.util.List;
import java.util.Map;

public class LCBRWBHAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        RecordSet rs = new RecordSet();
        String requestid = Util.null2String(request.getRequestid());
        int formid = request.getRequestManager().getFormid();
        Map<String, Object> requestDataMap = ECUtil.getrequestdatamap(requestid, formid);
        String tableName = request.getRequestManager().getBillTableName();

        Map<String, String> map = (Map<String, String>) requestDataMap.get("maindatamap");
        // 项目编号
        String xmcode = Util.null2String(map.get("xmcode"));

        List<Map<String, String>> dt2 = (List<Map<String, String>>) requestDataMap.get("dt2");
        String tempBH = "0";
        for(int i = 0; i < dt2.size(); i++) {
            Map<String, String> dt2Map = dt2.get(i);
            String lcbbh = Util.null2String(dt2Map.get("lcbbh"));
            String deid = Util.null2String(dt2Map.get("id"));
            String substr = xmcode + lcbbh;
            String rwbh = Util.null2String(dt2Map.get("rwbh"));
            if(!"".equals(rwbh)) {
                tempBH = rwbh.replace(substr,"");
            } else {
                int newBH = Integer.parseInt(tempBH) + 1;
                if(newBH < 10) {
                    tempBH = "0" + newBH;
                } else {
                    tempBH = "" + newBH;
                }
                String sql = "update " + tableName + "_dt2 set rwbh = '"+xmcode + lcbbh + tempBH +"' where id = '"+deid+"'";
                writeLog("更新新增明细任务编号SQL："+sql);
                rs.execute(sql);
            }

        }
        return SUCCESS;
    }
}
