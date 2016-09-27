package it.uniroma3.agiw2.launch;

import org.json.JSONObject;

/**this wrapper is used just for extend JSONObject and use polymorphism
 * Is need to avoid type check on councurrent data structures using poison pills**/
/**the method pillshoot return true if the object is a poisonpill**/
public class MyJSON extends JSONObject{

	public boolean pillShoot(){
		return false;
	}
}
