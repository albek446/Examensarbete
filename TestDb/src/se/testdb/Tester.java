package se.testdb;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Tester {
	private TestDb currentTest;
	private DummyData dd;
	
	public Tester(TestDb t) {
		currentTest = t;
		dd = new DummyData();
	}
	
	public double testInsertAll(int n){		
		double startTime = System.currentTimeMillis();

		dd.genDummyModules(n);
		for (Module m : dd.modules){
			currentTest.insert(m);
		}
		dd.genDummyBeds(n);
		for (Bed b : dd.beds){
			currentTest.insert(b);
		}
		dd.genPatientDummyData(n);
		for (Patient p : dd.patients){
			currentTest.insert(p);
		}
		dd.genParameterDummyData(n);	
		for (Parameter p : dd.parameters){
			currentTest.insert(p);
		}
		dd.genDummyData(n);
		for (Data d : dd.data){
			currentTest.insert(d);
		}
		double endTime = System.currentTimeMillis() - startTime;		
		return endTime;
	}
	
	public SimpleEntry<Double, Boolean> testGetParameters(){
		double startTime = System.currentTimeMillis();
		List<Parameter> params = currentTest.getParameters(Integer.parseInt(dd.data.get(0).bed));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(params, dd.parameters, Parameter.class);		
		//for (Parameter p : params)
		//	p.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetPatientData(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Data> d = currentTest.getPatientData(Integer.parseInt(dd.data.get(2).bed));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);
		//for (Data d2 : d)
		//	d2.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetParamData(){		
		double startTime = System.currentTimeMillis();
		List<Data> d = currentTest.getParamData(dd.data.get(0).parameterId, dd.data.get(0).bed);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);
		//for (Data d2 : d)
		//	d2.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetPatientsByName(){		
		double startTime = System.currentTimeMillis();
		List<Patient> patient = currentTest.getPatientsByName(dd.patients.get(0).name);
		//List<Patient> patient = currentTest.searchPatientByName("YOLOMACSWAGGINGS");
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(patient, dd.patients, Patient.class);
		//for (Patient p : patient)
		//	p.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetPatientsByModule(){		
		double startTime = System.currentTimeMillis();
		List<Patient> patient = currentTest.getPatientsForModule(dd.modules.get(0).id);		
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(patient, dd.patients, Patient.class);
		//for (Patient p : patient)
		//	p.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetBedsForModule(){
		double startTime = System.currentTimeMillis();
		List<Bed> beds = currentTest.getBedsForModule(dd.beds.get(0).module);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(beds, dd.beds, Bed.class);
		//for (Bed b : beds)
		//	b.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetModules(){
		double startTime = System.currentTimeMillis();
		List<Module> modules = currentTest.getModules();
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(modules, dd.modules, Module.class);
		//for (Module m : modules)
		//	m.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetDataFromTimespan(){
		double startTime = System.currentTimeMillis();
		List<Data> data = currentTest.getDataFromTimeSpan(dd.data.get(2).bed, Long.MIN_VALUE, Long.MAX_VALUE);
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(data, dd.data, Data.class);
		for (Data d : data)
			d.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}	
	
	private boolean validate(List<?> resultData, List<?> dummyData, Class c){
		int foundIdCounter = 0;
		try {
			Field fieldId = c.getField("id");			
			for(Object resElem : resultData){
				for(Object dummyElem : dummyData){
					//System.out.println(fieldId.get(resElem) + " - " + fieldId.get(dummyElem));
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
