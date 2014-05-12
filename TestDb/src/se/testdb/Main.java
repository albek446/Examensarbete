package se.testdb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

public class Main {
	private static String name;
	private static int modules;
	private static int beds;
	private static int patients;
	private static int parameters;
	private static int data;
	private static int[][] presets = {{4, 32, 14, 20, 80640},
									  {4, 32, 32, 30, 921600},
									  {8, 64, 64, 30, 1843200}};
		
	public static void main(String args[]) throws ParseException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter values: modules, beds, patients, parameters, data (for default values just write 0 for standard, 1 for worst case and 2 for worst case * 10)");
		String[] values = null;
		
			
		/*try {
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
		}*/		
		
		try{
			DummyData dd = new DummyData();
			for(int i = 0; i < 1; i++){
				for(int j = 0; j < 1; j++){
					modules = presets[i][0];
					beds = presets[i][1];
					patients = presets[i][2];
					parameters = presets[i][3];
					data = presets[i][4];
					
					dd.genDummyModules(modules);
					dd.genDummyBeds(beds);
					dd.genPatientDummyData(patients);
					dd.genParameterDummyData(parameters);
					dd.genDummyData(data);
				
					/*System.out.println("Test " + i + "-" + j + " #####EAV-tests#####");				 
					name = "Test " + i + "-" + j + " #####EAV-tests#####";
					runTests(new Tester(new EAV_DB(), dd));
					System.out.println("\nTest " + i + "-" + j + " #####Relation-tests#####");
					name = "Test " + i + "-" + j + " #####Relation-tests#####";
					runTests(new Tester(new Relational_DB(), dd));*/
					System.out.println("\nTest " + i + "-" + j + " #####MongoDB-tests#####");
					name = "Test " + i + "-" + j + " #####MongoDB-tests#####";
					runTests(new Tester(new MongoDB(), dd));
					dd.reset();
					
					Runtime rt = Runtime.getRuntime();
					System.out.println("Free: " + rt.freeMemory());
					System.out.println("Total: " + rt.totalMemory());
					System.out.println("Used: " + (rt.totalMemory() - rt.freeMemory()));
				}
			}
		}catch(Exception e){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));
			Runtime rt = Runtime.getRuntime();
			System.out.println("SOME ERROR OCCURED");
			System.out.println(e.toString());
			System.out.println("Free: " + rt.freeMemory());
			System.out.println("Total: " + rt.totalMemory());
			System.out.println("Used: " + (rt.totalMemory() - rt.freeMemory()));
		}
	}
	
	private static void runTests(Tester tester) {
		
		double insertTime = tester.testInsertAll();
		
		System.out.println("0. InsertTime: " + insertTime);
		
		HashMap<String, SimpleEntry<Double, Boolean>> results = new HashMap<>();
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
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(System.currentTimeMillis()+".log", "utf-8");
			writer.println(name);
			writer.println("InsertTime: " + insertTime);
			for(String key : results.keySet()){
				writer.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
				//System.out.println(key + ": Time:" + results.get(key).getKey() + " Valid: " + results.get(key).getValue());
			}
		} catch (Exception e) {
			System.out.println("Could not Write to file: " + e.toString());
		} finally{
			if(writer != null)
				writer.close();
		}
	}
}
