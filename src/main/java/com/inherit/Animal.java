package com.inherit;

import java.util.Date;

public abstract class Animal {

	public String name;
	public Integer age;
	public Date start;
	public Date end;

	public abstract void speack();

	public Animal(String name, Integer age, Date start, Date end) {
		super();
		this.name = name;
		this.age = age;
		this.start = start;
		this.end = end;
	}

	public void modify(String name, Integer age, long start, long end) {
		this.name += name;
		this.age += age;
		this.start = new Date(this.start.getTime() + start);
		this.end = new Date(this.end.getTime() + end);
	}
}