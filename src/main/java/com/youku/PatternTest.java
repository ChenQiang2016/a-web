package com.youku;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class PatternTest {

	@Test
	public void test(){
		String html = "更新至48集&nbsp;|&nbsp;共90集";
		try {
			Pattern pattern = Pattern.compile("更新至(\\d*)集");
			Matcher matcher = pattern.matcher(html);
			while(matcher.find()){
				System.out.println(matcher.group(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getNumber(){
		String ss = "青云志2第20集";
		try {
			Pattern pattern = Pattern.compile("(\\d*)");
			Matcher matcher = pattern.matcher(ss);
			while(matcher.find()){
				System.out.println(matcher.group(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
