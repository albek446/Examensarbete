package se.testdb;

import java.text.ParseException;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class Main {
	public static void main(String args[]) throws ParseException{
		System.out.println("EAV-tests");
		runTests(new Tester(new EAV_DB()));
		System.out.println("Relation-tests");
		runTests(new Tester(new Relational_DB()));
	}
	
	private static void runTests(Tester tester) {
		double insertTime = tester.testInsertAll(10);
		System.out.println("InsertTime: " + insertTime);
		
		HashMap<String, SimpleEntry<Double, Boolean>> results = new HashMap<>();
		results.put("getParamResult", tester.testGetParameters());
		results.put("getPatientDataResult", tester.testGetPatientData());
		results.put("getParamDataResult", tester.testGetParamData());
		results.put("getPatientByNameResult", tester.testGetPatientsByName());
		
		for(String key : results.keySet()){
			System.out.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
		}
	}
}
