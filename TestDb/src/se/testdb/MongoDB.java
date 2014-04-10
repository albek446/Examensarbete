package se.testdb;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoDB implements TestDb {

	private DB db;
	private HashMap<String,String> moduleIds; 
	private HashMap<String,String> bedIds; 
	private HashMap<String,String> patientIds; 
	private HashMap<String,String> parameterIds; 
	private HashMap<String,String> dataIds; 
	
	public MongoDB() {
		moduleIds = new HashMap<>();
		bedIds = new HashMap<>();
		patientIds = new HashMap<>();
		parameterIds = new HashMap<>();
		dataIds = new HashMap<>();
		
		//String username = "user";
		//String password = "password";
		try {
			MongoClient mongoClient = new MongoClient("localhost");
			
			db = mongoClient.getDB("Exjobb");
			db.dropDatabase();
			//boolean auth = db.authenicate(username, password);
			
			db.createCollection("module", new BasicDBObject());
			db.createCollection("parameter", new BasicDBObject());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void insert(Parameter p) {
		DBCollection coll = db.getCollection("parameters");
		ObjectId id = new ObjectId();		
		parameterIds.put(""+p.id, id.toString());
			
		DBObject parameter = new BasicDBObject("_id", id);		
		parameter.put("name",p.name);
		parameter.put("category", p.category);
		parameter.put("high", p.high);
		parameter.put("low", p.low);
		parameter.put("max", p.max);
		parameter.put("min", p.min);
		parameter.put("unit", p.unit);
		parameter.put("round", p.round);
		
		coll.save(parameter);
		
	}

	@Override
	public void insert(Data d) {
		DBCollection coll = db.getCollection("parameters");
		ObjectId id = new ObjectId();		
		dataIds.put(""+d.id, id.toString());
		
		BasicDBObject data = new BasicDBObject("_id", id);
		data.put("value", d.value);
		data.put("bedId", bedIds.get(d.bed));
		data.put("date", d.date);
		
		String findParameter = "{ '_id' : {'$oid' : '"+ parameterIds.get(d.parameterId)+"'}}";
		String updateParameter = "{'$push' : { 'data' : " + data.toString() +"}}";
		
		System.out.println(findParameter);
		System.out.println(updateParameter);
		
		BasicDBObject q1 = (BasicDBObject)JSON.parse(findParameter);
		BasicDBObject q2 = (BasicDBObject)JSON.parse(updateParameter);
		
		coll.update(q1, q2);
	}

	@Override
	public void insert(Patient p) {
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();
		patientIds.put(""+p.id, id.toString());
		
		String findModuleQuery = "{'beds' : {'$elemMatch' : { '_id' : {'$oid' : '" + bedIds.get(p.bed) + "'}}}}";
		DBObject query = (DBObject)JSON.parse(findModuleQuery);		
		DBObject data = coll.findOne(query);
		ObjectId moduleId = (ObjectId)data.get("_id");
		
		DBObject patient = new BasicDBObject("_id", id);		
		patient.put("name", p.name);
		patient.put("sex", p.sex);
		patient.put("socialSecurityNumber", p.socialSecurityNumber);		
		
		String qStr1 = "{'_id' : {$oid : '"+moduleId.toString()+"'}, 'beds._id' : {$oid :'"+bedIds.get(p.bed)+"'} }";
		DBObject q1 = (DBObject) JSON.parse(qStr1);
		String qStr2 = "{'$set' : { 'patient' : " + patient.toString() + "}}";
		DBObject q2 = (DBObject) JSON.parse(qStr2);

		coll.update(q1, q2);		
	}

	@Override
	public void insert(Module m) {		
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();		
		moduleIds.put(""+m.id, id.toString());
		
		DBObject module = new BasicDBObject("_id", id);		
		module.put("name",m.name);
		coll.save(module);
	}
	
	@Override
	public void insert(Bed b) {
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();
		bedIds.put(""+b.id, id.toString());
		DBObject bed = new BasicDBObject("_id", id);		
		bed.put("name", b.name);
		
		BasicDBObject matchId = new BasicDBObject();		
		matchId.put("_id", new ObjectId(moduleIds.get(b.module)));
		
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("beds", bed);
		
		BasicDBObject updateField = new BasicDBObject();
		updateField.put("$push", updateObject);
		
		coll.update(matchId, updateField);
	}


	@Override
	public List<Parameter> getParameters(int id) {
		return null;
	}

	@Override
	public List<Data> getPatientData(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Data> getParamData(String parameterId, String bed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> getPatientsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Patient> getPatientsForModule(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bed> getBedsForModule(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Module> getModules() {
		// TODO Auto-generated method stub
		return null;
	}

}
