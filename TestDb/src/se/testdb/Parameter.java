package se.testdb;

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
		this.min = min + "";
		this.max = max + "";
		this.low = low + "";
		this.high = high + "";
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
