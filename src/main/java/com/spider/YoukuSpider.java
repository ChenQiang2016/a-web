package com.spider;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class YoukuSpider {

	@Test
	public void testPerson(){
		String url = "http://v.qq.com/x/list/doco?&offset=30";
		try {
			Document document = Jsoup.connect(url).get();
			Elements elements = document.select("ul.figures_list");
			System.out.println(elements.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUrl(){
		String url = "http://list.youku.com/category/show/c_84_g_%E4%BA%BA%E7%89%A9_s_1_d_2_p_1.html?spm=a2h1n.8251845.0.0";
		try {
			Document document = Jsoup.connect(url).get();
			System.out.println(document.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}