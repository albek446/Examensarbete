/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * Represents information about a parameter
 *************************************************************************/

package se.testdb;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Parameter {
	public int id;
	public String round;
	public String unit;
	public String name;
	public String category;
	public String min;
	public String max;
	public String low;
	public String high;
	
	Parameter() {}
	
	Parameter(int id, String cat, String name, String round, String unit, float min, float max, float low, float high){		
		this.id = id;
		this.category = cat;
		this.name = name;
		this.round = round;
		this.unit = unit;
		this.min = new DecimalFormat("#.###").format(min).replace(",", ".");		
		this.max = this.min;
		this.low = this.min;
		this.high = this.min;
	}
	
	public void print(){
		String a = "Parameter:\n";
		a += "Id: " + id;
		a += " Round: " + round;
		a += " unit: " + unit;
		a += " Name: " + name;
		a += " Category: " + category;
		a += " min: " + min;
		a += " max: " + max;
		a += " low: " + low;		
		a += " high: " + high;		
		System.out.println(a);		
	}
}
