package it.uniroma3.agiw2.json_writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.uniroma3.agiw2.launch.Main;
import it.uniroma3.agiw2.launch.MyJSON;
import it.uniroma3.agiw2.launch.MyJSONPP;

public class JsonWriter implements Runnable{
	private LinkedBlockingDeque<MyJSON> deq_w;
	private String data_file = Main.data_file+"."+Main.cognome+"."+Main.ext;
	private String xpath_file = Main.xpath_file+"."+Main.cognome+"."+Main.ext;
	private String final_message;
	
	public JsonWriter(LinkedBlockingDeque<MyJSON> deq_w){
		this.deq_w = deq_w;
	}

	/**This runnable is used to write to file the answeres of the other threads and 
	 * serialize the output**/
	@Override
	public void run(){
		System.out.println("[JsonWriter] - Runing");
		this.write();
		System.out.println("[JsonWriter] - Terminated");
		System.out.println(this.final_message);
	}

	/**initialize the empty files 
	 * then wait on listening on the deq until poisin pill is extracted
	 * so flush and close the streams, then an output message is printed**/
	public void write() {
		FileWriter file_data;
		FileWriter file_xpath;
		try {
			file_data = new FileWriter(this.data_file);
			file_xpath = new FileWriter(this.xpath_file);
			file_data.write("{}");	//initialize json
			file_xpath.write("{}"); //initialize json
			file_data.flush();
			file_xpath.flush();
			MyJSON jo;
			while((jo = this.deq_w.takeLast())!=null && !jo.pillShoot()){
				writeFiles(jo, file_data, file_xpath);
				System.out.println("[JsonWriter] - write "+jo.get("name"));
			}
			MyJSONPP pill = (MyJSONPP) jo;
			this.final_message= pill.getMessage();
			file_data.flush();
			file_xpath.flush();
			file_data.close();
			file_xpath.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	/**for each message received 2 files are written: data and xpath**/
	private void writeFiles(MyJSON jo, FileWriter file_data ,FileWriter file_xpath){		
		if(jo.has("xpath")) {
			JSONArray xpath = jo.getJSONArray("xpath");
			this.writeXPath(file_xpath, xpath, jo.getString("source"), jo.getString("name"));
		}
		if(jo.has("data")) {
			JSONObject data = jo.getJSONObject("data");
			this.writeData(file_xpath, data, jo.getString("source"), jo.getString("name"));
		}
	}

	/**write the xpath file with the standard form requested**/
	private void writeXPath(FileWriter file_xpath, JSONArray jo, String source, String name) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JSONObject rootNode;
		try {
			File fpath = new File(this.xpath_file);
			rootNode = mapper.readValue(fpath, JSONObject.class);
			JSONObject jsource;
			if(!rootNode.has(source)) {
				jsource = new JSONObject();
				rootNode.put(source, new JSONObject());
			}
			jsource = rootNode.getJSONObject(source);
			jsource.put(name,jo);
			rootNode.put(source, jsource);
			mapper.writerWithDefaultPrettyPrinter().writeValue(fpath,rootNode.toString(4));
			//			try (FileWriter file = new FileWriter(fpath)) 
			//            {
			//                file.write(rootNode.toString(4));
			//            }
		}
		catch (JsonParseException e) {
			System.out.println(e.getMessage());
		} 
		catch (JsonMappingException e) {
			System.out.println(e.getMessage());
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**write the data file with the standard form requested**/
	private void writeData(FileWriter file_xpath, JSONObject jo, String source, String name) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JSONObject rootNode;
		try {
			File fpath = new File(this.data_file);
			rootNode = mapper.readValue(fpath, JSONObject.class);
			JSONObject jsource;
			if(!rootNode.has(source)) {
				jsource = new JSONObject();
				rootNode.put(source, new JSONObject());
			}
			jsource = rootNode.getJSONObject(source);
			jsource.put(name,jo);
			rootNode.put(source, jsource);
			mapper.writerWithDefaultPrettyPrinter().writeValue(fpath,rootNode.toString(4));
			//			try (FileWriter file = new FileWriter(fpath)) 
			//            {
			//                file.write(rootNode.toString(4));
			//            }
		}
		catch (JsonParseException e) {
			System.out.println(e.getMessage());
		} 
		catch (JsonMappingException e) {
			System.out.println(e.getMessage());
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}