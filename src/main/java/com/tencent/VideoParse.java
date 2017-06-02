package com.tencent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class VideoParse {

	@Test
	public void testVideo() {
		String url = "http://v.qq.com/x/list/doco?sort=4&offset=0";

		try {
			Document document = Jsoup.connect(url).get();
			Element span = document.select("span._items").first();
			System.out.println(span.children().last().text());
			Element ul = document.select(".figures_list").first();
			Elements lis = ul.children();
			for (Element li : lis) {
				String playurl = li.getElementsByTag("a").first().attr("href");
				String image = li.getElementsByTag("img").first().attr("r-lazyload");
				String assetname = li.select("div.figure_title_score").first().getElementsByTag("a").text();
				System.out.println(playurl);
				System.out.println(image);
				System.out.println(assetname);
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDetail() {
		String url = "https://v.qq.com/x/cover/0zltminmlwcc9o6.html";
		Document document;
		try {
			document = Jsoup.connect(url).get();
			System.out.println(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testJava() {
		String uri = "https://v.qq.com/x/cover/0zltminmlwcc9o6.html";
		try {
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void iAmMyDaddy() {
		String url = "https://v.qq.com/x/cover/0zltminmlwcc9o6.html";
		Connection connect = HttpConnection.connect(url);
		connect.timeout(3000);
		connect.header("Accept-Encoding", "gzip,deflate,sdch");
		connect.header("Connection", "close");
		connect.validateTLSCertificates(false);
		try {
			connect.execute();
			Document parse = connect.get();
			System.out.println(parse.html());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
