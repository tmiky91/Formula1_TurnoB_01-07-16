package it.polito.tdp.formulaone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private DefaultDirectedWeightedGraph<Constructor, DefaultWeightedEdge> grafo;
	private Map<Integer, Constructor> idMap; 
	
	public Model() {
		idMap = new HashMap<Integer, Constructor>();
	}

	public static List<Circuit> getAllCircuits() {
		FormulaOneDAO dao = new FormulaOneDAO();
		return dao.getAllCircuits();
	}

	public String getMigliorCostruttore(Circuit c) {
		FormulaOneDAO dao = new FormulaOneDAO();
		double max=0;
		Constructor best=null;
		String risultato="";
		grafo = new DefaultDirectedWeightedGraph<Constructor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.getAllConstructors(idMap);
		List<DatiGrafo> datiGrafo = dao.getDatiGrafo(idMap, c);
		for(DatiGrafo dg: datiGrafo) {
			if(!grafo.containsVertex(dg.getC1())) {
				grafo.addVertex(dg.getC1());
			}
			if(!grafo.containsVertex(dg.getC2())) {
				grafo.addVertex(dg.getC2());
			}
			DefaultWeightedEdge edge = grafo.getEdge(dg.getC1(), dg.getC2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, dg.getC1(), dg.getC2(), dg.getPeso());
			}else {
				grafo.setEdgeWeight(edge, dg.getPeso());
			}
		}
		System.out.println("Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size());
		
		for(Constructor c1: grafo.vertexSet()) {
			double sum=0;
			for(DefaultWeightedEdge edge: grafo.outgoingEdgesOf(c1)) {
				sum+=grafo.getEdgeWeight(edge);
			}
			for(DefaultWeightedEdge edge: grafo.incomingEdgesOf(c1)) {
				sum-=grafo.getEdgeWeight(edge);
			}
			if(sum>max) {
				max=sum;
				best=c1;
				risultato="Il Costruttore che ha conseguito il miglior risultato per il circuito selezionato è: "+c1.getName()+" con un risultato di: "+max;
			}
		}
		return risultato;
	}


}
