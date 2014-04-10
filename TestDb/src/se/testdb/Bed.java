package se.testdb;

public class Bed {
	public int id;
	public String name;
	public String module;
	
	Bed(){}
	
	Bed(int id, String name, String module){		
		this.id = id;
		this.name = name;
		this.module = module;
	}
	
	public void print(){
		String a = "Patient:\n";
		a += "Id: " + id;
		a += " Name: " + name;		
		a += " Module: " + module;		
		System.out.println(a);		
	}
}
