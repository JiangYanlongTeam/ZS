package weaver.interfaces.jiangyl.project;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.RequestInfo;

/**
 *
 * @author jiangyanlong
 * @date 2018-06-06
 */
public class AddDetailNumberAction extends BaseBean implements Action {

	@Override
	public String execute(RequestInfo request) {
		String requestid = Util.null2String(request.getRequestid());
		String tableName = Util.null2String(request.getRequestManager().getBillTableName());
		RecordSet rs = new RecordSet();
		RecordSet rs1 = new RecordSet();
		String sql = "select a.xmcode,b.lcbbh,b.id from " + tableName + " a, " + tableName + "_dt1 b where a.id = b.mainid and a.requestid = '"+requestid+"' order by b.id asc ";
		rs.execute(sql);
		int count = 1;
		while(rs.next()) {
			String number = "";
			String id = Util.null2String(rs.getString("id"));
			String lcbbh = Util.null2String(rs.getString("lcbbh"));
			String xmcode = Util.null2String(rs.getString("xmcode"));
			if(count < 10) {
				number = "0" + Util.null2String(count);
			} else {
				number = Util.null2String(count);
			}
			String rwbh = xmcode + "" + lcbbh + "" + number;
			String sql2 = "update " + tableName + "_dt1 set rwbh = '"+rwbh+"' where id = "+id+" ";
			writeLog("更新任务编号SQL:"+sql2);
			rs1.execute(sql2);
			count++;
		}
		return SUCCESS;
	}
}
