package com.youku;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class VideoEpisode {

	@Test
	public void getVideoEpisode(){
		String detail_url = "http://list.youku.com/show/id_za15f7a5e308111e6abda.html?spm=a2h0j.8191423.sMain.5~5~A!2";
		
		String showid = getShowId(detail_url);
		
		String url = "http://list.youku.com/show/point?id={showid}&stage=reload_{page}&callback=jQuery";
		
		url = url.replace("{showid}", showid);
		
		for(int i = 0; i < 2; i++){
			int page = i * 40 + 1;
			CloseableHttpClient client = null;
			CloseableHttpResponse response = null;
			
			try {
				client = HttpClients.createDefault();
				HttpGet get = new HttpGet(url.replace("{page}", page+""));
				
				response = client.execute(get);
				String html = EntityUtils.toString(response.getEntity());
				html = html.substring(html.indexOf("(")+1, html.lastIndexOf(")"));
				JSONObject json = new JSONObject(html);
				html = json.getString("html");
				Document document = Jsoup.parse(html);
				Elements divs = document.select("div.p-item");
				String[] temp = null;
				for(Element div : divs){
					temp = new String[5];
					Element a = div.getElementsByTag("a").first();
					String play_url = a.attr("href");
					if(play_url.startsWith("//")){
						play_url = "http:" + play_url;
					}
					String title = a.attr("title");
					if(title.contains("预告片")){
						continue;
					}
					
					String[] arr = title.split(" ");
					title = arr[arr.length - 1];
					String image = div.getElementsByTag("img").first().attr("src");
					
					String time = div.select("span.p-time").first().text();
					
					temp[0] = play_url;
					temp[1] = image;
					temp[2] = title;
					temp[3] = time;
					temp[4] = "高清";
					System.out.print(temp[0]+"  ");
					System.out.print(temp[1]+"  ");
					System.out.print(temp[2]+"  ");
					System.out.print(temp[3]+"  ");
					System.out.println(temp[4]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					response.close();
					client.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	public String getShowId(String url){
		String showid = "";
		try {
			Document document = Jsoup.connect(url).get();
			String pageConfig = document.select("script[type=text/javascript]").last().data();
			pageConfig = pageConfig.substring(pageConfig.indexOf("{"), pageConfig.lastIndexOf("}")+1);
			JSONObject json = new JSONObject(pageConfig);
			showid = json.getString("showid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return showid;
	}
}