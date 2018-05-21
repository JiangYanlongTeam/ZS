package weaver.interfaces.jiangyl.project;

public class DocBean {

	private String DOCID;
	private String FILENAME;
	private String FILEREALPATH;
	private String FILESTOREPATH;
	private String FILEYEAR;
	private String FILEMONTH;
	private String UUID;
	
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getDOCID() {
		return DOCID;
	}
	public void setDOCID(String dOCID) {
		DOCID = dOCID;
	}
	public String getFILENAME() {
		return FILENAME;
	}
	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}
	public String getFILEREALPATH() {
		return FILEREALPATH;
	}
	public void setFILEREALPATH(String fILEREALPATH) {
		FILEREALPATH = fILEREALPATH;
	}
	public String getFILESTOREPATH() {
		return FILESTOREPATH;
	}
	public void setFILESTOREPATH(String fILESTOREPATH) {
		FILESTOREPATH = fILESTOREPATH;
	}
	public String getFILEYEAR() {
		return FILEYEAR;
	}
	public void setFILEYEAR(String fILEYEAR) {
		FILEYEAR = fILEYEAR;
	}
	public String getFILEMONTH() {
		return FILEMONTH;
	}
	public void setFILEMONTH(String fILEMONTH) {
		FILEMONTH = fILEMONTH;
	}
}
