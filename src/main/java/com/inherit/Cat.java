package com.inherit;

import java.util.Date;

public class Cat extends Animal {

	public Cat(String name, Integer age, Date start, Date end) {
		super(name, age, start, end);
	}

	@Override
	public void speack() {
		System.out.println("------------");
	}
}