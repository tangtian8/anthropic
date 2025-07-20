package com.example.adoptions;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author tangtian
 * @date 2025-07-18 11:13
 */
@Entity
public class Dog {
	@Id
	int id;
	String name;
	String owner;
	String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
