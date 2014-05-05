package se.testdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DummyData {
	private Random rand = new Random();
	private List<String> parameterNames;
	private List<String> parameterCats;
	private List<String> parameterUnits;
	private List<String> parameterRounds;
	private List<String> patientNames;
	private List<String> bedNames;
	private List<String> moduleNames;
	
	public List<Parameter> parameters;
	public List<Patient> patients;
	public List<Bed> beds;
	public List<Module> modules;
	public List<Data> data;
	
	public DummyData() {
		parameterNames = Arrays.asList("pH", "pco2", "po2", "be", "sp02", "mode", "fi02", "pep", "ekg", "cvp", "tidavolym", "expirationsminut volym", "simdax", "temperatur", "dränväxteodling", "tinam", "adrenalin");
		parameterCats = Arrays.asList("Vital Parameter", "Inställning", "avläsning", "pump", "labb", "odling", "antibiotika", "övrigt");
		parameterUnits = Arrays.asList("kpa", "%", "cm", "y2", "ml", "dl", "m2", "c", "n", "ug");
		parameterRounds = Arrays.asList("respiration", "cirkulation", "infektion", "övrigt");
		patientNames = Arrays.asList("Kalle", "Anders", "Jonas", "Helge", "Flaskis", "Brask", "Bulle", "Soffis", "Mario", "Bord", "Baljan", "Mange", "QWERTYÄRMYCKETBRA", "Erik von Daffulegården Nils-Eriks Brorsdotter Karlsson");
		bedNames = Arrays.asList("B32", "A47", "PPP", "QQQ", "GGG", "AAA");
		moduleNames = Arrays.asList("M12", "C07", "K85", "H00", "CHO", "ABC");
	}
	
	public void genParameterDummyData(int nr){
		List<Parameter> params = new ArrayList<>();
		
		for (int i = 1; i <= nr; i++){
			//add i to param name to make parameternames unique
			String name = parameterNames.get(rand.nextInt(parameterNames.size())) + i;
			String cat = parameterCats.get(rand.nextInt(parameterCats.size()));
			String unit = parameterUnits.get(rand.nextInt(parameterUnits.size()));
			String round = parameterRounds.get(rand.nextInt(parameterRounds.size()));
			float value = rand.nextFloat() * 1000.0f;
			params.add(new Parameter(i, cat, name, unit, round, value, value, value, value));
		}
		parameters = params;
	}
	
	public void genPatientDummyData(int nr){
		List<Patient> patients = new ArrayList<>();
		
		for (int i = 1; i <= nr; i++){
			String name = patientNames.get(rand.nextInt(patientNames.size()));			
			patients.add(new Patient(i, i+"", name, "19920306-0000", rand.nextInt(2)));
		}
		this.patients = patients;
	}
	
	public void genDummyModules(int nr){
		List<Module> modules = new ArrayList<>();
		for (int i = 1; i <= nr; i++){
			String name = moduleNames.get(rand.nextInt(moduleNames.size()));
			modules.add(new Module(i, name));
		}
		this.modules = modules;
	}
	
	public void genDummyBeds(int nr){
		List<Bed> beds = new ArrayList<>();
		for (int i = 1; i <= nr; i++){
			String name = bedNames.get(rand.nextInt(bedNames.size()));
			int moduleId = modules.get(rand.nextInt(modules.size())).id;
			beds.add(new Bed(i, name, moduleId+""));
		}
		this.beds = beds;
	}
	
	public void genDummyData(int nr) {
		List<Data> data = new ArrayList<>();
			
		for (int i = 1; i< nr; i++){			
			long week = 7*1000*60*60*24;
			Date end = new Date();
			Date start = new Date(end.getTime() - week);			
			String paramId = parameters.get(rand.nextInt(parameters.size())).id + "";
			String bedId = beds.get(rand.nextInt(beds.size())).id + "";
			float value = rand.nextFloat() * 1000.0f;
			Data d = new Data(i, getRandomDate(start, end), paramId, bedId, value);
			data.add(d);
		}
		this.data = data;
	}
	
	private Date getRandomDate(Date start, Date end) {		
		long diff = end.getTime() - start.getTime();
		Date date = new Date(start.getTime() + rand.nextLong() % diff);
		return date;
	}
}
