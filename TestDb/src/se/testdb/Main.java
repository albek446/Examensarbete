package se.testdb;

import java.text.ParseException;

public class Main {
	public static void main(String args[]) throws ParseException{
		runTests(new Tester(new EAV_DB()));
		//runTests(new Tester(new Relational_DB()));
	}
	
	private static void runTests(Tester tester) {
		double insertTime = tester.testInsertAll(10);
		//double getParamTime = tester.testGetParameters();
		//double getPatientDataTime = tester.testGetPatientData();
		double getParamDataTime = tester.testGetParamData();
		//double getPatientByNameTime = tester.testGetPatientByName();
		//System.out.println("InsertTime: " + insertTime);
		//System.out.println("GetParamTime: " + getParamTime);
		//System.out.println("GetPatientDataTime: " + getPatientDataTime);
		System.out.println("GetParamDataTime: " + getParamDataTime);
		//System.out.println("GetPatientByNameTime: " + getPatientByNameTime);
	}
}
