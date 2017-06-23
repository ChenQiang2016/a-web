package com.inherit;

import java.util.Date;

public class Dog extends Animal {

	public Dog(String name, Integer age, Date start, Date end) {
		super(name, age, start, end);
	}

	@Override
	public void speack() {
		System.out.println("------------");
	}
}