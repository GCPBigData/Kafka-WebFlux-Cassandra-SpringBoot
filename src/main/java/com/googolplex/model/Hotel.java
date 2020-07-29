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
	@Column(name = "nome")
	private String nome;
	@Column(name = "telefone")
	private String telefone;
	@Column(name = "pois")
	private Set<String> coidgo;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Set<String> getCoidgo() {
		return coidgo;
	}

	public void setCoidgo(Set<String> coidgo) {
		this.coidgo = coidgo;
	}
}
