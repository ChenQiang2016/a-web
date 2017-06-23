package com.iqili;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;

public class VarietyParse {

	String url = "http://list.iqiyi.com/www/6/-------------11-{page}-1-iqiyi--.html";

	@Test
	public void parse() {

	}

	@Test
	public void testGetTotalPage() {
		System.out.println(totalPage());
	}

	public int totalPage() {
		int total = 1;
		for (int i = 0; i < 3; i++) {
			try {
				Document doc = JSoup.httpGetDocument(url.replace("{page}", "100"));
				if (doc != null) {
					Elements lis = doc.select("div.mod-page").first().children();
					String last_page = lis.get(lis.size() - 2).text();
					total = Integer.parseInt(last_page);
					break;
				}
				Thread.sleep(2 * 1000);
			} catch (Exception e) {
				System.out.println("异常");
			}
		}
		return total;
	}
}
