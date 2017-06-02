package com.tencent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class TencentLogin {
	
	
	public static void main(String[] args) {
		login();
	}
	
	private static void login(){
		try {
			String url = "http://c.v.qq.com/vfollowlst?otype=json&pagenum=1&pagesize=5&callback=jQuery191037271386748486024_1494311174024&g_tk=973875345&_=1494311174025";
			String cookie = "tvfe_boss_uuid=a8ae33afa860c3fe; pgv_info=ssid=s9826036940; ts_last=v.qq.com/; pgv_pvid=7081721960; ts_uid=4665592864; ptag=|u; pgv_pvi=1445660672; pgv_si=s6689992704; _qpsvr_localtk=0.006587437762069381; ptui_loginuin=3365723338%20%20%20%20%20; pt2gguin=o3365723338; uin=o3365723338; skey=@4J5B288Hm; ptisp=ctc; RK=EvneHZ8qy5; luin=o3365723338; lskey=000100003f1a3b7e470546388a3798600ada0f20bbf1abc7d0ff57b2a94ca58aa845c84003636482c5976c74; ptcz=c633cda8db822a6e7aa5115e554babffc51b99552253a258f0573eb91b154998; main_login=qq; encuin=9ba0005323753f77923e3b7fce6a6c54|3365723338; lw_nick=i%E5%BF%AB%E6%89%8B|3365723338|//q3.qlogo.cn/g?b=qq&k=jSyWHKDRb2RMZJe3PQcEkA&s=40&t=585|1; login_remember=qq";
			
			HttpClient client = HttpClients.createDefault();
			
			HttpGet httpGet = new HttpGet(url);
			
			httpGet.addHeader(new BasicHeader("Cookie", cookie));
			
			HttpResponse response = client.execute(httpGet);
			
			HttpEntity entity = response.getEntity();
			
			String result = EntityUtils.toString(entity);
			
			result = result.substring(result.indexOf("{"), result.length() - 1);
			
			System.out.println(result);
			
//			JSONObject json = new JSONObject(result);
//			
//			JSONArray jsonArray = json.getJSONArray("videolst");
//			System.out.println(jsonArray.getJSONObject(0).getString("uploadtime"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
