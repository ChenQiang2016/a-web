package com.youku;

import java.util.Date;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class VideoDetail {

	@Test
	public void detail(){
		String url = "http://list.youku.com/show/id_zf0919fb828efbfbd11ef.html?spm=a2h0j.8191423.sMain.5~5~A!2";
		
		getShowId(url);
	}

	public String getShowId(String url) {
		String showid = "";
		try {
			Document document = Jsoup.connect(url).get();
			String pageConfig = document.select("script[type=text/javascript]").last().data();
			pageConfig = pageConfig.substring(pageConfig.indexOf("{"), pageConfig.lastIndexOf("}")+1);
			System.out.println(pageConfig);
			JSONObject json = new JSONObject(pageConfig);
			showid = json.getString("showid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return showid;
	}
	
	@Test
	public void time(){
		System.out.println(new Date().getTime());
	}
}
