package se.testdb;

import java.util.List;

public interface TestDb {
	void insert(Parameter parameter);
	void insert(Data data);
	void insert(Patient patient);
	void insert(Bed bed);
	void insert(Module module);
	List<Parameter> getParameters(int id);
	List<Data> getPatientData(int id);
	List<Data> getParamData(String parameterId, String bed);
	List<Patient> getPatientsByName(String name);
	List<Patient> getPatientsForModule(int id);
	List<Bed> getBedsForModule(String id);
	List<Module> getModules();
}
