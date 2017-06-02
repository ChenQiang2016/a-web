package com.tencent;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class Doco {

	private static String searchUrl = "https://v.qq.com";

	@Test
	public void parse() {
		String url = "https://v.qq.com/x/cover/0zltminmlwcc9o6.html";
		String vid;
		try {
			Document document = Jsoup.connect(url).get();
			Elements lis = document.select("ul.figures_list._video_list_ul").first().children();
			for (Element li : lis) {
				String albumUrl = searchUrl + li.getElementsByTag("a").first().attr("href");
				vid = albumUrl.substring(albumUrl.lastIndexOf("/") + 1, albumUrl.lastIndexOf("."));
//				System.out.println(albumUrl + "   " + vid);
				String albumImage = "http:" + li.select("img").first().attr("r-lazyload");
			}
			System.out.println(lis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void desc(){
		String vid = "u050902bjjk";
		String url = "http://sns.video.qq.com/tvideo/fcgi-bin/video?otype=json&vid="+vid;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			response = client.execute(post);
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
