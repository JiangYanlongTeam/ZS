package weaver.interfaces.jiangyl.service.workflow;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.axis.encoding.Base64;

import java.util.Map.Entry;

import weaver.conn.RecordSet;
import weaver.docs.webservices.DocAttachment;
import weaver.docs.webservices.DocInfo;
import weaver.docs.webservices.DocServiceImpl;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.hrm.resource.ResourceComInfo;
import weaver.soa.workflow.request.*;
import weaver.soa.workflow.request.Row;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowDetailTableInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;
import weaver.workflow.webservices.WorkflowService;
import weaver.workflow.webservices.WorkflowServiceImpl;

public class WFServiceImpl extends BaseBean implements WFService {

    /**
     * 方法描述 : 创建流程方法
     *
     * @param workflowid
     * @param userid     创建人id
     * @param title      流程标题
     * @param IsNextFlow 是否默认流转到下一节点
     * @param map        数据map
     * @return String
     */
    @Override
    public WFResponse doCreateRequest(int workflowid, String userid, String title, MainInfo[] mainInfos, DetailTableInfo detailTableInfo, AttachmentInfo[] attachmentInfos) {
        RecordSet rs = new RecordSet();
        WFResponse response = new WFResponse();
        if ("".equals(Util.null2String(workflowid))) {
            response.setRequestid("-100");
            response.setMessage("workflowid不能为空");
            return response;
        }
        if ("".equals(Util.null2String(userid))) {
            response.setRequestid("-100");
            response.setMessage("loginid不能为空");
            return response;
        }
        if ("".equals(Util.null2String(title))) {
            response.setRequestid("-100");
            response.setMessage("title不能为空");
            return response;
        }
        rs.execute("select * from hrmresource where loginid = '" + userid + "'");
        rs.next();
        String userID = Util.null2String(rs.getString("id"));
        if (userID.equals("")) {
            response.setRequestid("-100");
            response.setMessage("loginid" + userid + "在泛微oa中不存在");
            return response;
        }

        if (attachmentInfos != null) {
            for (AttachmentInfo attachmentInfo : attachmentInfos) {
                // 附件对应流程表单字段
                String columnname = attachmentInfo.getColumnname();
                if (Util.null2String(columnname).equals("")) {
                    response.setRequestid("-100");
                    response.setMessage("附件中columnname不能为空");
                    return response;
                }
                List<FileInfo> list = attachmentInfo.getList();
                for(int m = 0; m < list.size(); m++) {
                    FileInfo fileInfo = list.get(m);
                    if(fileInfo != null) {
                        // 附件对应的转换后的随即名字
                        String filename = fileInfo.getFilename();
                        // 附件对应的中文名称
                        String fileremark = fileInfo.getFileremark();
                        // 附件文件所在的目录
                        String filepath = new FileInfo().getFilepath() + filename;
                        if (Util.null2String(filename).equals("")) {
                            response.setRequestid("-100");
                            response.setMessage("附件中filename不能为空");
                            return response;
                        }
                        if (Util.null2String(fileremark).equals("")) {
                            response.setRequestid("-100");
                            response.setMessage("附件中fileremark不能为空");
                            return response;
                        }
                        File file = new File(filepath);
                        if (!file.exists()) {
                            response.setRequestid("-100");
                            response.setMessage("附件" + filename + "找不到");
                            return response;
                        }


                        String categoryid = getDocCategory(Util.null2String(workflowid), 0);
                        if (categoryid.lastIndexOf(",") > 0) {
                            categoryid = categoryid.substring(categoryid.lastIndexOf(",") + 1);
                        }

                        DocServiceImpl docService = new DocServiceImpl();
                        User user = this.getUser(Util.getIntValue(userID, 0));


                        String createdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String datetime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                        DocInfo doc = new DocInfo();
                        byte[] content = new byte[102400];
                        try {
                            int byteread;
                            byte data[] = new byte[1024];
                            InputStream input = new FileInputStream(new File(filepath));

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            while ((byteread = input.read(data)) != -1) {
                                out.write(data, 0, byteread);
                                out.flush();
                            }
                            content = out.toByteArray();
                            input.close();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        DocAttachment[] DocAttachments = new DocAttachment[1];
                        DocAttachment DocAttachment = new DocAttachment();
                        DocAttachment.setDocid(0);
                        DocAttachment.setImagefileid(0);
                        DocAttachment.setFilerealpath("");
                        DocAttachment.setFilename(fileremark);
                        DocAttachment.setFilecontent(Base64.encode(content));
                        DocAttachments[0] = DocAttachment;


                        doc.setId(0);// 默认为0
                        doc.setSeccategory(Util.getIntValue(categoryid,0));// 设置文档子目录（当前所属目录）
                        doc.setDocSubject(fileremark);// 设置文档标题
                        doc.setDoccreatedate(createdate);
                        doc.setDoccreatetime(datetime);
                        doc.setDoclastmoddate(createdate);
                        doc.setDoclastmodtime(datetime);
                        doc.setAttachments(DocAttachments);

                        int docid = 0;
                        try {
                            docid = docService.createDocByUser(doc, user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(docid > 0) {
                            String existDocid = attachmentInfo.getDocid();
                            if(existDocid.equals("")) {
                                attachmentInfo.setDocid(Util.null2String(docid));
                            } else {
                                attachmentInfo.setDocid(existDocid+","+docid);
                            }

                        }
                    }
                }
            }
        }


        String newrequestid = "";
        RequestInfo req = new RequestInfo();
        req.setCreatorid(userID + "");  //创建人
        req.setWorkflowid(workflowid + "");//流程id
        req.setDescription(title);  //流程标题
        MainTableInfo maintable = new MainTableInfo();

        int mainlength = mainInfos.length;
        if (attachmentInfos != null) {
            mainlength += attachmentInfos.length;
        }
        Property[] property = new Property[mainlength];
        int i = 0;
        Property p = null;
        for (MainInfo key : mainInfos) {
            p = new Property();
            p.setName(key.getKey());
            p.setValue(key.getVal());
            property[i] = p;
            i++;
        }
        if (attachmentInfos != null) {
            for (AttachmentInfo attachmentInfo : attachmentInfos) {
                p = new Property();
                p.setName(attachmentInfo.getColumnname());
                p.setValue(attachmentInfo.getDocid());
                property[i] = p;
                i++;
            }
        }

        maintable.setProperty(property);
        req.setMainTableInfo(maintable);
        req.setDetailTableInfo(detailTableInfo);
        req.setRequestlevel("0");
        RequestService service = new RequestService();
        try {
            newrequestid = service.createRequest(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(newrequestid) > 0) {
            response.setMessage("创建成功");
        } else {
            if ("-1".equals(newrequestid)) {
                response.setMessage("创建流程失败");
            } else if ("-2".equals(newrequestid)) {
                response.setMessage("用户没有流程创建权限");
            } else if ("-3".equals(newrequestid)) {
                response.setMessage("创建流程基本信息失败");
            } else if ("-4".equals(newrequestid)) {
                response.setMessage("保存表单主表信息失败");
            } else if ("-5".equals(newrequestid)) {
                response.setMessage("更新紧急程度失败");
            } else if ("-6".equals(newrequestid)) {
                response.setMessage("流程操作者失败");
            } else if ("-7".equals(newrequestid)) {
                response.setMessage("流转至下一节点失败");
            } else if ("-8".equals(newrequestid)) {
                response.setMessage("节点附加操作失败");
            } else {
                response.setMessage("未知错误");
            }
        }
        response.setRequestid(newrequestid);
        return response;
    }


    private User getUser(int userid) {
        User user = new User();
        try {
            ResourceComInfo rc = new ResourceComInfo();
            user.setUid(userid);
            user.setLoginid(rc.getLoginID("" + userid));
            user.setFirstname(rc.getFirstname("" + userid));
            user.setLastname(rc.getLastname("" + userid));
            user.setLogintype("1");
            user.setSex(rc.getSexs("" + userid));
            user.setLanguage(7);
            user.setEmail(rc.getEmail("" + userid));
            user.setLocationid(rc.getLocationid("" + userid));
            user.setResourcetype(rc.getResourcetype("" + userid));
            user.setJobtitle(rc.getJobTitle("" + userid));
            user.setJoblevel(rc.getJoblevel("" + userid));
            user.setSeclevel(rc.getSeclevel("" + userid));
            user.setUserDepartment(Util.getIntValue(rc.getDepartmentID("" + userid), 0));
            user.setManagerid(rc.getManagerID("" + userid));
            user.setAssistantid(rc.getAssistantID("" + userid));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 获取文档目录
     *
     * @param id
     * @param type 类型 0-流程  1--子目录
     * @return String  主目录,分目录,子目录
     */
    public String getDocCategory(String id, int type) {
        String Categoryids = "";

        RecordSet rs = new RecordSet();

        String sql = "select id,workflowname,workflowtype,formid,docPath,docCategory from workflow_base where id =" + Util.getIntValue(id, 0);

        if (type == 1) {
            sql = "select a.id,a.subcategoryid,b.maincategoryid from DocSecCategory a,DocSubCategory b where a.subcategoryid = b.id and a.id = " + Util.getIntValue(id, 0);
        }

        rs.executeSql(sql);
        if (rs.next()) {
            if (type == 0) {
                Categoryids = Util.null2String(rs.getString("docCategory"));
            } else {
                Categoryids = Util.null2String(rs.getString("maincategoryid"));
                Categoryids += "," + Util.null2String(rs.getString("subcategoryid"));
                Categoryids += "," + Util.null2String(rs.getString("id"));
            }
        }

        return Categoryids;
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
}
