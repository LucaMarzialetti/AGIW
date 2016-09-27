package it.uniroma3.agiw2.launch;

public class MyJSONPP extends MyJSON{
	private String message;

	public MyJSONPP() {
	}
	
	public MyJSONPP(String message) {
		this.message=message;
	}
	
	/**this wrapper is used just for extend JSONObject and use polymorphism
	 * Is need to avoid type check on councurrent data structures using poison pills**/
	/**the method pillshoot return true if the object is a poisonpill**/
	@Override
	public boolean pillShoot(){
		return true;
	}

	public String getMessage() {
		return message;
	}
}
