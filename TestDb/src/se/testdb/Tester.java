/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * Times how long it takes to complete every test
 *************************************************************************/

package se.testdb;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class Tester {
	private TestDb currentTest; //Current database to test
	private DummyData dd;
	
	public Tester(TestDb t, DummyData dd) {
		currentTest = t;
		this.dd = dd;
	}
	
	public double testInsertAll(){		
		double startTime = System.currentTimeMillis();

		for (Module m : dd.modules){
			currentTest.insert(m);
		}		
		for (Bed b : dd.beds){
			currentTest.insert(b);
		}		
		for (Patient p : dd.patients){
			currentTest.insert(p);
		}		
		for (Parameter p : dd.parameters){
			currentTest.insert(p);
		}		
		for (Data d : dd.data){
			currentTest.insert(d);
		}
		double endTime = System.currentTimeMillis() - startTime;		
		return endTime;
	}
	
	//Times how long it takes to get parameters
	public SimpleEntry<Double, Boolean> testGetParameters(){
		double startTime = System.currentTimeMillis();
		List<Parameter> params = currentTest.getParameters(Integer.parseInt(dd.data.get(0).bed));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(params, dd.parameters, Parameter.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to get PatientData
	public SimpleEntry<Double, Boolean> testGetPatientData(){
		double startTime = System.currentTimeMillis();
		List<Data> d = currentTest.getPatientData(Integer.parseInt(dd.data.get(0).bed));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);		
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to get ParameterData
	public SimpleEntry<Double, Boolean> testGetParamData(){		
		double startTime = System.currentTimeMillis();
		List<Data> d = currentTest.getParamData(dd.data.get(0).parameterId, dd.data.get(0).bed);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to get all Patients by searching for name
	public SimpleEntry<Double, Boolean> testGetPatientsByName(){		
		double startTime = System.currentTimeMillis();
		List<Patient> patient = currentTest.getPatientsByName(dd.patients.get(0).name);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(patient, dd.patients, Patient.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to get all Patients that is in a specifc module
	public SimpleEntry<Double, Boolean> testGetPatientsByModule(){		
		double startTime = System.currentTimeMillis();
		List<Patient> patient = currentTest.getPatientsForModule(dd.modules.get(0).id);		
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(patient, dd.patients, Patient.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}

	//Times how long it takes to get all beds that is in a specifc module
	public SimpleEntry<Double, Boolean> testGetBedsForModule(){
		double startTime = System.currentTimeMillis();
		List<Bed> beds = currentTest.getBedsForModule(dd.beds.get(0).module);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(beds, dd.beds, Bed.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to get all modules
	public SimpleEntry<Double, Boolean> testGetModules(){
		double startTime = System.currentTimeMillis();
		List<Module> modules = currentTest.getModules();
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(modules, dd.modules, Module.class);
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}

	//Times how long it takes to get all data between two times
	public SimpleEntry<Double, Boolean> testGetDataFromTimespan(){
		double startTime = System.currentTimeMillis();
		List<Data> data = currentTest.getDataFromTimeSpan(dd.data.get(0).bed, Long.MIN_VALUE, Long.MAX_VALUE);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(data, dd.data, Data.class);		
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}	

	//Times how long it takes to get all data between two times given a specific parameter
	public SimpleEntry<Double, Boolean> testParameterDataFromTimespan(){
		double startTime = System.currentTimeMillis();
		List<Data> data = currentTest.getParameterDataFromTimeSpan(dd.data.get(0).bed, dd.data.get(0).parameterId, Long.MIN_VALUE, Long.MAX_VALUE);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(data, dd.data, Data.class);		
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to add a new field to the data collection
	public SimpleEntry<Double, Boolean> testAddFieldToData(){
		double startTime = System.currentTimeMillis();
		boolean valid = currentTest.addFieldToData("Data", "NewValue");
		double endTime = System.currentTimeMillis() - startTime;
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Times how long it takes to update values in the data collection
	public SimpleEntry<Double, Boolean> testUpdateValueForData(){
		double startTime = System.currentTimeMillis();
		boolean valid = currentTest.updateValueForData("Data", "newValue");
		double endTime = System.currentTimeMillis() - startTime;
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}

	//Times how long it takes to remove a field from the data collection
	public SimpleEntry<Double, Boolean> testRemoveFieldForData(){
		double startTime = System.currentTimeMillis();
		boolean valid = currentTest.removeFieldForData("Data", "NewValue");
		double endTime = System.currentTimeMillis() - startTime;
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	//Compares the result data with the generated dummydata
	private boolean validate(List<?> resultData, List<?> dummyData, Class c){
		int foundIdCounter = 0;
		try {
			Field fieldId = c.getField("id");			
			for(Object resElem : resultData){
				for(Object dummyElem : dummyData){
					if(fieldId.get(resElem).equals(fieldId.get(dummyElem))){						
						foundIdCounter += 1;
						for(Field f : c.getFields()){							
							if(!f.get(resElem).equals(f.get(dummyElem))){
								return false;
							}
						}
						break;
					}					
				}
			}
		} catch (Exception e) {			
			System.out.println(e.toString());			
			return false;
		}
		return foundIdCounter == resultData.size(); 
	}
}
