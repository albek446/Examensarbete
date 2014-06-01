/*************************************************************************
 * Written by: Albin Ekberg and Jacob Holm
 * Contact Albin: albek446@student.liu.se
 * Contact Jacob: jacho391@student.liu.se
 * Last modified: 2014-06-01 
 * 
 * Testing a MySQL database that is using an EAV-design
 *************************************************************************/

package se.testdb;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EAV_DB implements TestDb{
	
	private Connection con = null;
	
	//private String url = "jdbc:mysql://130.236.188.168:3306/eavdb";
	//private String url = "jdbc:mysql://130.236.188.167:3306/eavdb";
	private String url = "jdbc:mysql://127.0.0.1:3306/eavdb";
	
	private String user = "user";
    private String password = "password";
    
    //Standard queries for insert and select statements
    private String insertQuery = "INSERT INTO %s(id, attribute, value) VALUES(%s,'%s','%s')";
    private String selectQuery = "SELECT %s FROM %s";
	
    //Clear database to prepare for new tests
    public EAV_DB() {
    	try {    		
    		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con = DriverManager.getConnection(url, user, password);
			String clearEntriesQuery = "DELETE FROM";
    	    Statement stmt = con.createStatement();    	    
    		stmt.executeUpdate(clearEntriesQuery+" DataTime");
    		stmt.executeUpdate(clearEntriesQuery+" Data");
    		stmt.executeUpdate(clearEntriesQuery+" Patient");
    		stmt.executeUpdate(clearEntriesQuery+" Module");
    		stmt.executeUpdate(clearEntriesQuery+" Bed");
    		stmt.executeUpdate(clearEntriesQuery+" Parameter");
    		
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
	}
    
    //Insert data into database
    private void insertRow(String table, String id, String attribute, String value){
    	try {
    		String query = String.format(insertQuery, table, id, attribute, value);
            Statement stmt = con.createStatement();            
    		stmt.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }		
    }
    
	@Override
	public void insert(Parameter parameter) {
		insertRow("Parameter", parameter.id + "", "name", parameter.name);
		insertRow("Parameter", parameter.id + "", "category", parameter.category);
		insertRow("Parameter", parameter.id + "", "round", parameter.round);
		insertRow("Parameter", parameter.id + "", "unit", parameter.unit);	
		insertRow("Parameter", parameter.id + "", "min", parameter.min + "");
		insertRow("Parameter", parameter.id + "", "max", parameter.max + "");
		insertRow("Parameter", parameter.id + "", "low", parameter.low + "");
		insertRow("Parameter", parameter.id + "", "high", parameter.high + "");
	}

	@Override
	public void insert(Patient patient) {		
		insertRow("Patient", patient.id + "", "name", patient.name);
		insertRow("Patient", patient.id + "", "socialSecurityNumber", patient.socialSecurityNumber);
		insertRow("Patient", patient.id + "", "sex", patient.sex + "");
		insertRow("Patient", patient.id + "", "bed", patient.bed);
	}
	
	@Override
	public void insert(Data data) {
		insertRow("Data", data.id + "", "parameterId", data.parameterId + "");
		insertRow("Data", data.id + "", "bed", data.bed + "");
		insertRow("Data", data.id + "", "value", data.value + "");
		try {			
    		String query = "INSERT INTO DataTime(dataId, date) VALUES(" + data.id + ", " + data.date + ")";
            Statement stmt = con.createStatement();
    		stmt.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
	}
	
	@Override
	public void insert(Bed bed) {		
		insertRow("Bed", bed.id+"", "module", bed.module+"");		
		insertRow("Bed", bed.id+"", "name", bed.name);		
	}
	
	@Override
	public void insert(Module module) {
		insertRow("Module", module.id+"", "name", module.name);
	}

	//Gets data from database
	private List get(String select, String table, String where, Class c){
		ResultSet rs = null;
		try {
    		String query = String.format(selectQuery + where, select, table);
            Statement stmt = con.createStatement();            
    		rs = stmt.executeQuery(query);    		
        } catch (SQLException ex) {        	
            System.out.println(ex.toString());
        }
		return resultSetToArrayList(rs, c);
	}
	
	//Converts the SQL result to an Java Array
	@SuppressWarnings("rawtypes")
	private List resultSetToArrayList(ResultSet rs, Class c){		
		ArrayList list = new ArrayList();	
		int currId = 0;
		int i;
		Field[] fields = c.getFields();
		try {			
			Object o = c.newInstance();			
			boolean hasData = false;
			while (rs.next()){				
				if((i = (int)rs.getObject(1)) != currId){
					hasData = true;					
					o = c.newInstance();
					currId = i;
					Field field = c.getField("id");
					field.set(o, currId);
				}
				//Attribute is always string
				String attribute = (String)rs.getObject(2);				
				for (Field field : fields){
					if(field.getName().equals(attribute)) {
						field.set(o, rs.getObject(3));
						break;
					}
					else if(field.getName().equals("date")) {
						field.set(o, rs.getObject(4));
					}
				}
				if(hasData){
					hasData = false;
					list.add(o);
				}
			}
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Parameter> getParameters(int id) {
		String where = " WHERE id = ANY (SELECT value FROM Data WHERE attribute = 'parameterId' AND id = ANY (SELECT id FROM Data WHERE attribute = 'bed' AND value = " + id + "))";			
		return get("*", "Parameter", where, Parameter.class);
	}

	@Override
	public List<Data> getPatientData(int id) {		
		String where = " WHERE Data.id = ANY (SELECT id FROM Data WHERE attribute = 'bed' and value = " + id + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);
	}

	@Override
	public List<Data> getParamData(String parameterId, String bedId) {
		String where = " WHERE id = ANY (SELECT id FROM Data WHERE attribute = 'parameterId' and value = " + parameterId + ") AND id = ANY (SELECT id FROM Data WHERE attribute = 'bed' and value = " + bedId + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);
	}

	@Override
	public List<Patient> getPatientsByName(String name) {
		String where = " WHERE id = ANY (SELECT id FROM Patient WHERE attribute = 'name' AND value = '" + name + "')";
		return get("*", "Patient", where, Patient.class);
	}

	@Override
	public List<Patient> getPatientsForModule(int id) {
		String where = " WHERE id = ANY (SELECT id FROM Patient WHERE attribute = 'bed' AND value = ANY (SELECT id FROM Bed WHERE attribute = 'module' AND value = '" + id + "'))";
		return get("*", "Patient", where, Patient.class);		
	}

	@Override
	public List<Bed> getBedsForModule(String id) {
		String where = " WHERE id = ANY (SELECT id FROM Bed WHERE attribute = 'module' AND value = '" + id + "')";
		return get("*", "Bed", where, Bed.class);
	}

	@Override
	public List<Module> getModules() {
		String where = "";
		return get("*", "Module", where, Module.class);
	}

	@Override
	public List<Data> getDataFromTimeSpan(String bedId, long startTime, long endTime) {		
		String where = " WHERE DataTime.date >= " + startTime;
		where += " AND DataTime.date <= " + endTime;
		where += " AND Data.id = ANY (SELECT id FROM Data WHERE attribute = 'bed' and value = " + bedId + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);		
	}

	@Override
	public List<Data> getParameterDataFromTimeSpan(String bedId, String parameterId, long startTime, long endTime) {
		String where = " WHERE DataTime.date >= " + startTime;
		where += " AND DataTime.date <= " + endTime;
		where += " AND Data.id = ANY (SELECT id FROM Data WHERE attribute = 'bed' and value = " + bedId + ") AND id = ANY (SELECT id FROM Data WHERE attribute = 'parameterId' and value = " + parameterId + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);	
	}
	
	@Override
	public boolean addFieldToData(String entry, String field) {
		return true; //Nothing has to be done here for EAV
	}

	@Override
	public boolean updateValueForData(String entry, String value) {		
		String query = "UPDATE " + entry + " SET value = '" + value + "' WHERE attribute = 'value'";
		try {
    	    Statement stmt = con.createStatement();
    		stmt.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        }
		return true;
	}

	@Override
	public boolean removeFieldForData(String entry, String field) {
		String query = "DELETE FROM " + entry + " WHERE attribute = 'value'";
		try {
    	    Statement stmt = con.createStatement();
    		stmt.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        }
		return true;
	}
}
