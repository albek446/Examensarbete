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
import java.util.Map.Entry;

public class Relational_DB implements TestDb{

	private Connection con = null;
	
	private String url = "jdbc:mysql://localhost:3306/Exjobb";
	private String user = "user";
    private String password = "password";
    private List<String> dataTables;
    
    public Relational_DB(){
    	try {
    		dataTables = Arrays.asList("Data1", "Data2");
    		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con = DriverManager.getConnection(url, user, password);
			String clearEntriesQuery = "DELETE FROM";
    	    Statement stmt = con.createStatement();
    	    for (String table : dataTables){
    	    	stmt.executeUpdate(clearEntriesQuery + " " + table);
    	    }
    		stmt.executeUpdate(clearEntriesQuery+" Data");
    		stmt.executeUpdate("ALTER TABLE Data AUTO_INCREMENT = 1");
    		stmt.executeUpdate(clearEntriesQuery+" Patient");
    		stmt.executeUpdate(clearEntriesQuery+" Parameter");
    		stmt.executeUpdate("ALTER TABLE Parameter AUTO_INCREMENT = 1");
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		}
	}
    
    private String insertQuery(HashMap<String,Object> fields, String table, Object o){
    	if (fields.size() == 0)
    		return null;
    	String columnNames = "";
    	String argValues = "";
    	for(Entry<String,Object> e: fields.entrySet()) {  
    		if (e.getKey() == "id" && table != "Patient")
    			continue;
    		try {   			
    			columnNames += e.getKey() + ", ";
    			if(e.getValue().equals("LAST_INSERT_ID()"))
    				argValues += "" + e.getValue() + ", ";
    			else
    				argValues += "'" + e.getValue() + "', ";    			
			} catch (IllegalArgumentException er) {
				// TODO Auto-generated catch block
				er.printStackTrace();
			}
    	}
    	columnNames = columnNames.substring(0, columnNames.length()-2);
    	argValues = argValues.substring(0, argValues.length()-2);
    	
    	return "INSERT INTO "+table+"("+columnNames+") VALUES ("+argValues+")";
    }
    
    private void insertRow(String table, Object o)
    {    	
    	Field[] fields = o.getClass().getDeclaredFields();
    	HashMap<String,Object> f1 = new HashMap<String,Object>();
    	HashMap<String,Object> f2 = new HashMap<String,Object>();
    	String table2 = "Data2";
    	try {
	    	for(Field f : fields) {
	    		String name = f.getName();
	    		if (name.equals("value") && table.equals("Data")) {
	    			f2.put(name, f.get(o));	
	    			f2.put("dataId", "LAST_INSERT_ID()");
	    		}else {	    			
	    			f1.put(name, f.get(o));
	    		}		
	    	}
    	}
    	catch(Exception e) {
	    	e.printStackTrace();
    	}
    	String q1 = insertQuery(f1, table, o);
    	String q2 = null;
    	if(f2.size() != 0) {
    		if(f2.size() > 1) {
    			table2 = "Data1";
    		}
    		q2 = insertQuery(f2, table2, o);
    	}
    	
    	
    	try {    		
            Statement stmt = con.createStatement();
            stmt.execute(q1);
            if (q2 != null)
    			stmt.execute(q2);


    	} catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
	@Override
	public void insert(Parameter parameter) {
		insertRow("Parameter", parameter);
	}

	@Override
	public void insert(Data data) {		
		insertRow("Data", data);
	}

	@Override
	public void insert(Patient patient) {
		insertRow("Patient", patient);
	}
	
	private List get(String query , Class c){
		ResultSet rs = null;
		try {
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
		Field[] fields = c.getFields();
		try {			
			md = rs.getMetaData();		
			while (rs.next()){
				Object o = c.newInstance();
				//Boolean foundAttr = false;	
								
				for(int i=1;i<=md.getColumnCount();i++) {
					String col = md.getColumnName(i);
					Object val = rs.getObject(i);
					for (Field field : fields){						
						if(field.getName().equals(col)){
							if(field.getName().equals("id"))
								val = Integer.parseInt(val+"");
							else if(!field.getName().equals("date"))
								val = val+"";							
							field.set(o, val);
							break;
						}
						/*if(field.getName().equals(col)) {
							if(col.equals("patientId") || col.equals("parameterId")) {
								val = ""+val;
							}
							field.set(o, val);
							foundAttr = true;
							break;
						}*/
					}

					/*if(!foundAttr) {
						if(c == Data.class) {
							((Data)o).addProperties(col, val);
						}
					}*/	
				}
				list.add(o);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
		return list;
	}

	@Override
	public List<Parameter> getParameters(int id) {		
		String query = "SELECT * FROM Parameter"; 
		query += " WHERE id = ANY (";
		query +=" SELECT parameterId FROM Data";
        query += " WHERE patientId = " + id;
		query +=");";
		return get(query,Parameter.class);
	}

	@Override
	public List<Data> getPatientData(int id) {		
		String dataQuery = "SELECT Data.*, %s.value FROM Data";
		dataQuery += " LEFT JOIN %s";
		dataQuery += " ON Data.id = %s.dataId";
		dataQuery += " WHERE Data.patientId = " + id;
		dataQuery += " AND %s.value IS NOT NULL";
		List<Data> data = new ArrayList<>();
		for(String table : dataTables){
			data.addAll(get(String.format(dataQuery,table,table, table, table), Data.class));
		}
		return data;
	}

	@Override
	public List<Data> getParamData(Data data) {
		String dataQuery = "SELECT * FROM Data";
		dataQuery += " LEFT JOIN %s";
		dataQuery += " On Data.id = %s.dataId";
		dataQuery += " Where Data.patientId = " + data.patientId;
		dataQuery += " AND Data.parameterId = " + data.parameterId;
		dataQuery += " AND %s.value IS NOT NULL";
		List<Data> d = new ArrayList<>();
		for(String table : dataTables){
			d.addAll(get(String.format(dataQuery,table,table,table), Data.class));
		}
		return d;
	}

	@Override
	public List<Patient> searchPatientsByName(String name) {
		String query = "SELECT * FROM Patient WHERE name='"+name+"'";
		return get(query,Patient.class);
	}

	@Override
	public List<Patient> searchPatientsByModule(String module) {
		String query = "SELECT * FROM Patient WHERE module='"+module+"'";
		return get(query,Patient.class);
	}

}
