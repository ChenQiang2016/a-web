package com.tencent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.util.Http;

public class VarietyParse {

	private static String url = "http://v.qq.com/x/list/variety?sort=4&offset={page}";
	
	public static void parse(){
		int total = getTotalPage();
		try {
			System.out.println(total);
			Document doc = Jsoup.connect(url.replace("{page}", 0+"")).get();
			Elements lis = doc.select("ul.figures_list").first().children();
			for(Element li : lis) {
				String play_url = li.getElementsByTag("a").first().attr("href");
				String html = Http.get(play_url);
				Element video_detail = Jsoup.parse(html).select("div.video_detail").first();
				String detail_url = video_detail.getElementsByTag("a").first().attr("href");
				String lid = detail_url.substring(detail_url.lastIndexOf("/") + 1, detail_url.lastIndexOf("."));
				System.out.println(lid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int getTotalPage() {
        int total = 1;
        try {
            Document document = Jsoup.connect(url.replace("{page}", 0+"")).get();
            Element pager = document.select("div.mod_pages").first().select("span._items").first().children().last();
            total = Integer.parseInt(pager.text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
	
	@Test
	public void test(){
		try {
			String url = "https://list.video.qq.com/fcgi-bin/list_common_cgi?otype=json&novalue=1&nounion=0&platform=1&version=10000&intfname=web_integrated_lid_list&tid=543&appkey=ebe7ee92f568e876&appid=20001174&sourceid=10001&listappid=10385&listappkey=10385&playright=2&sourcetype=1&cidorder=1&locate_type=0&callback=jQuery"
					+ "&lid=55228&pagesize=10&offset=0";
			String result = Http.get(url);
			result = result.substring(result.indexOf("{"), result.lastIndexOf("}")+1);
			JSONObject json = new JSONObject(result);
			int total = json.getInt("total");
			System.out.println(total);
			JSONArray data = json.getJSONObject("jsonvalue").getJSONArray("results");
			System.out.println(data.length());
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		parse();
	}
}