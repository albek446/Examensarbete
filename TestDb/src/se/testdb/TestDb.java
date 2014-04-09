package se.testdb;

import java.util.List;

public interface TestDb {
	void insert(Parameter parameter);
	void insert(Data data);
	void insert(Patient patient);
	List<Parameter> getParameters(int id);
	List<Data> getPatientData(int id);
	List<Data> getParamData(Data data);
	List<Patient> searchPatientsByName(String name);
	List<Patient> searchPatientsByModule(String module);
}
