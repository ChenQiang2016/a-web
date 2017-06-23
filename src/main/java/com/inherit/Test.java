package com.inherit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		
		System.out.println("1111111111111");
		Dog dog = new Dog("dog", 5, getDate("20170323"), getDate("20170623"));
		Cat cat = new Cat("cat", 7, getDate("20180323"), getDate("20180623"));
		Bird bird = new Bird("bird", 3, getDate("20190323"), getDate("20190623"));
		
		dog.modify("111", 10, 1000, 1000);
		cat.modify("222", 10, 1000, 1000);
		bird.modify("333", 10, 1000, 1000);
		System.out.println("2222222222222");
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	public static Date getDate(String d) {
		Date date = null;
		try {
			date = sdf.parse(d);
		} catch (ParseException e) {
			System.out.println("时间格式错误");
		}
		return date;
	}
}