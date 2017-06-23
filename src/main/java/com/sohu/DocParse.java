package com.sohu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class DocParse {

	String main_url = "http://so.tv.sohu.com/list_p1107_p2_p3_p4-1_p5_p6_p73_p80_p9_p10{page}_p11_p12_p13.html";

	@Test
	public void testDetail(){
//		String url = "http://tv.sohu.com/s2013/dajiemi/";
//		String url = "http://tv.sohu.com/s2012/jqjm/";
		String url = "http://tv.sohu.com/s2017/jlpkjdl/";
		String htmlBody = Http.get(url, "GB2312");
		System.out.println(htmlBody);
	}
	
	@Test
	public void parse() {
		String htmlBody = JSoup.httpGetString(main_url.replace("{page}", 1 + ""));
		Document doc = Jsoup.parse(htmlBody);
		Elements lis = doc.select("ul.st-list.cfix").first().children();
		for (Element li : lis) {
			String detail_url = li.getElementsByTag("a").first().attr("href");
			if (detail_url.startsWith("//"))
				detail_url = "http:" + detail_url;
			System.out.println(detail_url);
		}
	}

	@Test
	public void detail() {
		String url = "http://tv.sohu.com/s2016/jlphxxgx/";
		System.out.println(parserUrl(url));
	}

	public Map<String, Object> parserUrl(String detail_url) {
		Map<String, Object> seriesMap = new HashMap<String, Object>();
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();
		String htmlBody = Http.get(detail_url, "GB2312");
		Document doc = Jsoup.parse(htmlBody);
		getEpisodes(doc);
		String vid = "", pid = "", assetname = "", spiderurl = detail_url, is_finish = "1", HD = "0";
		Element info_div = doc.select("div.blockRA.bord.clear").first();
		if (info_div == null)
			return null;
		Element t = info_div.getElementsByTag("span").first();
		assetname = t.text();
		String image = getImage(doc, "div#picFocus");
		String category = info_div.select("p:contains(类型)").first().children().text();
		String description = info_div.select("div.d1").first().text();

		Element update_div = doc.select("div.d1.clear").first();
		if (update_div.select("div.l").first().text().contains("更新至")) {
			is_finish = "0";
		}

		Element definition_div = update_div.select("div.r").first();
		if (definition_div.text().contains("超清")) {
			HD = "2";
		} else if (definition_div.text().contains("高清")) {
			HD = "1";
		}

		seriesMap.put("category", category);
		seriesMap.put("image", image);
		seriesMap.put("assetname", assetname);
		seriesMap.put("description", description);
		seriesMap.put("spiderurl", spiderurl);
		seriesMap.put("is_hd", HD);
		seriesMap.put("is_finish", is_finish);
		seriesMap.put("total", 1);
		seriesMap.put("episodes", episodes);

		return seriesMap;
	}

	public Object getEpisodes(Document documet) {
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();
		Element list_element = documet.select("div#list_asc").first().select("div.pp.similarLists").first();
		Elements lis = list_element.getElementsByTag("li");
		for(Element li : lis) {
			String episodesname = li.getElementsByTag("strong").first().getElementsByTag("a").first().text();
			String spiderurl = li.getElementsByTag("a").first().attr("href");
			String length = "";
			String thumbnail = "";
			String introduction = episodesname;
			String smallpic = li.getElementsByTag("img").first().attr("src");
			System.out.println(episodesname + "   " + spiderurl + "   " + smallpic);
//			episodesname = ConvertUtil.NVL(documentary.get("videoName"), "");
//			spiderurl = ConvertUtil.NVL(documentary.get("videoUrl"), "");
//			length = ConvertUtil.NVL(documentary.get("videoPlayTime"), "");
//			thumbnail = ConvertUtil.NVL(documentary.get("videoBigPic"), "");
//			introduction = ConvertUtil.NVL(documentary.get("videoDesc"), "");
//			smallpic = ConvertUtil.NVL(documentary.get("videoSmallPic"), "");
		}
		return episodes;
	}

	public String getImage(Document doc, String queryString) {
		String image = "";
		try {
			image = doc.select(queryString).first().getElementsByTag("img").first().attr("src");
		} catch (Exception e) {
		}
		return image;
	}

	@Test
	public void testGetTotalPage() {
		System.out.println(totalPage());
	}

	public int totalPage() {
		int total = 1;
		String htmlBody = JSoup.httpGetString(main_url.replace("{page}", 1 + ""));
		Document doc = Jsoup.parse(htmlBody);
		Elements as = doc.select("div.ssPages.area").first().getElementsByTag("a");
		total = Integer.parseInt(as.get(as.size() - 2).text());
		return total;
	}

	@Test
	public void testEpisodes() {
		String url = "http://tv.sohu.com/s2010/jdcq/";
		String body = Http.get(url, "GB2312");
		Document doc = Jsoup.parse(body);
		getEpisodes(doc);
	}
}