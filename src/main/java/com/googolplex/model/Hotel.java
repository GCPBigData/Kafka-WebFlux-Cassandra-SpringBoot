package com.googolplex.model;

import java.util.Set;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
@Data
@Table(keyspace = "hotel", name = "hotel")
public class Hotel {

	@PartitionKey
	private String uuid;
	@Column(name = "name")
	private String name;
	@Column(name = "phone")
	private String phone;
	@Column(name = "pois")
	private Set<String> pois;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Set<String> getPois() {
		return pois;
	}

	public void setPois(Set<String> pois) {
		this.pois = pois;
	}
}
