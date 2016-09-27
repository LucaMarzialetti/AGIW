package it.uniroma3.agiw2.data_extractor.xpath;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import it.uniroma3.agiw2.data_extractor.CountersManager;
import it.uniroma3.agiw2.http_connector.HttpConnector;

public class XpathValidator {
	private Properties xPathRules;
	private String source;
	private CountersManager cm;

	public XpathValidator(Properties xProperties, String source, CountersManager cm){
		this.xPathRules = xProperties;
		this.source = source;
		this.cm = cm;
	}

	/** apply all the xpath in xPathRules on source **/
	/** xPathRules are already matched based on the prefix **/
	public JSONObject validateLinks(JSONArray links) {
		Random random = new Random();
		JSONObject jdata = new JSONObject();
		int linknumbers = links.length();
		/*for each link, validate, and add to a common json the result*/
		for(int i=0; i<linknumbers; i++){
			this.cm.increment();
			String url = links.getString(i);
			JSONObject data = validate(url);
			Iterator<String> iter = data.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				JSONArray ja;
				if(jdata.has(key))
					ja = jdata.getJSONArray(key);
				else
					ja = new JSONArray();
				ja.put(data.get(key));
				jdata.put(key, ja);
			}
			try {
				int sleepTime = 500+random.nextInt(2000*this.cm.inMap(this.source));
				//System.out.println("sorgente "+this.source+", iterazione "+i+", dormo "+sleepTime);
				Thread.sleep(sleepTime);
			} 
			catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		return jdata;
	}

	/** apply all the xpath in xPathRules on the URL **/
	/** xPathRules are already matched based on the prefix **/
	public JSONObject validate(String url) {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		JSONObject jdata = new JSONObject();
		String prop=null;
		try {
			Document doc = HttpConnector.getPageRetrieve(url).normalise();
			String html = doc.html();
			TagNode tagNode = new HtmlCleaner().clean(html);
			org.w3c.dom.Document document = new DomSerializer(new CleanerProperties(), false).createDOM(tagNode);
			Iterator<Object> iter = this.xPathRules.keySet().iterator();
			while(iter.hasNext()){
			/*for each property compose a json with the its name and the value*/
				String property_key = (String) iter.next();
				prop = (String) this.xPathRules.getProperty(property_key);
				XPath xpath = xPathFactory.newXPath();
				String nl = (String) xpath.evaluate(prop, document, XPathConstants.STRING);
				nl = nl.trim();
				String key = property_key.split("\\.")[1];
				/**json data**/
				JSONObject jo = new JSONObject();
				jo.put(url, nl);			
				jdata.put(key, jo);
			}
		}
		catch (ParserConfigurationException e) {
			System.out.println("Err "+e.getMessage());
		}
		catch(XPathExpressionException e){
			//se l'espressione e' vuota o ha problemi
			System.out.println("Xpath error evaluating expression -> ("+prop+")");
		}
		catch (IOException e) {
			//se la pagina non e' raggiugibile o ci sono errori nell'url
			System.out.println(e.getMessage()+" -> ["+url+"]");
		}
		return jdata;
	}
}