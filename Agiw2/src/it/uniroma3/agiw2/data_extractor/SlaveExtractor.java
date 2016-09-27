package it.uniroma3.agiw2.data_extractor;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniroma3.agiw2.data_extractor.xpath.PrefixedProperty;
import it.uniroma3.agiw2.data_extractor.xpath.XpathValidator;
import it.uniroma3.agiw2.launch.Main;
import it.uniroma3.agiw2.launch.MyJSON;

public class SlaveExtractor implements Runnable {
	private String source;
	private String name;
	private JSONArray links;
	private String prefix;
	private Semaphore semaphore;
	private LinkedBlockingDeque<MyJSON> deq_w;
	private CountersManager cm;
	private Properties xPath_rules;

	public SlaveExtractor(String source, String name, JSONArray links, Semaphore semaphore, LinkedBlockingDeque<MyJSON> deq_w, CountersManager cm) {
		this.source = source;
		this.name = name;
		this.links = links;
		this.semaphore = semaphore;
		this.deq_w = deq_w;
		this.cm = cm;
		/**get prefix from name**/
		Pattern pattern = Pattern.compile(".*?([0-9]*)");
		Matcher matcher = pattern.matcher(this.name);
		if (matcher.find()){
			this.prefix = matcher.group(1);
		}
		this.xPath_rules = new PrefixedProperty(Main.xpath_rules_file).getPropertyByPrefix(this.prefix);
	}

	/**This runnable is used to process a single source(set of links) and procude an output validating the xpath on them**/
	@Override
	public void run() {
		System.out.println("[SlaveExtractor] - "+this.prefix+": Fetching & Validating");
		this.extract();
		System.out.println("[SlaveExtractor] - "+this.prefix+": Terminated");
		this.semaphore.release();
	}

	public void extract() {
		MyJSON toWrite = new MyJSON();
		JSONArray ja = this.doXpathFile();		//make the json for xpath
		JSONObject data = this.doDataFile();	//make the json for data
		/**add field to json to write**/
		toWrite.put("xpath", ja);
		toWrite.put("data",data);
		toWrite.put("source", this.source);
		toWrite.put("name", this.name);
		try {
			this.deq_w.putFirst(toWrite);
		}
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	/**Create JSON with the data results of the xpath**/
	public JSONObject doDataFile(){
		XpathValidator validator = new XpathValidator(this.xPath_rules, this.source, this.cm);
		this.cm.holdMap(this.source);
		JSONObject res = validator.validateLinks(this.links);
		this.cm.releaseMap(this.source);
		return res;
	}

	/**Create JSON with xpath**/
	public JSONArray doXpathFile(){
		Iterator<Object> iterator = this.xPath_rules.keySet().iterator();
		JSONArray ja = new JSONArray(); 
		while(iterator.hasNext()){
			String key = (String) iterator.next();
			String val = this.xPath_rules.getProperty(key);
			String key_name = key.split("\\.")[1];
			JSONObject jo = new JSONObject();
			jo.put("rule", val);
			jo.put("attribute_name", key_name);
			jo.put("page_id", "true");
			ja.put(jo);
		}
		return ja;
	}
}