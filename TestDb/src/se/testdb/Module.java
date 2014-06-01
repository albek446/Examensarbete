/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * Represents information about a module
 *************************************************************************/

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
