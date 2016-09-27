package it.uniroma3.agiw2.launch;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Tester {
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String file = "xpath.marzialetti.json";
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		File fpath = new File(file);
		JSONObject rootNode = mapper.readValue(fpath, JSONObject.class);
		deepPrint(rootNode," ");
	}

	/**Recursive Deep print of a json object **/
	public static void deepPrint(Object o, String tabs){
		if(o instanceof JSONObject){
			JSONObject jo = new JSONObject(o.toString());
			Iterator<String> it = jo.keySet().iterator();
			System.out.println(tabs+"{");
			while(it.hasNext()){
				String key = (String) it.next();
				Object val = jo.get(key);
				System.out.print(tabs+key+":");
				if(!(val instanceof String))
					System.out.println();
				deepPrint(val,tabs+"\t");
			}
			System.out.println(tabs.substring(1)+"}");
		}
		else 
			if(o instanceof JSONArray){
				JSONArray a = new JSONArray(o.toString());
				System.out.println(tabs+"[");
				int i=0;
				for(i=0; i<a.length()-1;i++){
					deepPrint(a.get(i),tabs+"\t");
					System.out.println(tabs+",");
				}
				if(a.length()>0)
					deepPrint(a.get(i),tabs+"\t");
				System.out.println(tabs.substring(1)+"]");
			}
			else{
				System.out.println(String.valueOf(o.toString()));
			}
	}
}
