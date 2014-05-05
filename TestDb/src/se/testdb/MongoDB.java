package se.testdb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
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
			//MongoClient mongoClient = new MongoClient("130.236.188.168");
			MongoClient mongoClient = new MongoClient("130.236.188.167");			
			
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
		parameterIds.put(id.toString(),""+p.id);
			
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
		dataIds.put(id.toString(), ""+d.id);
		
		BasicDBObject data = new BasicDBObject("_id", id);
		data.put("value", d.value);
		data.put("bedId", new ObjectId(bedIds.get(d.bed)));
		data.put("date", d.date);
		
		String findParameter = "{ '_id' : {'$oid' : '"+ parameterIds.get(d.parameterId)+"'}}";
		String updateParameter = "{'$push' : { 'data' : " + data.toString() +"}}";
		
		//System.out.println(findParameter);
		//System.out.println(updateParameter);
		
		BasicDBObject q1 = (BasicDBObject)JSON.parse(findParameter);
		BasicDBObject q2 = (BasicDBObject)JSON.parse(updateParameter);
		
		coll.update(q1, q2);
	}

	@Override
	public void insert(Patient p) {
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();
		patientIds.put(""+p.id, id.toString());
		patientIds.put(id.toString(), ""+p.id);
		
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
		String qStr2 = "{'$set' : { 'beds.$.patient' : " + patient.toString() + "}}";
		DBObject q2 = (DBObject) JSON.parse(qStr2);

		coll.update(q1, q2);		
	}

	@Override
	public void insert(Module m) {		
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();		
		moduleIds.put(""+m.id, id.toString());
		moduleIds.put(id.toString(), ""+m.id);
		
		DBObject module = new BasicDBObject("_id", id);		
		module.put("name",m.name);
		coll.save(module);
	}
	
	@Override
	public void insert(Bed b) {
		DBCollection coll = db.getCollection("modules");
		ObjectId id = new ObjectId();
		bedIds.put(""+b.id, id.toString());
		bedIds.put(id.toString(), ""+b.id);
		
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
		BasicDBObject query = new BasicDBObject();	
		BasicDBObject field = new BasicDBObject();			
		query.put("data.bedId", new ObjectId(bedIds.get(id+"")));
		field.put("data", 0);
		DBCursor cursor = db.getCollection("parameters").find(query, field);
		List<Parameter> result = new ArrayList<>();
		while (cursor.hasNext()) {			
			BasicDBObject obj = (BasicDBObject) cursor.next();
	    	Parameter p = new Parameter();
	    	ObjectId paramId = (ObjectId)obj.get("_id");	    	
	    	p.id = Integer.parseInt(parameterIds.get(paramId.toString()));
	    	p.category = obj.getString("category");
	    	p.high = obj.getString("high");
	    	p.low = obj.getString("low");
	    	p.max = obj.getString("max");
	    	p.min = obj.getString("min");
	    	p.name = obj.getString("name");
	    	p.round = obj.getString("round");
	    	p.unit = obj.getString("unit");	    	
	    	
	    	result.add(p);	    	
	    }	
		
		return result;		
	}

	@Override
	public List<Data> getPatientData(int id) {
		String queryMatch = "{'data.bedId' : { $oid : '"+ bedIds.get(id+"") + "'}}";
		String queryFilter = "{'data' : {$elemMatch : {'bedId' : { $oid : '" + bedIds.get(id+"") + "'}}}}";		
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		BasicDBObject filter = (BasicDBObject)JSON.parse(queryFilter);
		DBCursor cursor = db.getCollection("parameters").find(match, filter);
		
		List<Data> result = new ArrayList<>();
		
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    ObjectId paramId = (ObjectId)obj.get("_id");
		    BasicDBList data = (BasicDBList)obj.get("data");	    
		    for (Object dataObj : data){
		    	BasicDBObject o = (BasicDBObject)dataObj;
		    	Data d = new Data();
		    	ObjectId dataId = (ObjectId)o.get("_id");
		    	ObjectId bedId = (ObjectId)o.get("bedId");
		    	d.id = Integer.parseInt(dataIds.get(dataId.toString()));
		    	d.parameterId = parameterIds.get(paramId.toString());
		    	d.bed = bedIds.get(bedId.toString());
		    	d.value = o.get("value");
		    	d.date = o.getLong("date");
		    	result.add(d);		    	
		    }		    
		}
		return result;

	}

	@Override
	public List<Data> getParamData(String parameterId, String bed) {
		String queryMatch = "{'_id' : { $oid : '"+ parameterIds.get(parameterId) + "'}}";
		String queryFilter = "{'data' : {$elemMatch : {'bedId' : { $oid : '" + bedIds.get(bed) + "'}}}}";		
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		BasicDBObject filter = (BasicDBObject)JSON.parse(queryFilter);
		DBCursor cursor = db.getCollection("parameters").find(match, filter);
		
		List<Data> result = new ArrayList<>();
		
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    ObjectId paramId = (ObjectId)obj.get("_id");
		    BasicDBList data = (BasicDBList)obj.get("data");	    
		    for (Object dataObj : data){
		    	BasicDBObject o = (BasicDBObject)dataObj;
		    	Data d = new Data();
		    	ObjectId dataId = (ObjectId)o.get("_id");
		    	ObjectId bedId = (ObjectId)o.get("bedId");
		    	d.id = Integer.parseInt(dataIds.get(dataId.toString()));
		    	d.parameterId = parameterIds.get(paramId.toString());
		    	d.bed = bedIds.get(bedId.toString());
		    	d.value = o.get("value");
		    	d.date = o.getLong("date");
		    	result.add(d);		    	
		    }		    
		}
		return result;
	}

	@Override
	public List<Patient> getPatientsByName(String name) {
		String queryMatch = "{'beds.patient.name' : '" + name + "'}";
		String queryFilter = "{'beds' : {$elemMatch : {'patient.name' : '" + name + "'}}}";
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		BasicDBObject filter = (BasicDBObject)JSON.parse(queryFilter);
		DBCursor cursor = db.getCollection("modules").find(match, filter);
		List<Patient> result = new ArrayList<>();  
		while(cursor.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursor.next();
			BasicDBList beds = (BasicDBList)obj.get("beds");	    
		    for (Object dataObj : beds){
		    	Patient p = new Patient();	
		    	BasicDBObject bedObject = (BasicDBObject)dataObj;
		    	BasicDBObject patientObject = (BasicDBObject)bedObject.get("patient");
		    	ObjectId bedId = (ObjectId)bedObject.get("_id");
		    	ObjectId patientId = (ObjectId)patientObject.get("_id");
		    	p.id = Integer.parseInt(patientIds.get(patientId.toString()));
		    	p.bed = bedIds.get(bedId.toString());
		    	p.name = patientObject.getString("name");
		    	p.sex = patientObject.getString("sex");
		    	p.socialSecurityNumber = patientObject.getString("socialSecurityNumber");	 
		    	result.add(p);
		    }
		}
		return result;
	}

	@Override
	public List<Patient> getPatientsForModule(int id) {
		String queryMatch = "{'_id' : {$oid : '"+ moduleIds.get(id+"") + "'}}";
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		DBCursor cursor = db.getCollection("modules").find(match);
		List<Patient> result = new ArrayList<>();		
		while(cursor.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursor.next();
			BasicDBList beds = (BasicDBList)obj.get("beds");
			if(beds != null){
			    for (Object dataObj : beds){
			    	Patient p = new Patient();		    
			    	BasicDBObject bedObject = (BasicDBObject)dataObj;
			    	BasicDBObject patientObject = (BasicDBObject)bedObject.get("patient");
			    	//if bed is empty
			    	if(patientObject == null){
			    		System.out.println("It Is NULL: " + bedObject);
			    		continue;
			    	}
			    	ObjectId bedId = (ObjectId)bedObject.get("_id");
			    	ObjectId patientId = (ObjectId)patientObject.get("_id");
			    	p.id = Integer.parseInt(patientIds.get(patientId.toString()));
			    	p.bed = bedIds.get(bedId.toString());
			    	p.name = patientObject.getString("name");
			    	p.sex = patientObject.getString("sex");
			    	p.socialSecurityNumber = patientObject.getString("socialSecurityNumber");	 
			    	result.add(p);		    	
			    }			
			}
		}
		return result;
	}

	@Override
	public List<Bed> getBedsForModule(String id) {
		String queryMatch = "{'_id' : {$oid : '"+ moduleIds.get(id) + "'}}";
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		DBCursor cursor = db.getCollection("modules").find(match);
		List<Bed> result = new ArrayList<>();  
		while(cursor.hasNext()){
			BasicDBObject obj = (BasicDBObject) cursor.next();			
			ObjectId moduleId = (ObjectId)obj.get("_id");
			BasicDBList beds = (BasicDBList)obj.get("beds");	    
		    for (Object dataObj : beds){
		    	Bed b = new Bed();		    
		    	BasicDBObject o = (BasicDBObject)dataObj;		    	
		    	ObjectId bedId = (ObjectId)o.get("_id");
		    	b.id = Integer.parseInt(bedIds.get(bedId.toString()));
		    	b.module = moduleIds.get(moduleId.toString());
		    	b.name = o.getString("name");
		    	result.add(b);
		    }			
		}
		return result;
	}

	@Override
	public List<Module> getModules() {
		String queryFilter = "{'beds' : 0}";
		BasicDBObject filter = (BasicDBObject)JSON.parse(queryFilter);
		DBCursor cursor = db.getCollection("modules").find(new BasicDBObject(), filter);
		
		List<Module> result = new ArrayList<>();
		
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    Module m = new Module();
		    ObjectId id = (ObjectId)obj.get("_id");  
		    m.id = Integer.parseInt(moduleIds.get(id.toString()));
		    m.name = obj.getString("name");		    
		    result.add(m);
		}
		return result;
	}

	@Override
	public List<Data> getDataFromTimeSpan(String bedId, long startTime, long endTime) {
		String queryMatch = "{'data.bedId' : { $oid : '"+ bedIds.get(bedId+"") + "'},";
		queryMatch += " 'data.date' : {$gte : " + startTime + ", $lte : " + endTime + "}}";
		String queryFilter = "{'data' : {$elemMatch : {'bedId' : { $oid : '" + bedIds.get(bedId+"") + "'}}}}";		
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);
		BasicDBObject filter = (BasicDBObject)JSON.parse(queryFilter);
		DBCursor cursor = db.getCollection("parameters").find(match, filter);
		
		List<Data> result = new ArrayList<>();
		
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    ObjectId paramId = (ObjectId)obj.get("_id");
		    BasicDBList data = (BasicDBList)obj.get("data");	    
		    for (Object dataObj : data){
		    	BasicDBObject o = (BasicDBObject)dataObj;
		    	Data d = new Data();
		    	ObjectId dataId = (ObjectId)o.get("_id");
		    	ObjectId bId = (ObjectId)o.get("bedId");
		    	d.id = Integer.parseInt(dataIds.get(dataId.toString()));
		    	d.parameterId = parameterIds.get(paramId.toString());
		    	d.bed = bedIds.get(bId.toString());
		    	d.value = o.get("value");
		    	d.date = o.getLong("date");
		    	result.add(d);		    	
		    }		    
		}
		return result;
	}

	@Override
	public boolean addFieldToData(String entry, String field) {
		return true;
	}

	@Override
	public boolean updateValueForData(String entry, String value) {	
		return true;
		/*
		String queryMatch = "{data : {$exists : true}}";		
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);		
		DBCursor cursor = db.getCollection("parameters").find(match);
		try{
			while(cursor.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursor.next();				
				List<BasicDBObject> a = (ArrayList)obj.get("data");
				for(BasicDBObject o : a){
					ObjectId id = o.getObjectId("_id");
					String tempQuery = "{'data._id' : {$oid : '" + id.toString() + "'}}";
					String hej = "{'$set' : {'data.$.value' : '" + value + "'}}";
					BasicDBObject q = (BasicDBObject)JSON.parse(tempQuery);
					BasicDBObject w = (BasicDBObject)JSON.parse(hej);
					db.getCollection("parameters").update(q, w);
				}				
			}
		}catch(Exception e){
			System.out.println(e.toString());
			return false;
		}finally{
			cursor.close();
		}
		return true;*/
	}

	@Override
	public boolean removeFieldForData(String entry, String field) {
		return true;
		/*
		String queryMatch = "{data : {$exists : true}}";		
		BasicDBObject match = (BasicDBObject)JSON.parse(queryMatch);		
		DBCursor cursor = db.getCollection("parameters").find(match);
		try{
			while(cursor.hasNext()){
				BasicDBObject obj = (BasicDBObject) cursor.next();				
				List<BasicDBObject> a = (ArrayList)obj.get("data");
				for(BasicDBObject o : a){
					ObjectId id = o.getObjectId("_id");
					String tempQuery = "{'data._id' : {$oid : '" + id.toString() + "'}}";
					String hej = "{'$unset' : {'data.$.value' : 1}}";
					BasicDBObject q = (BasicDBObject)JSON.parse(tempQuery);
					BasicDBObject w = (BasicDBObject)JSON.parse(hej);
					db.getCollection("parameters").update(q, w);	
				}				
			}
		}catch(Exception e){
			System.out.println(e.toString());
			return false;
		}finally{
			cursor.close();
		}
		return true;*/
	}
}
