package com.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Http {

	public static String get(String url) {
		return sendGet(url, "UTF-8");
	}

	public static String get(String url, String charset) {
		return sendGet(url, charset);
	}
	
	public static String sendGet(String url, String result) {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			response = client.execute(get);
			result = EntityUtils.toString(response.getEntity(), result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null)
					client.close();
				if (response != null)
					response.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
}