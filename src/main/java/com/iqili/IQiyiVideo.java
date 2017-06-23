package com.iqili;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class IQiyiVideo {

	@Test
	public void testGetAlbum(){
		String url = "http://cache.video.iqiyi.com/jp/sdvlst/6/205785101/201704/?categoryId=6&sourceId=205785101&tvYear=201704&callback=window.Q.__callbacks__.cbfqjb1p";
		String result = Http.get(url);
		System.out.println(result);
	}
	
	@Test
	public void parseDoc(){
		String url = "http://www.iqiyi.com/a_19rrhakwat.html#vfrm=2-4-0-1";
		Document doc = JSoup.httpGetDocument(url);
		String id = "";
		Element number_list = doc.select("div.mod-album_tab_num.fl").first();
		if(number_list != null){//数字选集
			return;
		}
		Elements scripts = doc.select("script[type=text/javascript]");
		id = getId(scripts, "sourceId");
		String cid = getId(scripts, "cid");
		System.out.println(cid);
		String[] years = null;
		Elements as = doc.select("div.choose-years-list.fl").first().children();
		if(as != null && as.size() > 0){
			years = new String[as.size()];
			Pattern p = Pattern.compile("\\d*");
			for(int i = 0; i < as.size(); i++) {
				Matcher m = p.matcher(as.get(i).text());
				while(m.find()){
					if(m.group() != null && !"".equals(m.group())) {
						years[i] = m.group();
						break;
					}
				}
			}
			LinkedList<JSONObject> eplist = getAlbumByDate(id, years, cid);
			
			System.out.println(eplist);
		}
	}
	
	public LinkedList<JSONObject> getAlbumByDate(String id, String[] years, String cid) {
		LinkedList<JSONObject> eplist = new LinkedList<JSONObject>();
		for(String y : years){
			for(int m = 1; m <= 12; m++){
				String ym = "";
				if(m < 10){
					ym = y + "0" + m;
				}else{
					ym = ""+ y + m;
				}
				String episodes_url = "http://cache.video.iqiyi.com/jp/sdvlst/"+cid+"/"+id+"/"+ym+"/?categoryId="+cid+"&sourceId="+id+"&tvYear="+ym+"&callback=window.Q.__callbacks__.cbjv3m64";
				eplist.addAll(getEpisodes(episodes_url));
			}
		}
		return eplist;
	}
	
	public LinkedList<JSONObject> getEpisodes(String url) {
		LinkedList<JSONObject> episodes = new LinkedList<JSONObject>();
		try {
			String htmlBody = "";
			for(int i = 0; i < 3; i++) {
				htmlBody = Http.get(url);
				if(htmlBody != null && !"".equals(htmlBody)){
					break;
				}
			}

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
		} catch (Exception e) {
			System.out.println("按期获取分集失败");
		}
		return episodes;
	}

	public String getId(Elements eles, String id_key){
		String id = "";
		String script_string = "";
		for(Element script : eles){
			if(script.toString().contains("window.Q")){
				script_string = script.toString();
				break;
			}
		}
		String[] array = script_string.split("\n");
		for (int i = 0; i < array.length; i++) {
			if(array[i].contains(id_key)){
				String[] key_value = array[i].split(":");
				String value = key_value[1].trim();
				id = value.substring(0, value.length() - 1);
				break;
			}
		}
		return id;
	}
	
	@Test
	public void parse(){
		String url = "http://list.iqiyi.com/www/3/----------0---1-{page}-1--1-.html";
		try {
			for(int i = 1; i <= 30; i++){
				Document doc = null;
				for(int n = 0; n < 3; n++){
					try {
						doc = JSoup.httpGetDocument(url);
						if(doc != null){
							break;
						}
					} catch (Exception e) {
					}
				}
				Elements lis = doc.select("ul.site-piclist.site-piclist-180236.site-piclist-auto").first()
						.children();
				for (Element li : lis) {
					String detailUrl = li.select("a.site-piclist_pic_link").first().attr("href");
					String title = li.select("a.site-piclist_pic_link").first().attr("title");
					System.out.println(title+"   "+detailUrl);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	@Test
	public void getList() {
		String url = "http://list.iqiyi.com/www/3/----------0---1-{page}-1--1-.html";
		int total = 1;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			for (int i = 1; i <= total; i++) {
				System.out.println("*************************************正在解析第【" + i
						+ "】页数据*************************************");
				HttpGet get = new HttpGet(url.replace("{page}", i + ""));
				response = client.execute(get);
				Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
				Elements page = document.select("div.mod-page").first().children();
				Element total_element = page.get(page.size() - 2);
				total = Integer.parseInt(total_element.text());

				Elements lis = document.select("ul.site-piclist.site-piclist-180236.site-piclist-auto").first()
						.children();
				for (Element li : lis) {
					String detailUrl = li.select("a.site-piclist_pic_link").first().attr("href");
					try {
						getInfo(detailUrl);
					} catch (Exception e) {
						System.out.println("!!!!!!!!!!!!!!!!!!!!" + detailUrl + "解析失败!!!!!!!!!!!!!!!!!!!!");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
				if (client != null)
					client.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Test
	public void getAlbumList() {
		String detail_url = "http://cache.video.qiyi.com/jp/sdvlst/3/200385201/?categoryId=3&sourceId=200385201&callback=window.Q.__callbacks__.cbdroeai";
		try {
			Document document = Jsoup.connect(detail_url).get();
			System.out.println(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getInfo() {
		getAlbum("200385201", false);
//		getAlbum("205414901", true);
	}

	public void getAlbum(String id, boolean isAvlist) {
		String url = "";
		if (isAvlist) {
			url = "http://cache.video.qiyi.com/jp/avlist/" + id + "/1/200/?albumId=" + id + "&pageNum=200&pageNo=1";
		} else {
			url = "http://cache.video.qiyi.com/jp/sdvlst/3/" + id + "/?categoryId=3&sourceId=" + id + "";
		}
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			HttpGet get = new HttpGet(url);
			response = client.execute(get);
			String result = EntityUtils.toString(response.getEntity());
			result = result.substring(result.indexOf("{"));
			System.out.println(result);
			JSONObject jsonObject = new JSONObject(result);
			JSONArray array = null;
			if (isAvlist) {
				array = jsonObject.getJSONObject("data").getJSONArray("vlist");
			} else {
				array = jsonObject.getJSONArray("data");
			}
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				String[] episode = new String[4];
				if(isAvlist){
					episode[0] = obj.getString("vurl");
					episode[1] = obj.getString("vpic");
					episode[3] = obj.getString("desc");
				}else{
					episode[0] = obj.getString("vUrl");
					episode[1] = obj.getString("aPicUrl");
					episode[3] = obj.getString("aDesc");
				}
				episode[2] = obj.getString("shortTitle"); // 资产名
				System.out.println(episode[0]+","+episode[1]+","+episode[2]+","+episode[3]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
				if (client != null)
					client.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Test
	public void getDetail() {
		getInfo("http://www.iqiyi.com/lib/m_210047414.html?src=search");
	}

	@Test
	public void testPage(){
		String url = "http://www.iqiyi.com/a_19rrha7li1.html#vfrm=2-4-0-1";
//		String url = "http://www.iqiyi.com/a_19rrh9w709.html#vfrm=2-4-0-1";
		try {
			Document document = Jsoup.connect(url).get();
			Element div = document.select("div.mod-album_tab_num.fl").first();
			if(div == null){
				System.out.println("1111");
			}else{
				System.out.println(div);
			}
			Elements scripts = document.select("script[type=text/javascript]");
			String script_string = "";
			for(Element script : scripts){
				if(script.toString().contains("window.Q")){
					script_string = script.toString();
					break;
				}
			}
			String[] array = script_string.split("\n");
			for (int i = 0; i < array.length; i++) {
				if(array[i].contains("albumId")){
					String[] key_value = array[i].split(":");
					String value = key_value[1].trim();
					System.out.println(key_value[0].trim()+":"+value.substring(0, value.length() - 1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getInfo(String detail_url) {
		Map<String, Object> paramMap = new HashMap<>();
		String tvName = "";
		String tvPictureUrl = "";
		String tvDesc = "";
		String categoryKeywords = "";
		String copyright = "";

		String type_query = "";
		String area_query = "";
		try {
			Document document = Jsoup.connect(detail_url).get();
			Elements big_div = document.select("div#block-B");
			Element info_div = null;
			if (big_div != null && big_div.size() > 0 && !big_div.first().attr("data-block-name").equals("导航")) {
				Element image_div = big_div.select("div.album-picCon.album-picCon-onePic").first();
				info_div = big_div.select("div.album-msg").first();

				tvPictureUrl = image_div.getElementsByTag("img").first().attr("src");
				tvName = image_div.select("a.white").first().text();

				Elements span_text = info_div.select("span.bigPic-b-jtxt");
				tvDesc = getParams(span_text, true, true);

				type_query = "p:contains(类型)";
				area_query = "p:contains(地区)";
			} else {
				Element detail_info = document.select("div.mod_reuslt.clearfix").first();
				Element image_div = detail_info.select("div.result_pic.pr").first();
				info_div = detail_info.select("div.result_detail").first();

				tvPictureUrl = image_div.select("img").first().attr("src"); // 海报
				tvName = info_div.select("h1.main_title").first().getElementsByTag("a").first().text(); // 资产名

				Elements span_text = info_div.select("span.showMoreText");
				tvDesc = getParams(span_text, false, false);

				type_query = "em:contains(类型)";
				area_query = "em:contains(地区)";
			}
			Elements type_elements = info_div.select(type_query);
			categoryKeywords = getParams(type_elements, false, true);

			Elements area_elements = info_div.select(area_query);
			copyright = getParams(area_elements, false, true);

			paramMap.put("introduction", tvDesc);
			paramMap.put("image", tvPictureUrl);
			paramMap.put("assetname", tvName);
			paramMap.put("source", copyright);
			paramMap.put("categoryKeywords", categoryKeywords);
			System.out.println(paramMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getParams(Elements eles, boolean isDesc, boolean isFirst) {
		String result = "";
		if (eles != null && eles.size() > 0)
			if (isDesc) {
				result = eles.last().text();
			} else {
				if (isFirst) {
					result = eles.first().children().first().text();
				} else {
					result = eles.last().children().first().text();
				}
			}
		return result;
	}
}
