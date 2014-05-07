package se.testdb;

import java.text.DecimalFormat;
import java.util.Date;

public class Data {
	public int id;
	public long date;
	public String parameterId;
	public String bed;
	public Object value;
	
	public Data() {
	}
	
	public Data(int id, Date date, String paramId, String bed, Object value) {		
		this.id = id;
		this.date = date.getTime();
		this.parameterId = paramId;
		this.bed = bed;		
		this.value = new DecimalFormat(".###").format(value).replace(",", ".");
	}
	
	public void print(){		
		String s = "Data:\n";
		s +="Id: " + id;
		s+= " Date: " + date;
		s+= " ParamId: " + parameterId;
		s+= " Bed: " + bed;		
		if(value != null)
			s+= " Value: " + value.toString();
		else
			s+= " Value: NULL";
		System.out.println(s);
	}
}
