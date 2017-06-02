package com.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class HttpClient {

	@Test
	public void testReadHtml() {
		Date start = new Date();
		String url = "http://list.youku.com/category/show/c_84_s_6_d_1_g_%E4%BA%BA%E7%89%A9.html";
		String html = com.spider.HttpClient.getInfo(url);
		Document document = Jsoup.parse(html);
		Elements elements = document.select("ul.panel").first().children();
		Map<String, String> map = new HashMap<>();
		Date middle = new Date();
		System.out.println(middle.getTime() - start.getTime());
		for (Element element : elements) {
			Date for_start = new Date();
			String video_url = element.getElementsByTag("a").first().attr("href");
			String image_url = element.getElementsByTag("img").first().attr("src");
			if (video_url.startsWith("//")) {
				video_url = "http:" + video_url;
			}
			Date for_81 = new Date();
			System.out.println("for_start-81:" + (for_81.getTime() - for_start.getTime()));
			String detail_html = com.spider.HttpClient.getInfo(video_url);
			Date for_84 = new Date();
			System.out.println("81-84:" + (for_84.getTime() - for_81.getTime()));
			Document detail_doc = Jsoup.parse(detail_html);
			String detail_url = detail_doc.select("a.desc-link").attr("href");
			Date for_88 = new Date();
			System.out.println("83-88:" + (for_88.getTime() - for_84.getTime()));
			if (detail_url.startsWith("//")) {
				detail_url = "http:" + detail_url;
			}
			map.put(detail_url, image_url);
			Date for_end = new Date();
			System.out.println("88-for_end:" + (for_end.getTime() - for_88.getTime()));
		}
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime());
	}

	@Test
	public void testUrl() {
		Date start = new Date();
		String url = "http://list.youku.com/category/show/c_84_s_6_d_1_g_%E4%BA%BA%E7%89%A9.html";
		String html = getInfo(url);
		Document document = Jsoup.parse(html);
		Elements elements = document.select("ul.panel").first().children();
		Map<String, String> map = new HashMap<>();
		Date middle = new Date();
		System.out.println(middle.getTime() - start.getTime());
		for (Element element : elements) {
			Date for_start = new Date();
			String video_url = element.getElementsByTag("a").first().attr("href");
			String image_url = element.getElementsByTag("img").first().attr("src");
			if (video_url.startsWith("//")) {
				video_url = "http:" + video_url;
			}
			Date for_81 = new Date();
			System.out.println("for_start-81:" + (for_81.getTime() - for_start.getTime()));
			String detail_html = getDetailUrl(video_url);
			Date for_84 = new Date();
			System.out.println("81-84:" + (for_84.getTime() - for_81.getTime()));
			Document detail_doc = Jsoup.parse(detail_html);
			String detail_url = detail_doc.select("a.desc-link").attr("href");
			Date for_88 = new Date();
			System.out.println("83-88:" + (for_88.getTime() - for_84.getTime()));
			if (detail_url.startsWith("//")) {
				detail_url = "http:" + detail_url;
			}
			map.put(detail_url, image_url);
			Date for_end = new Date();
			System.out.println("88-for_end:" + (for_end.getTime() - for_88.getTime()));
		}
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime());
	}

	@Test
	public void test() {
		String url = "http://v.youku.com/v_show/id_XODY0ODA2MzA4.html";
		String html = getDetailUrl(url);
		Document document = Jsoup.parse(html);
		System.out.println(document);
	}

	public String getDetailUrl(String uri) {
		String line = "";
		try {
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				if (line.contains("<a class=\"desc-link\"")) {
					line = "<html><body>" + line;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

	public String getInfo(String url) {
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