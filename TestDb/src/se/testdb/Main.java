/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * A program that tests how long it takes to retrieve data from different
 * databases. Also checks how long it takes to modify the databases
 * structure and content.
 *************************************************************************/

package se.testdb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class Main {
	private static int modules;
	private static int beds;
	private static int patients;
	private static int parameters;
	private static int data;
	private static int[][] presets = {{4, 32, 14, 20, 80640},
									    {4, 32, 32, 30, 921600},
									    {8, 64, 64, 30, 1843200}};
	//Prepare all tests
	public static void main(String args[]) throws ParseException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter values: modules, beds, patients, parameters, data (for default values just write 0 for standard, 1 for worst case and 2 for worst case * 2)");
		String[] values = null;
			
		//Handles input
		try {
			String input = br.readLine();
			if(input.length() > 1){
				values = input.split(", ");
				modules = Integer.parseInt(values[0]);
				beds = Integer.parseInt(values[1]);
				patients = Integer.parseInt(values[2]);
				parameters = Integer.parseInt(values[3]);
				data = Integer.parseInt(values[4]);
			}
			else if(input.length() == 1){
				int key = Integer.parseInt(input);
				modules = presets[key][0];
				beds = presets[key][1];
				patients = presets[key][2];
				parameters = presets[key][3];
				data = presets[key][4];
			}
			else{
				throw new NullPointerException();
			}
			
			System.out.println("Generating dummyData.");
			DummyData dd = new DummyData();
			dd.genDummyModules(modules);
			dd.genDummyBeds(beds);
			dd.genPatientDummyData(patients);
			dd.genParameterDummyData(parameters);
			dd.genDummyData(data);
			System.out.println("DummyData generated. Starting tests");
			
			System.out.println("#####EAV-tests#####");				 
			runTests(new Tester(new EAV_DB(), dd));
			System.out.println("\n#####Relation-tests#####");
			runTests(new Tester(new Relational_DB(), dd));
			System.out.println("\n#####MongoDB-tests#####");
			runTests(new Tester(new MongoDB(), dd));
			dd.reset();
			
		} catch (Exception e) {
			System.out.println("Invalid input");
			e.printStackTrace();
			return;
		}
	}
	
	//Run all tests
	private static void runTests(Tester tester) {
		
		System.out.println("Inserting data...");
		double insertTime = tester.testInsertAll();
		System.out.println("Data is inserted");
		
		HashMap<String, SimpleEntry<Double, Boolean>> results = new HashMap<>();
		System.out.println("Starting tests");
		results.put("1. getPatientsByNameResult", tester.testGetPatientsByName());
		results.put("2. getModulesResult", tester.testGetModules());
		results.put("3. getBedsForModuleResult", tester.testGetBedsForModule());
		results.put("4. getPatientsByModuleResult", tester.testGetPatientsByModule());
		results.put("5. getParamResult", tester.testGetParameters());
		results.put("6. getParamDataResult", tester.testGetParamData());
		results.put("7. getParamDataFromTimespanResult", tester.testParameterDataFromTimespan());
		results.put("8. getPatientDataResult", tester.testGetPatientData());
		results.put("9. getDataFromTimespanResult", tester.testGetDataFromTimespan());
		results.put("10. addFieldToDataResult", tester.testAddFieldToData());
		results.put("11. updateValueForDataResult", tester.testUpdateValueForData());
		results.put("12. removeFieldForDataResult", tester.testRemoveFieldForData());
		
		System.out.println("0. InsertTime: " + insertTime);
		for(String key : results.keySet()){
			System.out.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
		}		
	}
}
