package it.uniroma3.agiw2.data_extractor;
import java.util.concurrent.ConcurrentHashMap;

public class CountersManager {
	/**used for total links count**/
	private int count;		
	
	/**used to know how many threads operate over a source
	in xpathvalidator a thread.sleep use this information: 
	more thread use a source, more time sleep is needed to avoid blacklisting**/
	private ConcurrentHashMap<String, Integer> map;		

	public CountersManager() {
		this.count=0;
		this.map=new ConcurrentHashMap<>();
	}

	public int getCount(){
		return this.count;
	}

	/**Incremented for each HTTL GET on some URL**/
	synchronized public void increment() {
		this.count++;
	}

	/**Thread working on a source declare to hold it**/
	synchronized public void holdMap(String k) {
		if(!this.map.containsKey(k))
			this.map.put(k, new Integer(1));
		else{
			int ans = this.map.get(k).intValue();
			ans++;
			this.map.put(k, new Integer(ans));
		}
	}

	/**Thread that have finished to work on a source declare to release it**/
	synchronized public void releaseMap(String k) {
		if(this.map.containsKey(k)){
			int i = this.map.get(k).intValue();
			i--;
			if(i==0)
				this.map.remove(k);
			else
				this.map.put(k, new Integer(i));
		}
	}
	
	/**Return number of threads running on a source**/
	public int inMap(String k) {
		int ans = 0;
		if(this.map.containsKey(k))
			ans = this.map.get(k).intValue();
		return ans;
	}
}
