/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * Represents information about a bed
 *************************************************************************/

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
		String a = "Bed:\n";
		a += "Id: " + id;
		a += " Name: " + name;		
		a += " Module: " + module;		
		System.out.println(a);		
	}
}
