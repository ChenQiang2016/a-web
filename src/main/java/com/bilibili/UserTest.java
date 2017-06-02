package com.bilibili;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UserTest {

	public String username = "sevacc";
	public String password = "1234qwer";

	public static void main(String[] args) {
		loginAndGet();
	}

	public static void loginAndGet() {
		String result = "";

		HttpGet get = new HttpGet("http://www.baidu.com");

		HttpClient client = HttpClients.createDefault();

		try {
			HttpResponse response = client.execute(get);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
	}
}