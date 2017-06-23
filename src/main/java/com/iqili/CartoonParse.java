package com.iqili;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class CartoonParse {

	private String main_url = "http://list.iqiyi.com/www/4/-------------4-{page}-1-iqiyi--.html";

	@Test
	public void parse() {
		
		String htmlBody = JSoup.httpGetString(main_url.replace("{page}", 1+""));
		Document doc = Jsoup.parse(htmlBody);
		Elements lis = doc.select("ul.site-piclist.site-piclist-180236.site-piclist-auto").first().children();
		for(Element li : lis) {
			String detail_url = li.getElementsByTag("a").first().attr("href");
			String image = li.select("img").first().attr("src");
			System.out.println(detail_url + "   " + image);
		}
	}

	@Test
	public void testDetail(){
//		String detail_url = "http://www.iqiyi.com/a_19rrhafzq1.html#vfrm=2-4-0-1";
//		String detail_url = "http://www.iqiyi.com/a_19rrh8y3od.html#vfrm=2-4-0-1";
//		String detail_url = "http://www.iqiyi.com/a_19rrha2ush.html#vfrm=2-4-0-1";
		String detail_url = "http://www.iqiyi.com/a_19rrhaf97d.html#vfrm=2-4-0-1";
		System.out.println(getDetail(detail_url));
	}
	
	public Map<String, Object> getDetail(String detail_url){
		String html = JSoup.httpGetString(detail_url);
		if(html == null || "".equals(html)) return null;
		Document doc = Jsoup.parse(html);
		Element main_div = doc.select("div.mod_reuslt.clearfix").first();
		if(main_div == null) return null;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();
		paramMap.put("Spiderurl", detail_url);
		
		Element title_h1 = main_div.select("h1.main_title").first();
		String assetname = title_h1.getElementsByTag("a").first().text();
		String year = title_h1.getElementsByTag("span").first().text();
		String image = main_div.getElementsByTag("img").first().attr("src");
		String origin = getParams(main_div, "em:contains(地区)");
		String category = getParams(main_div, "em:contains(类型)");
		String introduction = main_div.select("span.showMoreText").last().children().first().text();
		paramMap.put("assetname", assetname);
		paramMap.put("year", year);
		paramMap.put("image", image);
		paramMap.put("origin", origin);
		paramMap.put("category", category);
		paramMap.put("introduction", introduction);
		
		String albumId = getAlbumId(doc.select("script[type=text/javascript]"));
		// TODO
		Element blockj_div = doc.select("div#block-J").first();
		if(blockj_div == null){
			//按集
			String total_str = main_div.select("em:contains(集数)").first().getElementsByTag("a").first().text();
			int total = 1, page = 1;
			Pattern p = Pattern.compile("\\d*");
			Matcher mc = p.matcher(total_str);
			while(mc.find()){
				if(mc.group() != null && !"".equals(mc.group())) {
					try {
						total = Integer.parseInt(mc.group());
						break;
					} catch (Exception e) {
					}
				}
			}
			if(total % 50 == 0) {
				page = total / 50;
			} else {
				page = total / 50 + 1;
			}
			String epidodes_url = "http://cache.video.iqiyi.com/jp/avlist/"+albumId+"/{page}/50/?albumId="+albumId+"&pageNum=50&pageNo={page}&callback=window.Q.__callbacks__.cbn6jvis";
			
			for(int i = 1; i <= page; i++) {
				episodes.addAll(getEpisodes(albumId, epidodes_url.replace("{page}", i+"")));
			}
		}else{
			//按期
			Elements as = blockj_div.select("div.choose-years-list.fl").first().children();
			if(as.size() > 0){
				Integer years[] = new Integer[as.size()];
				Pattern p = Pattern.compile("\\d*");
				for(int i = 0; i < as.size(); i++) {
					Element a = as.get(i);
					Matcher m = p.matcher(a.text());
					while(m.find()){
						if(m.group() != null && !"".equals(m.group())) {
							try {
								int total = Integer.parseInt(m.group());
								years[i] = total;
								break;
							} catch (Exception e) {
							}
						}
					}
				}
				for(int y : years){
					for(int m = 1; m <= 12; m++){
						String ym = "";
						if(m < 10){
							ym = y + "0" + m;
						}else{
							ym = ""+ y + m;
						}
						String episodes_url = "http://cache.video.iqiyi.com/jp/sdvlst/4/"+albumId+"/"+ym+"/?categoryId=4&sourceId="+albumId+"&tvYear="+ym+"&callback=window.Q.__callbacks__.cbyhwccj";
						episodes.addAll(getEpisodes(episodes_url));
					}
				}
			}
		}
		
		paramMap.put("total", episodes.size());
		paramMap.put("episodes", episodes);
		return paramMap;
	}
	
	@Test
	public void testNumber(){
		String ss = "更新至82";
		String sd = "aaa62集全";
//		System.out.println(sd.substring(0, sd.indexOf("集全")));
//		System.out.println(ss.substring(ss.lastIndexOf("更新至")));
//		Pattern p = Pattern.compile("\\d*");
		Matcher ms = Pattern.compile("(\\d*)").matcher(ss);
		Matcher md = Pattern.compile("\\d*").matcher(sd);
		while(md.find()){
			if(md.group() != null && !"".equals(md.group()))
			System.out.println("md"+md.group());
		}
		System.out.println("=====");
		while(ms.find()){
			if(ms.group() != null && !"".equals(ms.group()))
			System.out.println("ms"+ms.group());
		}
	}
	
	@Test
	public void testEp(){
		String url = "http://cache.video.iqiyi.com/jp/sdvlst/4/205177101/201706/?categoryId=4&sourceId=205177101&tvYear=201706&callback=window.Q.__callbacks__.cbyhwccj";
		getEpisodes(url);
	}

	public LinkedList<JSONObject> getEpisodes(String url) {
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();		
		String htmlBody = Http.get(url);
		if (htmlBody.contains("try") && htmlBody.contains("catch"))
			htmlBody = htmlBody.substring(htmlBody.indexOf("try"), htmlBody.lastIndexOf("catch"));
		htmlBody = htmlBody.substring(htmlBody.indexOf("(") + 1, htmlBody.lastIndexOf(")"));
		JSONObject data = new JSONObject(htmlBody);
		if("A00000".equals(data.getString("code"))){
			JSONArray list = data.getJSONArray("data");
			for(int i = 0; i < list.length(); i++) {
				JSONObject video = list.getJSONObject(i);
				String videoName = video.getString("videoName");
				String vurl = video.getString("vUrl");
				String vpic = video.getString("aPicUrl");
				String timeLength = video.getInt("timeLength") + "";
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("videoName", videoName);
				jsonObject.put("videoImage", vpic);
				jsonObject.put("playLength", timeLength);
				jsonObject.put("videoUrl", vurl);				
				jsonObject.put("videoOrder", "0");
				episodes.add(jsonObject);
			}
		}
		return episodes;
	}
	
	@Test
	public void testGetEpisodes(){
		String albumId = "203712501";
		int page = 1;
		String epidodes_url = "http://cache.video.iqiyi.com/jp/avlist/"+albumId+"/"+page+"/50/?albumId="+albumId+"&pageNum=50&pageNo="+page+"&callback=window.Q.__callbacks__.cbn6jvis";
		getEpisodes(albumId, epidodes_url);
	}
	
	public LinkedList<JSONObject> getEpisodes(String albumId, String epidodes_url){
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();
		String htmlBody = Http.get(epidodes_url);
		if (htmlBody.contains("try") && htmlBody.contains("catch"))
			htmlBody = htmlBody.substring(htmlBody.indexOf("try"), htmlBody.lastIndexOf("catch"));
		htmlBody = htmlBody.substring(htmlBody.indexOf("(") + 1, htmlBody.lastIndexOf(")"));
		JSONObject data = new JSONObject(htmlBody);
		JSONArray list = data.getJSONObject("data").getJSONArray("vlist");
		for(int i = 0; i < list.length(); i++){
			JSONObject video = list.getJSONObject(i);
			String pds = video.getString("pds");
			if (pds.contains("预"))
				continue;
			String shortTitle = video.getString("vn");
			String vurl = video.getString("vurl");
			String vpic = video.getString("vpic");
			String timeLength = video.getInt("timeLength") + "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("videoName", shortTitle);
			jsonObject.put("videoImage", vpic);
			jsonObject.put("playLength", timeLength);
			jsonObject.put("videoUrl", vurl);
			String index = "0";
			try {
				index = shortTitle.substring(shortTitle.indexOf("第") + 1, shortTitle.lastIndexOf("集"));
			} catch (Exception e) {
			}
			jsonObject.put("videoOrder", index);
			episodes.add(jsonObject);
		}
		return episodes;
	}
	
	private String getParams(Element ele, String query) {
		String result = "";
		try {
			result = ele.select(query).first().children().text();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result;
	}
	
	public String getAlbumId(Elements eles) {
		String id = "";
		try {
			String script_string = "";
			for (Element script : eles) {
				if (script.toString().contains("window.Q")) {
					script_string = script.toString();
					break;
				}
			}
			String[] array = script_string.split("\n");
			for (int i = 0; i < array.length; i++) {
				if (array[i].contains("albumId")) {
					String[] key_value = array[i].split(":");
					String value = key_value[1].trim();
					id = value.substring(0, value.length() - 1);
					break;
				}
			}
		} catch (Exception e) {
//			logger.info("albumId获取失败");
		}
		return id;
	}
	
	@Test
	public void testTotalPage() {
		System.out.println(getTotalPage());
	}

	private int getTotalPage() {
		int total = 1;
		try {
			for (int i = 0; i < 3; i++) {
				String html = JSoup.httpGetString(main_url.replace("{page}", "100"));
				if (html == null || "".equals(html)) {
					Thread.sleep(2 * 1000);
				} else {
					Document doc = Jsoup.parse(html);
					Elements as = doc.select("div.mod-page").first().children();
					Element last_page = as.get(as.size() - 2);
					total = Integer.parseInt(last_page.text());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	@Test
	public void testEpisodes(){
		String url = "http://cache.video.iqiyi.com/jp/avlist/203758901/1/50/?albumId=203758901&pageNum=200&pageNo=1&callback=window.Q.__callbacks__.cbvv6k7x";
		String htmlBody = Http.get(url);
		if (htmlBody.contains("try") && htmlBody.contains("catch"))
			htmlBody = htmlBody.substring(htmlBody.indexOf("try"), htmlBody.lastIndexOf("catch"));
		htmlBody = htmlBody.substring(htmlBody.indexOf("(") + 1, htmlBody.lastIndexOf(")"));
		System.out.println(htmlBody);
	}
}