package com.tencent;

import org.apache.log4j.PatternLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.util.Http;

public class SeriesParse {

	private String series_url = "https://v.qq.com/x/list/tv?iarea=-1&offset={page}";
	
	@Test
	public void parse(){
		int total = 1;
		
		
		int page = (total - 1) * 30;
		String response = Http.get(series_url.replace("{page}", page+""));
		Document document = Jsoup.parse(response);
		Element pager = document.select("div.mod_pages").first().select("span._items").first().children().last();
		total = Integer.parseInt(pager.text());
		
		Elements lis = document.select("ul.figures_list").first().children();
		for(Element li : lis) {
			String play_url = li.getElementsByTag("a").first().attr("href");
			String detailUrl = play_url.replace("x/cover", "detail/s");
			System.out.println(Http.get(detailUrl));
		}
		System.out.println(lis); 
		System.out.println(total);
	}
	
	@Test
	public void getDetail(){
		String detailUrl = "https://v.qq.com/detail/s/sqmkhre9cpq6t2y.html";
		String html = Http.get(detailUrl);
		Document doc = Jsoup.parse(html);
		Elements spans = doc.select("div.mod_episode").first().getElementsByTag("span");
		System.out.println(spans);
	}
	
	@Test
	public void getEpisode(){
		String id = "m8i6uooilmandtf";
		String url = "http://s.video.qq.com/get_playsource?id={id}&plat=2&type=4&data_type=2&video_type=2&range=1-200&plname=qq&otype=json&num_mod_cnt=20&callback=_jsonp_18_805c";
		getEpisodeInfo(url.replace("{id}", id));
	}

	public void getEpisodeInfo(String url) {
		String jsonString = Http.get(url);
		jsonString = jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf(")"));
		JSONObject json = new JSONObject(jsonString);
		JSONArray array = json.getJSONObject("PlaylistItem").getJSONArray("videoPlayList");
		for(int i = 0; i < array.length(); i++){
			JSONObject video = array.getJSONObject(i);
			JSONArray markLabelList = video.getJSONArray("markLabelList");
			System.out.println(video);
			String playUrl = video.getString("playUrl");
			String title = video.getString("title");
			String pic = video.getString("pic");
		}
		System.out.println(array.length());
	}
}
