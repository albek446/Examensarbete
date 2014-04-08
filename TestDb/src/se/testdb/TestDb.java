package se.testdb;

import java.util.List;

public interface TestDb {
	void insert(Parameter parameter);
	void insert(Data data);
	void insert(Patient patient);
	List<Parameter> getParameters(Patient patient);
	List<Data> getPatientData(Patient patient);
	List<Data> getParamData(Parameter parameter, Patient patient);
	List<Patient> searchPatientByName(String name);
}
