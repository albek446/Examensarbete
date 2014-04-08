package se.testdb;

public class Patient {
	public int id;
	public String name;
	public String socialSecurityNumber;
	public String sex;
	public String module;
	
	Patient(){}
	
	Patient(int id, String name, String ssnr, int sex, String module){		
		this.id = id;
		this.name = name;
		this.socialSecurityNumber = ssnr;
		this.sex = "" + sex;
		this.module = module;
	}
	
	public void print(){
		String a = "Patient:\n";
		a += "Id: " + id;
		a += " Name: " + name;
		a += " SocialSecurityNumber: " + socialSecurityNumber;
		a += " Module: " + module;		
		System.out.println(a);		
	}
}
