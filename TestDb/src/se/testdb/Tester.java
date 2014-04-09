package se.testdb;

import java.lang.reflect.Field;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
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
		dd.getParameterDummyData(n);
		dd.getPatientDummyData(n);
		dd.getDummyData(dd.patients, dd.parameters, n);
		double startTime = System.currentTimeMillis();		
		for (Parameter p : dd.parameters){
			currentTest.insert(p);
		}
		for (Patient p : dd.patients){
			currentTest.insert(p);
		}
		for (Data d : dd.data){
			currentTest.insert(d);
		}
		double endTime = System.currentTimeMillis() - startTime;		
		return endTime;
	}
	
	public SimpleEntry<Double, Boolean> testGetParameters(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Parameter> params = currentTest.getParameters(Integer.parseInt(dd.data.get(0).patientId));
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
		List<Data> d = currentTest.getPatientData(Integer.parseInt(dd.data.get(2).patientId));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);
		//for (Data d2 : d)
		//	d2.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetParamData(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		//Parameter param = dd.getParameterDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Data> d = currentTest.getParamData(dd.data.get(0));
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(d, dd.data, Data.class);
		//for (Data d2 : d)
		//	d2.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	public SimpleEntry<Double, Boolean> testGetPatientsByName(){		
		double startTime = System.currentTimeMillis();
		List<Patient> patient = currentTest.searchPatientsByName(dd.patients.get(0).name);
		//List<Patient> patient = currentTest.searchPatientByName("YOLOMACSWAGGINGS");
		double endTime = System.currentTimeMillis() - startTime;
		boolean valid = validate(patient, dd.patients, Patient.class);
		//for (Patient p : patient)
		//	p.print();
		return new SimpleEntry<Double, Boolean>(endTime, valid);
	}
	
	private boolean validate(List<?> resultData, List<?> dummyData, Class c){
		try {
			Field fieldId = c.getField("id");
			for(Object resElem : resultData){
				for(Object dummyElem : dummyData){
					if(fieldId.get(resElem) == fieldId.get(dummyElem)){
						for(Field f : c.getFields()){
							if(f.get(resElem) != f.get(dummyElem)){
								return false;
							}
						}
					}
				}
			}
		} catch (Exception e) {			
			System.out.println(e.toString());			
			return false;
		}
		return true;
	}
}
