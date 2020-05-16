package it.polito.tdp.crimes.model;

public class Adiacenza {
	private String reato1;
	private String reato2;
	private Double peso;
	/**
	 * @param reato1
	 * @param reato2
	 * @param peso
	 */
	public Adiacenza(String reato1, String reato2, Double peso) {
		super();
		this.reato1 = reato1;
		this.reato2 = reato2;
		this.peso = peso;
	}
	public String getReato1() {
		return reato1;
	}
	public void setReato1(String reato1) {
		this.reato1 = reato1;
	}
	public String getReato2() {
		return reato2;
	}
	public void setReato2(String reato2) {
		this.reato2 = reato2;
	}
	public Double getPeso() {
		return peso;
	}
	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	
	
	

}
