package it.uniroma3.agiw2.http_connector;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpConnector {

	/**GET METHOD
	 * @throws IOException **/
	public static Document getPageRetrieve(String url) throws IOException {
		Document doc=null;
		doc = Jsoup.connect(url)
				.timeout(0)
				.maxBodySize(0)
				.userAgent(RandomUserAgent.getRandomUserAgent())
				.get();
		return doc;
	}
}
