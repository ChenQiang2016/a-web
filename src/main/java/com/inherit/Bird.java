package com.inherit;

import java.util.Date;

public class Bird extends Animal {

	public Bird(String name, Integer age, Date start, Date end) {
		super(name, age, start, end);
	}

	@Override
	public void speack() {
		System.out.println("------------");
	}
}