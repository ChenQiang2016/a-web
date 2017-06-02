package com.youku;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class YoukuVideo {

	@Test
	public void testGetVideoDetail(){
		Map<String,String> map = new TreeMap<>();
		String url = "http://list.youku.com/show/id_za15f7a5e308111e6abda.html?spm=a2h0j.8191423.sMain.5~5~A!2";
		try {
			Document document = Jsoup.connect(url).get();
			Element thumb_element = document.select("div.p-thumb").first().getElementsByTag("img").first();
			String image = thumb_element.attr("src");
			String assetname = thumb_element.attr("alt");
			map.put("image", image);
			map.put("assetname", assetname);
			
			Elements lis = document.select("div.p-base").first().children().first().children();
			
			String origin = lis.select("li:contains(地区)").first().children().text();
			map.put("origin", origin);
			
			String category = lis.select("li:contains(类型)").first().children().text();
			map.put("category", category);
			
			String actor = lis.select("li:contains(主演)").first().attr("title");
			map.put("actor", actor);
			
			String introduction = lis.select("li:contains(简介)").first().getElementsByClass("intro-more").first().text();
			map.put("introduction", introduction);
			
			String year = lis.select("span.pub").first().ownText().split("-")[0];
			map.put("year", year);
			//清晰度
			
			String finished = "0";
			String finished_text = lis.select("li.p-row.p-renew").first().text();
			long detailSize = 1l;
			if(finished_text.indexOf("集全") > -1){
				finished = "1";
				detailSize = Long.parseLong(finished_text.substring(0, finished_text.indexOf("集全")));
			}
			map.put("finished", finished);
			System.out.println(finished_text);
			
			if("0".equals(finished)){
				Pattern p_idCount = null;
				p_idCount = Pattern.compile("<liclass=\"p-row p-renew\">更新至(\\d*)", Pattern.CASE_INSENSITIVE);// 集数
				Matcher m_idCount = p_idCount.matcher(StringUtil.replaceBlank(document.toString()));
				while (m_idCount.find()) {
					System.out.println(m_idCount.group(0));
					System.out.println(m_idCount.group(1));
					System.out.println(m_idCount.group(2));
					if (m_idCount.group(0) == null || m_idCount.group(0).length() <= 0) {
						detailSize = Long.parseLong(m_idCount.group(1));
					} else {
						detailSize = Long.parseLong(m_idCount.group(1));
					}
				}
			}
			System.out.println(detailSize);
			
			//是否完结
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
