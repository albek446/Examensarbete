package se.testdb;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EAV_DB implements TestDb{
	
	private Connection con = null;
	
	private String url = "jdbc:mysql://localhost:3306/EAV";
	private String user = "user";
    private String password = "password";
    
    private String insertQuery = "INSERT INTO %s(id, attribute, value) VALUES(%s,'%s','%s')";
    private String selectQuery = "SELECT %s FROM %s";
	
    public EAV_DB() {
    	try {    		
    		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con = DriverManager.getConnection(url, user, password);
			String clearEntriesQuery = "DELETE FROM";
    	    Statement stmt = con.createStatement();    	    
    		stmt.executeUpdate(clearEntriesQuery+" DataTime");
    		stmt.executeUpdate(clearEntriesQuery+" Data");
    		stmt.executeUpdate(clearEntriesQuery+" Patient");
    		stmt.executeUpdate(clearEntriesQuery+" Parameter");
    		
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
	}
    
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
		insertRow("Patient", patient.id + "", "id", patient.id + "");
		insertRow("Patient", patient.id + "", "name", patient.name);
		insertRow("Patient", patient.id + "", "socialSecurityNumber", patient.socialSecurityNumber);
		insertRow("Patient", patient.id + "", "sex", patient.sex + "");
		insertRow("Patient", patient.id + "", "module", patient.module);
	}
	
	@Override
	public void insert(Data data) {
		insertRow("Data", data.id + "", "parameterId", data.parameterId + "");
		insertRow("Data", data.id + "", "patientId", data.patientId + "");
		insertRow("Data", data.id + "", "value", data.value + "");
		try {			
    		String query = "INSERT INTO DataTime(dataId, date) VALUES(" + data.id + ", " + data.date + ")";
            Statement stmt = con.createStatement();
    		stmt.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
	}

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
	
	@SuppressWarnings("rawtypes")
	private List resultSetToArrayList(ResultSet rs, Class c){
		ResultSetMetaData md;		
		ArrayList list = new ArrayList();	
		int currId = 0;
		int i;
		Field[] fields = c.getFields();
		try {			
			Object o = c.newInstance();
			md = rs.getMetaData();
			boolean hasData = false;
			while (rs.next()){
				hasData = true;
				//New object for each new ID in result
				if((i = (int)rs.getObject(1)) != currId){
					//list.add(o);

					o = c.newInstance();
					currId = i;
					Field field = c.getField("id");
					field.set(o, currId);
				}
				//Attribute is always string
				Boolean foundAttr = false;
				String attribute = (String)rs.getObject(2);
				for (Field field : fields){					
					if(field.getName().equals(attribute))
						field.set(o, rs.getObject(3));
					/*if(field.getName().equals(attribute)) {
						field.set(o, rs.getObject(3));
						foundAttr = true;
						break;
					}*/
				}
				/*if(!foundAttr) {
					if(c == Data.class) {
						((Data)o).addProperties(attribute, rs.getObject(3));
					}
				}*/	
			}
			if(hasData)
				list.add(o);
		} catch (SQLException e) {
			System.out.println(e.toString());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<Parameter> getParameters(Patient patient) {
		String where = " WHERE id = ANY (SELECT b.value FROM Data a, Data b WHERE a.attribute = 'patientId' AND a.value = " + patient.id + " AND b.id = a.id AND b.attribute = 'parameterId')";		
		return get("*", "Parameter", where, Parameter.class);
	}

	@Override
	public List<Data> getPatientData(Patient patient) {
		String where = " WHERE id = ANY (SELECT id FROM Data WHERE attribute = 'patientId' and value = " + patient.id + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);
	}

	@Override
	public List<Data> getParamData(Parameter parameter, Patient patient) {		
		String where = " WHERE id = ANY (SELECT id FROM Data WHERE attribute = 'parameterId' and value = " + parameter.id + ") AND id = ANY (SELECT id FROM Data WHERE attribute = 'patientId' and value = " + patient.id + ")";
		return get("Data.*, DataTime.date", "DataTime LEFT JOIN Data ON DataTime.dataId = Data.id", where, Data.class);
	}

	@Override
	public List<Patient> searchPatientByName(String name) {
		String where = " WHERE attribute = 'name' AND value = '" + name + "'";
		return get("*", "Patient", where, Patient.class);
	}
	
}
