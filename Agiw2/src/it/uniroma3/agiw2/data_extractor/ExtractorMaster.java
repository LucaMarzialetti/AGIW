package it.uniroma3.agiw2.data_extractor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;

import it.uniroma3.agiw2.launch.MyJSON;
import it.uniroma3.agiw2.launch.MyJSONPP;

public class ExtractorMaster implements Runnable{
	private LinkedBlockingDeque<MyJSON> deq_r;
	private LinkedBlockingDeque<MyJSON> deq_w;
	private ExecutorService es;
	private CountersManager cm;
	
	public ExtractorMaster(LinkedBlockingDeque<MyJSON> deq_r, LinkedBlockingDeque<MyJSON> deq_w) {
		this.deq_r=deq_r;
		this.deq_w=deq_w;
		this.es = Executors.newCachedThreadPool();
		this.cm = new CountersManager();
	}

	/**This runnable manage the execution of all other slaves and coordinte the wrinting to output**/
	@Override
	public void run() {
		System.out.println("[ExtractorMaster] - Runing");
		this.crawl();
		System.out.println("[ExtractorMaster] - Teminated");
	}

	/**The master submit all the slaves tasks, then wait for theme termination (using semaphore)
	 * once terminated, kill the writer thread and **/
	private void crawl(){
		int numberOfTasks=0;
		Semaphore semaphore = new Semaphore(0);
		try {
			MyJSON jo;
			while((jo = this.deq_r.takeLast())!=null && !jo.pillShoot()){
				String source = (String)jo.get("source");
				String name = (String)jo.get("name");
				JSONArray links = new JSONArray(jo.get("links").toString());
				//				System.out.println(source);
				//				System.out.println(name);
				//				for(int i=0; i<links.length(); i++)
				//					System.out.println("\t"+links.get(i));
				System.out.println("[ExtractorMaster]-pop: "+name);
				numberOfTasks++;
				this.es.submit(new SlaveExtractor(source, name, links, semaphore, this.deq_w, this.cm));	
			}
			System.out.println("[ExtractorMaster] - Poison_pill!!");
			System.out.println("[ExtractorMaster] - Waiting for Slaves");
		    semaphore.acquire(numberOfTasks);
			System.out.println("[ExtractorMaster] - Killing Writer");
		    this.deq_w.putFirst(new MyJSONPP("Total Links Scraped: "+cm.getCount()));
			this.es.shutdown();
		}
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
