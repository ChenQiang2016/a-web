package com.jsoup;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.spider.HttpClient;

public class JSoup {

	
	
	@Test
	public void testHtml(){
		String url = "http://v.youku.com/v_show/id_XMjc3ODkzNDU1Ng==.html";
		String html =HttpClient.getInfo(url);
		Document document = Jsoup.parse(html);
		System.out.println(document.select("a.desc-link").attr("href"));
//		Element element = document.select("ul.panel").first();
//		System.out.println(element.toString());
//		
//		Elements elements = element.children();
//		for(Element ele : elements){
//			System.out.println(ele.getElementsByTag("a").first().attr("href"));
//			System.out.println(ele.getElementsByTag("img").first().attr("src"));
//		}
	}
	
	@Test
	public void testString(){
		String ss = "//v.youku.com/v_show/id_XMTY2NjM5NDMyNA==.html";
		System.out.println(ss.startsWith("//"));
	}
	
	@Test
	public void testUrl(){
		String url = "http://v.youku.com/v_show/id_XMjc3ODkzNDU1Ng==.html";
		String html =HttpClient.getInfo(url);
		Document document = Jsoup.parse(html);
		Elements elements = document.children();
		for(Element ele : elements){
			System.out.println(ele.getElementsByTag("a").first().attr("href"));
			System.out.println(ele.getElementsByTag("img").first().attr("src"));
		}
	}
}
