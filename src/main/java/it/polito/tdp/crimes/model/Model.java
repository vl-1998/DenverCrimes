package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private SimpleWeightedGraph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<Integer> getMesi(){
		return dao.getMesi();
	}
	
	public List<String> getCategorie(){
		return dao.getCategorie();
	}
	
	
	public void creaGrafo(String categoria, Integer mese) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		List<Adiacenza> adiacenze = this.dao.getAdiacenze(categoria, mese);
		for(Adiacenza a : adiacenze) {
			if(!this.grafo.containsVertex(a.getV1()))
				this.grafo.addVertex(a.getV1());
			if(!this.grafo.containsVertex(a.getV2()))
				this.grafo.addVertex(a.getV2());
			
			if(this.grafo.getEdge(a.getV1(), a.getV2()) == null) 
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		
		System.out.println(String.format("Grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
	}
	
	
	public List<Arco> getArchi(){
		double pesoMedio = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		pesoMedio = pesoMedio/this.grafo.edgeSet().size();
		
		List<Arco> archi = new ArrayList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > pesoMedio)
				archi.add(new Arco(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),this.grafo.getEdgeWeight(e)));
		}
		Collections.sort(archi);
		return archi;
	}
	
	
	public List<String> trovaPercorso(String sorgente, String destinazione) {
		List<String> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		parziale.add(sorgente);
		trovaRiscorsivo(destinazione,parziale, 0);
		return this.best;
	}

	private void trovaRiscorsivo(String destinazione, List<String> parziale, int L) {

		//CASO TERMINALE? -> quando l'ultimo vertice inserito in parziale è uguale alla destinazione
		if(parziale.get(parziale.size() - 1).equals(destinazione)) {
			if(parziale.size() > this.best.size()) {
				this.best = new ArrayList<>(parziale);
			}
			return;
		}
		
		//scorro i vicini dell'ultimo vertice inserito in parziale
		for(String vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size() -1 ))) {
			//cammino aciclico -> controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				//provo ad aggiungere
				parziale.add(vicino);
				//continuo la ricorsione
				this.trovaRiscorsivo(destinazione, parziale, L+1);
				//faccio backtracking
				parziale.remove(parziale.size() -1);
			}
		}
	}
}
