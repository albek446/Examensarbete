package se.testdb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class Main {
	private static String name;
	private static int modules;
	private static int beds;
	private static int patients;
	private static int parameters;
	private static int data;
	private static int[][] presets = {{4, 32, 14, 52, 161280},
									  {4, 32, 32, 52, 14376960},
									  {40, 320, 320, 52, 143769600}};	
		
	public static void main(String args[]) throws ParseException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter values: modules, beds, patients, parameters, data (for default values just write 0 for standard, 1 for worst case and 2 for worst case * 10)");
		String[] values = null;
		DummyData dd = new DummyData();
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
			
			System.out.println("Generating dummyData. This may take a while");
			dd.genDummyModules(modules);
			dd.genDummyBeds(beds);
			dd.genPatientDummyData(patients);
			dd.genParameterDummyData(parameters);
			dd.genDummyData(data);
			System.out.println("DummyData generated. Starting tests");
			
		} catch (Exception e) {
			System.out.println("Invalid input");
			return;
		}
		
		System.out.println("#####EAV-tests#####");
		name = "#####EAV-tests#####";
		runTests(new Tester(new EAV_DB(), dd));
		System.out.println("\n#####Relation-tests#####");
		name = "#####Relation-tests#####";
		runTests(new Tester(new Relational_DB(), dd));
		System.out.println("\n#####MongoDB-tests#####");
		name = "#####MongoDB-tests#####";
		runTests(new Tester(new MongoDB(), dd));
	}
	
	private static void runTests(Tester tester) {
		
		double insertTime = tester.testInsertAll();
		
		System.out.println("InsertTime: " + insertTime);
		
		HashMap<String, SimpleEntry<Double, Boolean>> results = new HashMap<>();
		results.put("getParamResult", tester.testGetParameters());		
		results.put("getPatientDataResult", tester.testGetPatientData());
		results.put("getParamDataResult", tester.testGetParamData());
		results.put("getPatientsByNameResult", tester.testGetPatientsByName());
		results.put("getPatientsByModuleResult", tester.testGetPatientsByModule());
		results.put("getBedsForModuleResult", tester.testGetBedsForModule());
		results.put("getModulesResult", tester.testGetModules());
		results.put("getDataFromTimespanResult", tester.testGetDataFromTimespan());
		results.put("addFieldToDataResult", tester.testAddFieldToData());
		results.put("updateValueForDataResult", tester.testUpdateValueForData());
		results.put("removeFieldForDataResult", tester.testRemoveFieldForData());
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(System.currentTimeMillis()+".log", "utf-8");
			writer.println(name);
			writer.println("InsertTime: " + insertTime);
			for(String key : results.keySet()){
				writer.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
				System.out.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
			}
		} catch (Exception e) {
			System.out.println("Could not Write to file: " + e.toString());
		} finally{
			if(writer != null)
				writer.close();
		}
	}
}
