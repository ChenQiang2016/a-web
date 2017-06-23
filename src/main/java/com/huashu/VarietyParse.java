package com.huashu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;
import com.util.Http;

public class VarietyParse {

	private String url = "http://all.wasu.cn/index/sort/time/class/program/cid/37?p={page}";

	@Test
	public void parse() {
		int totalPage = getPageNumber(url);
		System.out.println(totalPage);
		Document doc = JSoup.httpGetDocument(url.replace("{page}", "1"));
		Elements divs = doc.select("div.ws_row.mb25").first().children();
		for (Element div : divs) {
			String detail_url = div.getElementsByTag("a").first().attr("href");
			System.out.println(detail_url);
		}
	}

	@Test
	public void getDetail(){
		String detail_url = "http://www.wasu.cn/Agginfo/index/id/33839";
		detail(detail_url);
	}
	
	public void detail(String url){
		Document doc = JSoup.httpGetDocument(url);
		Element main_div = doc.select("div.mt20.f14.clearfix").first();
		Element info_div = main_div.select("div.movie_info.l").first();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String[]> episodes = new ArrayList<String[]>();

		paramMap.put("Spiderurl", url);

		String image = main_div.getElementsByTag("img").first().attr("src");
		paramMap.put("image", image);

		Elements spans = info_div.select("div.row1").first().getElementsByTag("span");
		String assetname = spans.first().text();
		paramMap.put("assetname", assetname);
		
		String actor = getParams(info_div, "p:contains(主持人|嘉宾)");
		paramMap.put("actor", actor);
		String category = getParams(info_div, "p:contains(类型)");
		paramMap.put("category", category);
		String origin = getParams(info_div, "p:contains(地区)");
		paramMap.put("origin", origin);
		
		String introduction = info_div.select("p.rela").first().getElementsByTag("span").first().text();
		paramMap.put("introduction", introduction);
		
//		paramMap.put("episodes", episodes);
		paramMap.put("total", episodes.size());
		System.out.println(paramMap);
		
		Element params_li = main_div.select("div.remove_box").first().children().first();
		String onclick_str = params_li.attr("onclick");
		onclick_str = onclick_str.substring(onclick_str.indexOf("(")+1, onclick_str.lastIndexOf("")-2);
		onclick_str = onclick_str.replace("'", "").replace("\"", "");
		String params[] = onclick_str.split(",");
		String id = params[0];
		String sid = params[1];
		System.out.println(id+","+sid);
	}
	
	public String getParams(Element info_div, String queryString) {
		String result = "";
		try {
			result = info_div.select(queryString).first().children().text();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	@Test
	public void getTotalPage() {
		System.out.println(getPageNumber(url));
	}

	public int getPageNumber(String url) {
		int total = 1;
		for (int i = 100; i > 0;) {
			Document doc = JSoup.httpGetDocument(url.replace("{page}", i+""));
			Elements as = doc.select("div.item_page").first().children();
			Element next_page = as.last();
			if (next_page.attr("href") == null || "".equals(next_page.attr("href"))) {
				Element last_page = as.get(as.size() - 2);
				total = Integer.parseInt(last_page.text());
				break;
			}
			i += 20;
		}
		return total;
	}
	
	@Test
	public void getEpisodes(){
		String detail_url = "http://www.wasu.cn/Agginfo/index/id/33839";
		Document document = JSoup.httpGetDocument(detail_url);
		Element main_div = document.select("div.mt20.f14.clearfix").first();
		Element params_li = main_div.select("div.remove_box").first().children().first();
		String onclick_str = params_li.attr("onclick");
		onclick_str = onclick_str.substring(onclick_str.indexOf("(")+1, onclick_str.lastIndexOf("")-2);
		onclick_str = onclick_str.replace("'", "").replace("\"", "");
		String params[] = onclick_str.split(",");
		String id = params[0];
		String sid = params[1];
		
		
		
		episodes(id, sid, "", "");
	}

	public void episodes(String id, String sid, String year, String month) {
		String episodes_url = "http://www.wasu.cn/AggColumn/AjaxAggColumnData?id={id}&sid={sid}&source=wasu&flag=3&year={year}&month={month}";
		episodes_url = episodes_url.replace("{id}", id);
		episodes_url = episodes_url.replace("{sid}", sid);
		episodes_url = episodes_url.replace("{year}", year);
		episodes_url = episodes_url.replace("{month}", month);
		String str = Http.get(episodes_url);
		boolean is_no_data = false;
		if(str == null || "".equals(str)) is_no_data = true;
		
		JSONObject json = new JSONObject(str);
		if(json.getString("dataHtml") == null || "".equals(json.getString("dataHtml"))) is_no_data = true;
		
		if(is_no_data){
			System.out.println(id+"-"+sid+"-"+year+"-"+month+" : no data");
		} else {
			System.out.print(id+"-"+sid+"-"+year+"-"+month+" : ");
			Document doc = Jsoup.parse(json.getString("dataHtml"));
			Elements lis = doc.getElementsByTag("li");
			for(Element li : lis) {
				String playurl = li.getElementsByTag("a").first().attr("href");
				String imgurl = li.getElementsByTag("img").first().attr("src");
				String sectitle = li.select("div.pic_info").first().getElementsByTag("a").first().text();
				String date = li.select("p.abso_lb.pic_shadow").first().text();
				
				JSONObject episodeJsonObject = new JSONObject();
				episodeJsonObject.put("imgurl", imgurl);
				episodeJsonObject.put("playurl", playurl);
				episodeJsonObject.put("sectitle", sectitle);// 主题
				episodeJsonObject.put("date", date);// 首播时间
				episodeJsonObject.put("brief", "");
				episodeJsonObject.put("actor", "");
				System.out.println(episodeJsonObject);
			}
		}
	}
	
	@Test
	public void getYearAndMonthList(){
		String detail_url = "http://www.wasu.cn/Agginfo/index/id/51753";
		Document document = JSoup.httpGetDocument(detail_url);
		Element main_div = document.select("div.mt20.f14.clearfix").first();
		Element params_li = main_div.select("div.remove_box").first().children().first();
		String onclick_str = params_li.attr("onclick");
		onclick_str = onclick_str.substring(onclick_str.indexOf("(")+1, onclick_str.lastIndexOf("")-2);
		onclick_str = onclick_str.replace("'", "").replace("\"", "");
		String params[] = onclick_str.split(",");
		String id = params[0];
		String sid = params[1];
		String[] years = null;
		Elements lis = document.select("ul#year_list_ul").first().getElementsByTag("li");
//		if(lis == null || lis.size() < 1) break;
		years = new String[lis.size()];
		for(int i = 0; i < lis.size(); i++) {
			Element li = lis.get(i);
			String year = li.getElementsByTag("a").first().text();
			years[i] = year;
		}
		
		for(String year : years) {
			for(int i = 1; i <= 12; i++) {
				episodes(id, sid, year, i+"");
			}
		}
	}
	
	@Test
	public void testEpisodes(){
		String episodes_url = "http://www.wasu.cn/AggColumn/AjaxAggColumnData?id=40426&sid=18192&source=wasu&flag=3&year=2017&month=9";
		String str = Http.get(episodes_url);
		System.out.println(str);
	}
}
