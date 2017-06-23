package com.tencent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class Cartoon {

	@Test
	public void getDetail() {
		String url = "http://v.qq.com/detail/1/13yr8sapcmabnyc.html";
		detail(url);
	}

	public void detail(String detailUrl) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String[]> episodes = new ArrayList<String[]>();

		paramMap.put("SpiderUrl", detailUrl);
		Document doc = JSoup.httpGetDocument(detailUrl);
		Element main_div = doc.select("div.mod_figure_detail.mod_figure_detail_en.cf").first();
		String imageurl = main_div.getElementsByTag("img").first().attr("src");
		if (imageurl.startsWith("//"))
			imageurl = "http:" + imageurl;
		String assetname = main_div.select("h1.video_title_cn").first().getElementsByTag("a").first().text();
		paramMap.put("assetname", assetname); // 名称
		paramMap.put("image", imageurl); // 海报
		String origin = getParams(main_div, "span:matches(地*区)");
		String year = getParams(main_div, "span:contains(出品时间)");
		String total = getParams(main_div, "span:contains(总集数)");
		paramMap.put("origin", origin);
		paramMap.put("year", year);
		paramMap.put("total", total);

		String introduction = main_div.select("span.txt._desc_txt_lineHight").first().text();
		paramMap.put("introduction", introduction);// 简介

		String vid = detailUrl.substring(detailUrl.lastIndexOf("/") + 1, detailUrl.lastIndexOf("."));
		episodes = getEpisodes(vid, total);
		paramMap.put("episodes", episodes);
		paramMap.put("total", episodes.size());
		System.out.println(paramMap);
	}

	private String getParams(Element ele, String reg) {
		Element element = ele.select(reg).first();
		return element == null ? "" : element.parent().getElementsByTag("span").last().text();
	}

	private List<String[]> getEpisodes(String id, String total) {
		List<String[]> episodes = new ArrayList<String[]>();
		String url = "http://s.video.qq.com/get_playsource?id=" + id
				+ "&plat=2&type=4&data_type=2&video_type=3&range=1-" + total
				+ "&plname=qq&otype=json&num_mod_cnt=20&callback=_jsonp_17_038b";
		String str = Http.get(url);
		str = str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1);
		JSONObject json = new JSONObject(str);
		JSONArray data = json.getJSONObject("PlaylistItem").getJSONArray("videoPlayList");
		for (int i = 0; i < data.length(); i++) {
			JSONObject video = data.getJSONObject(i);
			String markLabelList = video.getJSONArray("markLabelList").toString();
			if(markLabelList.contains("预告")) continue;
			String[] catoon = new String[2];
			catoon[0] = video.getString("playUrl");
			catoon[1] = video.getString("title");
			episodes.add(catoon);
		}
		return episodes;
	}

	@Test
	public void get() {
		String url = "http://s.video.qq.com/get_playsource?id=13yr8sapcmabnyc&plat=2&type=4&data_type=2&video_type=3&range=1-40&plname=qq&otype=json&num_mod_cnt=20&callback=_jsonp_17_038b";
		String str = Http.get(url);
		str = str.substring(str.indexOf("{"), str.lastIndexOf("}") + 1);
		JSONObject json = new JSONObject(str);
		JSONArray data = json.getJSONObject("PlaylistItem").getJSONArray("videoPlayList");
		for(int i = 0; i < data.length(); i++){
			JSONObject video = data.getJSONObject(i);
			String markLabelList = video.getJSONArray("markLabelList").toString();
			if(markLabelList.contains("预告")) continue;
			System.out.println(markLabelList);
		}
	}

	@Test
	public void getInfo() {
		String url = "http://v.qq.com/x/list/cartoon?offset={page}&sort=4";
		int total = getTotalPage(url.replace("{page}", "1"));
		System.out.println(total);

		for (int i = 0; i <= 0; i++) {
			int page = i * 30;
			String htmlBody = Http.get(url.replace("{page}", page + ""));
			Elements lis = Jsoup.parse(htmlBody).select("ul.figures_list").first().children();
			for (Element li : lis) {
				String play_url = li.getElementsByTag("a").first().attr("href");
				String vid = play_url.substring(play_url.lastIndexOf("/") + 1, play_url.lastIndexOf("."));
				String temp = "detail/" + vid.charAt(0);
				String detail_url = play_url.replace("x/cover", temp);
				String image_url = li.getElementsByTag("img").first().attr("r-lazyload");
				if (image_url.startsWith("//"))
					image_url = "http:" + image_url;
				System.out.println(play_url + "  " + vid + "  " + detail_url + "  " + image_url);
			}
		}
	}

	private int getTotalPage(String url) {
		int total = 0;
		String html = Http.get(url);
		Document doc = Jsoup.parse(html);
		Elements as = doc.select("span._items").first().children();
		total = Integer.parseInt(as.last().text());
		return total;
	}
}
