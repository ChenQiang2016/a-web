package com.iqili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class SeriesParse {

	String url = "http://list.iqiyi.com/www/2/-------------11-{page}-1-iqiyi--.html";

	@Test
	public void testDetail() {
//		 String detail_url =
//		 "http://www.iqiyi.com/a_19rrk2hct9.html#vfrm=2-4-0-1";
//		String detail_url = "http://www.iqiyi.com/a_19rrhali49.html#vfrm=2-4-0-1";
		 String detail_url =
		 "http://www.iqiyi.com/a_19rrhanumx.html#vfrm=2-4-0-1";
		Map<String, Object> map = detail(detail_url);
		System.out.println(map);
	}

	public Map<String, Object> detail(String detail_url) {
		String assetname = "", image = "", year = "", origin = "", category = "", actor = "", director = "", introduction = "";
		int total = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<JSONObject> episodes = new ArrayList<JSONObject>();
		Document doc = JSoup.httpGetDocument(detail_url);

		if (doc.select("ul.focus_img_list").first() == null) {
			// 原始版本
			Element main_div = doc.select("div.album-head-info").first();
			image = main_div.getElementsByTag("img").first().attr("src");

			Element info_div = main_div.select("div.info-intro").first();
			assetname = info_div.select("h1").first().text().split(" ")[0];

			String update_str = info_div.select("span.update-progress").first().text();
			if (!update_str.contains("更新")) {
				paramMap.put("update", "1"); // 是否已完结
			}
			total = Integer.parseInt(info_div.select("span.update-progress").first().getElementsByTag("em").first().text().replace("集", ""));

			Element params_div = doc.select("div.episodeIntro-item.clearfix").first();
			year = getParams(params_div, "em:contains(年份)", false);
			origin = getParams(params_div, "em:contains(地区)", false);
			category = getParams(params_div, "em:contains(类型)", false);
			actor = getParams(params_div, "em:contains(主演)", false);
			director = getParams(params_div, "em:contains(导演)", false);
			introduction = info_div.select("span.briefIntroTxt").last().text();
		} else {
			if (doc.select("div.info-intro").first() == null) {
				// 最新模式
				Element li = doc.select("ul.focus_img_list").first().getElementsByTag("li").first();
				String image_str = li.attr("style");
				image = image_str.substring(image_str.indexOf("(") + 1, image_str.lastIndexOf("") - 1);
				Element title_div = doc.select("div.album-playArea.clearfix").first();
				assetname = title_div.getElementsByTag("dt").first().getElementsByTag("a").first().text();

				String update_str = title_div.getElementsByTag("dd").first().text();
				if (!update_str.contains("更新")) {
					paramMap.put("update", "1"); // 是否已完结
				}
				total = Integer.parseInt(title_div.getElementsByTag("dd").first().getElementsByTag("em").first().text().replace("集", ""));

				Element info_div = doc.select("div.msg-bd.fs12").first();
				year = getParams(info_div, "p:contains(年份)", true);
				origin = getParams(info_div, "p:contains(地区)", true);
				category = getParams(info_div, "p:contains(类型)", true);
				actor = getParams(info_div, "p:contains(主演)", true);
				director = getParams(info_div, "p:contains(导演)", true);
				introduction = info_div.select("span.bigPic-b-jtxt").last().text();
			} else {
				// 中间模式
				Element li = doc.select("ul.focus_img_list").first().getElementsByTag("li").first();
				String image_str = li.attr("style");
				image = image_str.substring(image_str.indexOf("(") + 1, image_str.lastIndexOf("") - 1);

				Element main_div = doc.select("div.album-head-info").first();
				Element info_div = main_div.select("div.info-intro").first();
				assetname = info_div.select("h1").first().text().split(" ")[0];

				String update_str = info_div.select("span.update-progress").first().text();
				if (!update_str.contains("更新")) {
					paramMap.put("update", "1"); // 是否已完结
				}
				total = Integer.parseInt(info_div.select("span.update-progress").first().getElementsByTag("em").first().text().replace("集", ""));

				Element params_div = doc.select("div.episodeIntro-item.clearfix").first();
				year = getParams(params_div, "em:contains(年份)", false);
				origin = getParams(params_div, "em:contains(地区)", false);
				category = getParams(params_div, "em:contains(类型)", false);
				actor = getParams(params_div, "em:contains(主演)", false);
				director = getParams(params_div, "em:contains(导演)", false);
				introduction = info_div.select("span.briefIntroTxt").last().text();
			}
		}

		int totalPage = 1;
		if (total % 50 == 0) {
			totalPage = total / 50;
		} else {
			totalPage = total / 50 + 1;
		}
		String albumId = getAlbumId(doc.select("script[type=text/javascript]"));
		String episodes_url = "http://cache.video.iqiyi.com/jp/avlist/"+albumId+"/{page}/50/?albumId=" + albumId
				+ "&pageNum=50&pageNo={page}&callback=window.Q.__callbacks__.cb6rt7ee";

		for (int i = 1; i <= totalPage; i++) {
			String url = episodes_url.replace("{page}", "" + i);
			episodes.addAll(getEpisodes(url));
		}

		paramMap.put("assetname", assetname);
		paramMap.put("image", image);
		paramMap.put("year", year);
		paramMap.put("origin", origin);
		paramMap.put("category", category);
		paramMap.put("actor", actor);
		paramMap.put("director", director);
		paramMap.put("introduction", introduction);

		paramMap.put("episodes", episodes);
		paramMap.put("total", episodes.size());

		return paramMap;
	}

	public String getAlbumId(Elements eles) {
		String id = "";
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
		return id;
	}

	private String getParams(Element ele, String query, boolean isChildren) {
		String result = "";
		try {
			if (isChildren) {
				result = ele.select(query).first().children().text();
			} else {
				result = ele.select(query).first().nextElementSibling().text();
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result;
	}

	@Test
	public void parse() {
		int totalPage = 2;
		String url = "http://cache.video.iqiyi.com/jp/avlist/205087501/{page}/50/?albumId=205087501&pageNum=50&pageNo={page}&callback=window.Q.__callbacks__.cb6rt7ee";
		for (int i = 1; i <= totalPage; i++) {
			getEpisodes(url.replace("{page}", i + ""));
		}
	}

	public LinkedList<JSONObject> getEpisodes(String url) {
		LinkedList<JSONObject> episodes = new LinkedList<>();
		String htmlBody = Http.get(url);
		if (htmlBody.contains("try") && htmlBody.contains("catch"))
			htmlBody = htmlBody.substring(htmlBody.indexOf("try"), htmlBody.lastIndexOf("catch"));
		htmlBody = htmlBody.substring(htmlBody.indexOf("(") + 1, htmlBody.lastIndexOf(")"));
		JSONObject json = new JSONObject(htmlBody);
		JSONArray list = json.getJSONObject("data").getJSONArray("vlist");
		for (int j = 0; j < list.length(); j++) {
			JSONObject video = list.getJSONObject(j);
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

	@Test
	public void testGetTotalPage() {
		getTotalPage(url);
	}

	public int getTotalPage(String url) {
		int totalPage = 1;
		String uri = url.replace("{page}", "1000");
		Document doc = JSoup.httpGetDocument(uri);
		Elements as = doc.select("div.mod-page").first().children();
		Element last_span = as.last();
		System.out.println(last_span);
		return totalPage;
	}

	@Test
	public void test() {
		Document doc = JSoup.httpGetDocument("http://list.iqiyi.com/www/2/-------------11-1-1-iqiyi--.html");
		Element type = doc.select("div.mod_sear_list").get(1);
		Element li = type.select("li.selected").first();
		System.out.println(li.nextElementSibling());
		System.out.println();
		System.out.println(type);
	}
}
