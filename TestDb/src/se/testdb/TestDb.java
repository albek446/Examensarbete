/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * A contract that describe what functions shouldbe tested for every
 * database.
 *************************************************************************/

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
	List<Data> getDataFromTimeSpan(String bedId, long startTime, long endTime);
	List<Data> getParameterDataFromTimeSpan(String bedId, String parameterId, long startTime, long endTime);
	
	boolean addFieldToData(String entry, String field);
	boolean updateValueForData(String entry, String value);
	boolean removeFieldForData(String entry, String field);
}
