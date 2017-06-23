package com.bilibili;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class CartoonVideo {

	private static String BILI_CARTOON_INDEX_URL = "http://bangumi.bilibili.com/web_api/season/index_global?page={page}&page_size=20&version=0&is_finish=0&start_year=0&tag_id=&index_type=1&index_sort=0&quarter=0";

	@Test
	public void getDetail() {
		String detail_url = "http://bangumi.bilibili.com/anime/959";
		String video_id = detail_url.substring(detail_url.lastIndexOf("/"));
		System.out.println(video_id);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			Document doc = Jsoup.connect(detail_url).get();
			// 获取标题
			String infoTitle = doc.select("h1.info-title").first().text();
			paramMap.put("assetname", infoTitle);
			// 获取封面图
			String imgUrl = doc.select("div.bangumi-preview").first().getElementsByTag("img").first().attr("src");
			if (imgUrl.startsWith("//")) {
				imgUrl = "http:" + imgUrl;
			} else if (!imgUrl.startsWith("http://")) {
				imgUrl = "http://" + imgUrl;
			}
			paramMap.put("image", imgUrl);
			// 获取介绍
			String infoDesc = doc.select("div.info-desc").first().text();
			paramMap.put("introduction", infoDesc);
			// 获取年份
			String year = doc.select("div.info-row.info-update").first().getElementsByTag("span").first().text();
			// 状态
			String status = doc.select("div.info-row.info-update").first().getElementsByTag("span").last().text();
			// 年份
			paramMap.put("year", year.substring(0, 4));// 上映日期
			// 是否连载
			// paramMap.put("finished", SeriesSchema.FINISHED); // 已完结
			// if (StringUtils.isNotBlank(status) && status.contains("连载中")) {
			paramMap.put("finished", status); // 连载中
			// }
			List<String[]> episodes = getEpisodeDetail(video_id);
			paramMap.put("episodes", episodes);
			paramMap.put("total", episodes.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void test(){
		getEpisodeDetail("959");
	}
	
	private List<String[]> getEpisodeDetail(String id) {
		List<String[]> list = new ArrayList<>();
		String url = "http://bangumi.bilibili.com/jsonp/seasoninfo/{id}.ver?callback=seasonListCallback&jsonp=jsonp&_=";
		url = url.replace("{id}", id) + new Date().getTime();
		String result = get(url);
		result = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
		JSONObject data = new JSONObject(result);
		JSONArray episodes = data.getJSONObject("result").getJSONArray("episodes");
		for(int i = 0; i < episodes.length(); i++){
			JSONObject episode = episodes.getJSONObject(i);
			String[] temp = new String[4];
			temp[0] = episode.getString("webplay_url");//分集url
			temp[1] = episode.getString("index");// 分集顺序
			temp[2] = episode.getString("index_title");// 分集介绍
			temp[3] = episode.getString("cover");// 分集图片
			list.add(temp);
		}
		return list;
	}

	private String get(String url) {
		String result = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			response = client.execute(get);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null)
					client.close();
				if (response != null)
					response.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	@Test
	public void getVartoon() {
		int total = 1;
		try {
			for (int i = 1; i <= total; i++) {
				String jsonResult = get(BILI_CARTOON_INDEX_URL.replace("{page}", i + ""));
				JSONObject json = new JSONObject(jsonResult);
				JSONObject data = json.getJSONObject("result");
				total = data.getInt("pages");
				JSONArray list = data.getJSONArray("list");
				for (int j = 0; j < list.length(); j++) {
					JSONObject video = list.getJSONObject(j);
					System.out.println(video);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}