package it.uniroma3.agiw2.data_extractor.xpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class PrefixedProperty extends Properties {
	private String fileName;
	private static final long serialVersionUID = 1L;

	public PrefixedProperty(String fileName) {
		this.fileName=fileName;
	}

	public Properties getPropertyByPrefix(String prefix) {
		Properties properties = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		try {
			properties.load(is);
			final Iterator<Object> keyIterator = properties.keySet().iterator();
			while(keyIterator.hasNext()){
				if(!keyIterator.next().toString().startsWith(prefix))
					keyIterator.remove();
			}
		} 
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return properties;
	}
}

