package it.uniroma3.agiw2.launch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import it.uniroma3.agiw2.data_extractor.ExtractorMaster;
import it.uniroma3.agiw2.json_writer.JsonWriter;
import it.uniroma3.agiw2.parser.SourceParser;

public class Main {
	
	/*xpath_rulse-properties*/
	public static final String xpath_rules_file = "xpath.properties";
	
	/*source_parser-conf*/
	public static final String source_file_name = "sources.json";
	
	/*writer-conf*/
	public static final String ext = "json";
	public static final String cognome = "test";
	public static final String data_file = "data";
	public static final String xpath_file = "xpath";

	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();
		LinkedBlockingDeque<MyJSON> deq_r = new LinkedBlockingDeque<>();
		LinkedBlockingDeque<MyJSON> deq_w = new LinkedBlockingDeque<>();
		SourceParser sourceParser = new SourceParser(deq_r);
		ExtractorMaster extractorMaster = new ExtractorMaster(deq_r, deq_w);
		JsonWriter jsonWriter = new JsonWriter(deq_w);
		System.out.println("[Main] - Launching Pool");
		es.execute(sourceParser);
		es.execute(extractorMaster);
		es.execute(jsonWriter);
		es.shutdown();
		System.out.println("[Main] - Terminated");
	}
}