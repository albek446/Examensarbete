package se.testdb;

import java.util.Date;

public class Data {
	public int id;
	public long date;
	public String parameterId;
	public String patientId;
	public Object value;
	
	public Data() {
	}
	
	public Data(int id, Date date, String paramId, String patientId, Object value) {		
		this.id = id;
		this.date = date.getTime();
		this.parameterId = paramId;
		this.patientId = patientId;
		this.value = value;
	}
	
	public void print(){		
		String s = "Data:\n";
		s +="Id: " + id;
		s+= " Date: " + date;
		s+= " ParamId: " + parameterId;
		s+= " PatientId: " + patientId;
		//s+= " Value: " + value.toString();
		System.out.println(s);
	}
}
