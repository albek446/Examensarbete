package se.testdb;

public class Module {
	public int id;
	public String name;
	
	Module(){}
	
	Module(int id, String name){		
		this.id = id;
		this.name = name;
	}
	
	public void print(){
		String a = "Patient:\n";
		a += "Id: " + id;
		a += " Name: " + name;			
		System.out.println(a);		
	}
}
