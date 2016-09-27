package it.uniroma3.agiw2.parser;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.uniroma3.agiw2.launch.Main;
import it.uniroma3.agiw2.launch.MyJSON;
import it.uniroma3.agiw2.launch.MyJSONPP;


public class SourceParser implements Runnable{
	private LinkedBlockingDeque<MyJSON> deq;
	private int range_min = 5401;
	private int range_max = 5600;

	public SourceParser(LinkedBlockingDeque<MyJSON> deq) {
		this.deq = deq;
		/**to select a specified sub-range without change min,max**/
//		int number = 197;
//		this.range_min=this.range_min+number;
//		this.range_max=this.range_min+2;
	}

	/**This runnable is used to parse the json file in input and put in a Deq all the jobs**/
	@Override
	public void run() {
		System.out.println("[SourceParser] - Runing");
		this.parse();
		System.out.println("[SourceParser] - Terminated");
	}

	/**Put in the deq all the sources and then add a poison pill**/
	public void parse(){
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readValue(new File(Main.source_file_name), JsonNode.class);
			this.getInRange(rootNode);
			this.deq.putFirst(new MyJSONPP());
			System.out.println("[SourceParser] - Poison_pill!!");
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
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}	

	/** Get all the sources from the source file in the specified range 
	 * the range is matched based on the number of the source eg:(5401-5600) **/
	private void getInRange(JsonNode jn){
		boolean found = false;
		boolean done = false;
		Iterator<String> iter1 = jn.fieldNames();
		while(iter1.hasNext()&&!(found&&done)){
			String parent = iter1.next();
			JsonNode pnode = jn.get(parent);
			Iterator<String> iter2 = pnode.fieldNames();
			while(iter2.hasNext()&&!(found&&done)){
				String child = iter2.next();
				if(child.contains(String.valueOf(range_min))&&!found)
					found=true;
				if(child.contains(String.valueOf(range_max))&&found)
					done=true;
				if(found){
					MyJSON mj = new MyJSON();
					mj.put("source", parent);
					mj.put("name", child);
					mj.put("links", pnode.get(child));
					try {
						this.deq.putFirst(mj);
					} 
					catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
					System.out.println("[SourceParser]-push: "+child);
				}
			}
		}
	}
}
