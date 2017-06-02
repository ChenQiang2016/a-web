package com.spider;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClient {

	@Test
	public void getInfo() {
		String url = "http://list.youku.com/category/show/c_84_g_%E4%BA%BA%E7%89%A9_s_1_d_2_p_2.html";

		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		HttpGet get = new HttpGet(url);

		try {
			client = HttpClients.createDefault();
			response = client.execute(get);
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static String getInfo(String url){
		String result = "";
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		HttpGet get = new HttpGet(url);

		try {
			client = HttpClients.createDefault();
			response = client.execute(get);
			result = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}