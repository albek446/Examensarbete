package se.testdb;

import java.util.List;

public class Tester {
	private TestDb currentTest;
	private List<Parameter> params;
	private List<Patient> patients;
	private List<Data> data;
	
	public Tester(TestDb t) {
		currentTest = t;
	}
	
	public double testInsertAll(int n){
		DummyData dd = new DummyData();
		params = dd.getParameterDummyData(n);
		patients = dd.getPatientDummyData(n);
		data = dd.getDummyData(patients, params, n);
		double startTime = System.currentTimeMillis();		
		for (Parameter p : params){
			currentTest.insert(p);
		}
		for (Patient p : patients){
			currentTest.insert(p);
		}
		for (Data d : data){
			currentTest.insert(d);
		}
		double endTime = System.currentTimeMillis() - startTime;		
		return endTime;
	}
	
	public double testGetParameters(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Parameter> params = currentTest.getParameters(patients.get(0));
		double endTime = System.currentTimeMillis() - startTime;		
		for (Parameter p : params)
			p.print();
		return endTime;
	}
	
	public double testGetPatientData(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Data> data = currentTest.getPatientData(patients.get(0));
		double endTime = System.currentTimeMillis() - startTime;
		for (Data d : data)
			d.print();
		return endTime;
	}
	
	public double testGetParamData(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		//Parameter param = dd.getParameterDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Data> data = currentTest.getParamData(params.get(0), patients.get(0));
		double endTime = System.currentTimeMillis() - startTime;
		for (Data d : data)
			d.print();
		return endTime;
	}
	
	public double testGetPatientByName(){
		//DummyData dd = new DummyData();
		//Patient patient = dd.getPatientDummyData(1).get(0);
		double startTime = System.currentTimeMillis();
		List<Patient> p = currentTest.searchPatientByName(patients.get(0).name);
		double endTime = System.currentTimeMillis() - startTime;
		return endTime;
	}
}
