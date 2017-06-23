package com.huashu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.jsoup.JSoup;

public class CartoonParse {

	private String url = "http://all.wasu.cn/index/cid/19?p={page}";

	@Test
	public void parse() {
		Document doc = JSoup.httpGetDocument(url.replace("{page}", "1"));
		Elements divs = doc.select("div.ws_row.mb25").first().children();
		for (Element div : divs) {
			String detail_url = div.getElementsByTag("a").first().attr("href");
			System.out.println(detail_url);
		}
	}

	@Test
	public void testDetail() {
		String detail_url = "http://www.wasu.cn/Agginfo/index/id/105762";
		detail(detail_url);
	}

	public void detail(String detail_url) {
		Document doc = JSoup.httpGetDocument(detail_url);
		Element main_div = doc.select("div.mt20.f14.clearfix").first();
		Element info_div = main_div.select("div.movie_info.l").first();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<String[]> episodes = new ArrayList<String[]>();

		paramMap.put("Spiderurl", detail_url);

		String image = main_div.getElementsByTag("img").first().attr("src");
		paramMap.put("image", image);

		Elements spans = info_div.select("div.row1").first().getElementsByTag("span");
		String assetname = spans.first().text();
		paramMap.put("assetname", assetname);

		String actor = getParams(info_div, "p:contains(主演)");
		paramMap.put("actor", actor);
		String director = getParams(info_div, "p:contains(导演)");
		paramMap.put("director", director);
		String category = getParams(info_div, "p:contains(类型)");
		paramMap.put("category", category);
		String origin = getParams(info_div, "p:contains(地区)");
		paramMap.put("origin", origin);
		String year = getParams(info_div, "p:contains(年份)");
		paramMap.put("year", year);
		String introduction = info_div.select("p.rela").first().getElementsByTag("span").first().text();
		paramMap.put("introduction", introduction);

		Elements divs = doc.select("div.list_box");
		for (Element div : divs) {
			Elements lis = div.select("li.rela");
			for (Element li : lis) {
				Element yugao = li.select("span.abso_rt.yugao").first();
				if (yugao != null)
					continue;
				String[] serie = new String[4];
				serie[0] = li.getElementsByTag("a").first().attr("href");
				serie[1] = li.getElementsByTag("a").first().text();// 分集号
				serie[2] = "";
				serie[3] = ""; // 分集图片
				episodes.add(serie);
			}
		}
		// paramMap.put("episodes", episodes);
		paramMap.put("total", episodes.size());
		System.out.println(paramMap);
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
			Document doc = JSoup.httpGetDocument(url.replace("{page}", i + ""));
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
}