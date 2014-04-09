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
	private List<String> patientModules;
	
	public List<Parameter> parameters;
	public List<Patient> patients;
	public List<Data> data;
	
	public DummyData() {
		parameterNames = Arrays.asList("pH", "pco2", "po2", "be", "sp02", "mode", "fi02", "pep", "ekg", "cvp", "tidavolym", "expirationsminut volym", "simdax", "temperatur", "dränväxteodling", "tinam", "adrenalin");
		parameterCats = Arrays.asList("Vital Parameter", "Inställning", "avläsning", "pump", "labb", "odling", "antibiotika", "övrigt");
		parameterUnits = Arrays.asList("kpa", "%", "cm", "y2", "ml", "dl", "m2", "c", "n", "ug");
		parameterRounds = Arrays.asList("respiration", "cirkulation", "infektion", "övrigt");
		patientNames = Arrays.asList("Kalle", "Anders", "Jonas", "Helge", "Flaskis", "Brask", "Bulle", "Soffis", "Mario", "Bord", "Baljan", "Mange", "QWERTYÄRMYCKETBRA", "Erik von Daffulegården Nils-Eriks Brorsdotter Karlsson");
		patientModules = Arrays.asList("M12", "C07", "K85", "H00", "CHO", "ABC");
	}
	
	public List<Parameter> getParameterDummyData(int nr){
		List<Parameter> params = new ArrayList<>();
		
		for (int i = 1; i <= nr; i++){
			String name = parameterNames.get(rand.nextInt(parameterNames.size()));
			String cat = parameterCats.get(rand.nextInt(parameterCats.size()));
			String unit = parameterUnits.get(rand.nextInt(parameterUnits.size()));
			String round = parameterRounds.get(rand.nextInt(parameterRounds.size()));
			float value = rand.nextFloat() * 1000.0f;
			params.add(new Parameter(i, cat, name, unit, round, value, value, value, value));
		}
		parameters = params;
		return params;
	}
	
	public List<Patient> getPatientDummyData(int nr){
		List<Patient> patients = new ArrayList<>();
		
		for (int i = 1; i <= nr; i++){
			String name = patientNames.get(rand.nextInt(patientNames.size()));
			String module = patientModules.get(rand.nextInt(patientModules.size()));
			patients.add(new Patient(i, name, "19920306-0000", rand.nextInt(1), module));
		}
		this.patients = patients;
		return patients;
	}
	
	public List<Data> getDummyData(List<Patient> patients, List<Parameter> params, int nr) {
		List<Data> data = new ArrayList<>();
			
		for (int i = 0; i< nr; i++){
			
			long week = 7*1000*60*60*24;
			Date end = new Date();
			Date start = new Date(end.getTime() - week);			
			String paramId = params.get(rand.nextInt(params.size())).id + "";
			String patId = patients.get(rand.nextInt(patients.size())).id + "";
			float value = rand.nextFloat() * 1000.0f;
			Data d = new Data(i, getRandomDate(start, end), paramId, patId, value);
			data.add(d);
		}
		this.data = data;
		return data;
	}
	
	private Date getRandomDate(Date start, Date end) {		
		long diff = end.getTime() - start.getTime();
		Date date = new Date(start.getTime() + rand.nextLong() % diff);
		return date;
	}
}
