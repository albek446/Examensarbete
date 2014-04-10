package se.testdb;

public class Patient {
	public int id;
	public String bed;
	public String name;
	public String socialSecurityNumber;
	public String sex;
	
	Patient(){}
	
	Patient(int id, String bed, String name, String ssnr, int sex){		
		this.id = id;
		this.bed = bed;
		this.name = name;
		this.socialSecurityNumber = ssnr;
		this.sex = "" + sex;
	}
	
	public void print(){
		String a = "Patient:\n";
		a += "Id: " + id;
		a += "Bed: " + bed;
		a += " Name: " + name;
		a += " SocialSecurityNumber: " + socialSecurityNumber;	
		System.out.println(a);		
	}
}
