package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	//creo la variabile grafo
	private Graph <String, DefaultWeightedEdge> grafo;
	//IN QUESTO CASO NON USO LA MAPPA
	private EventsDao dao ;
	
	//PER LA RICORSIONE
	private List <String> best;
	
	public Model() {
		//prendo un'istanza del DAO
		dao = new EventsDao();
	}
	
	public void creGrafo(String tipoReato, Integer mese) {
		//inizializzo il grafo 
		this.grafo= new SimpleWeightedGraph <>(DefaultWeightedEdge.class);
		//dao.listAllEvents(idMap); //conterrà tutti gli oggetti presenti nella tabella 
		List <Adiacenza> adiacenze = this.dao.getAdiacenze(tipoReato, mese);
		
		for (Adiacenza a : adiacenze) {
			//inserisco le informazioni nel grafo
			if (!this.grafo.containsVertex(a.getReato1())) {
				this.grafo.addVertex(a.getReato1());
			}
			if(!this.grafo.containsVertex(a.getReato2())) {
				this.grafo.addVertex(a.getReato2());
			}
			
			//creo l'arco tra il 1 e il 2 reato solo se questo non c'è ancora
			if (this.grafo.getEdge(a.getReato1(), a.getReato2())==null) {
				Graphs.addEdgeWithVertices(this.grafo, a.getReato1(), a.getReato2(), a.getPeso());
			}
		}
		System.out.println(String.format("Grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
	}
	
	public String stampa (){
		String result="";
		double pesoMedio=0.0;
		double somma=0.0;
		int i =0;
		
		for (DefaultWeightedEdge e : grafo.edgeSet()) {
			somma += this.grafo.getEdgeWeight(e);
			i++;
		}
		
		pesoMedio = somma/i;
		
		for (DefaultWeightedEdge e : grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(e)>pesoMedio) {
				if (result =="") {
					result = "(" + grafo.getEdgeSource(e) + "," + grafo.getEdgeTarget(e) + ")"+ " peso: "+ grafo.getEdgeWeight(e);
				}
				else {
					result = result+ "\n"+"(" + grafo.getEdgeSource(e) + "," + grafo.getEdgeTarget(e) + ")"+ " peso: "+ grafo.getEdgeWeight(e);
				}	
			}
		}
		
		return result;
	}
	
	
	//Metodo con la creazione di una classe Arco
	public List<Arco> getArchi(){
		double pesoMedio=0.0;
		
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		
		pesoMedio=pesoMedio/this.grafo.edgeSet().size();
		
		List <Arco> archi = new ArrayList <>();
		
		for (DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(e)>pesoMedio) {
				archi.add(new Arco (this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
			}
			
		}
		Collections.sort(archi);
		return archi;
		
	}
	
	public List<String> elencoReati(){
		return dao.categoriaReato();
	}
	
	public List<Integer> elencoMesi(){
		return dao.mesi();
	}

	//procedura ricorsiva
	public List<String> trovaPercorso(String sorgente, String destinazione) {
		List <String> parziale = new ArrayList <>();
		this.best = new ArrayList <>();
		parziale.add(sorgente);
		
		trovaRicorsivo (destinazione, parziale, 0);
		return this.best;
		
	}

	private void trovaRicorsivo(String destinazione, List<String> parziale, int i) {
		//caso terminale
		//l'ultimo vertice inserito in parziale è uguale alla destinazione, eventualmente controllero se il 
		//percorso trovato è migliore a quello precendent
		if (parziale.get(parziale.size()-1).equals(destinazione)){
			//controllo la bontà del percorso trovato
			if (parziale.size()> this.best.size()) {
				this.best= new ArrayList<>(parziale);
			}
			return;
		}
		
		//scorro i vicini dell'ultimo vertice inserito in parziale e per ogni vicino provo a metterlo nel percorso,
		//lancio la procedura ricorsiva e faccio backtracking
		for (String vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			//cammino aciclico -> non devo visitare lo stesso nodo più volte, controllo che il vertice non sia gia in parziale
			if (!parziale.contains(vicino)) {
				//provo ad aggiungere
				parziale.add(vicino);
				//continuo la ricorsione
				this.trovaRicorsivo(destinazione, parziale, i+1);
				//backtracking
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
	
}
