package org.mishu;

public class Animal {
	private String name;
	public Animal(String name) {
		this.name = name;
	}

	public void speak() {
		System.out.println("I am a " + name);
	}
}

