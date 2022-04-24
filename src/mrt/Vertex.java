package mrt;

import java.util.*;

public class Vertex{

	public static final int BIGNUMBER = 10000;

	private String station;
	private ArrayList<Edge> incidentEdges;
	private boolean identified;
	private int dist;
	private ArrayList<String> path;
	

	public Vertex( String station ){
		this.station = station;
		incidentEdges = new ArrayList<Edge>();
		identified = false;
		dist = BIGNUMBER;
		path = new ArrayList<String>();
	}

	public Vertex( String station, Edge e ){
		this.station = station;
		incidentEdges = new ArrayList<Edge>();
		incidentEdges.add(e);
		identified = false;
		dist = BIGNUMBER;
		path = new ArrayList<String>();
	}

	public Vertex( String station, ArrayList<Edge> eList ){
		this.station = station;
		incidentEdges = eList;
		identified = false;
		dist = BIGNUMBER;
		path = new ArrayList<String>();
	}


	public String getS(){
		return station;
	}

	public ArrayList<Edge> getIE(){
		return incidentEdges;
	}

	public String getNeighbor( Edge e ){
		String s;

		if( e.getS1().matches(station) ){
			s = e.getS2();
		}
		else if( e.getS2().matches(station) ){
			s = e.getS1();
		}
		else{
			s = ""; 
		}

		return s;
	}

	public ArrayList<String> getNeighborList(){
		ArrayList<String> neighborList = new ArrayList<String>();
		
		for( Edge e: incidentEdges ){
			neighborList.add( getNeighbor(e) );
		}

		return neighborList;
	}

	public Edge getEdge( String s ){
		int n = 0;
		ArrayList<String> list = getNeighborList();

		for( int i = 0; i < degree(); i++ ){
			if( list.get(i).equals(s) ){
				n = i;
			}
		}

		return incidentEdges.get(n);
	}

	public boolean getID(){
		return identified;
	}

	public int getDist(){
		return dist;
	}

	public ArrayList<String> getPath(){
		return clonePath();
	}

	public int degree(){
		return incidentEdges.size();
	}


	public void setID( boolean id ){
		identified = id;
	}

	public void setDist( int dist ){
		this.dist = dist;
	}


	public void addEdge( Edge e ){
		incidentEdges.add(e);
	}

	public void deleteEdge( String s ){
		for( int i = incidentEdges.size()-1; i >= 0; i-- ){
			if( getNeighbor( incidentEdges.get(i) ).equals(s) )
				incidentEdges.remove(i);
		}
	}

	public void setPath( ArrayList<String> list ){
		path = list;
	}

	public void addPath( String s ){
		path.add(s);
	}


	public boolean matches( String station ){
		return station.equals( this.station );
	}


	public boolean isAdjacent( Vertex v ){
		boolean b = false;

		for( Edge e: incidentEdges ){
			if( v.getS().equals(getNeighbor(e)) ){
				b = true;
			}
		}

		return b;
	}


	public Vertex clone(){
		ArrayList<Edge> cloneList = new ArrayList<Edge>();
		for( Edge e: getIE() ){
			cloneList.add( e.clone() );
		}

		return new Vertex( station, cloneList );
	}

	public ArrayList<String> clonePath(){
		ArrayList<String> cloneList = new ArrayList<String>();
		for( String s: path ){
			cloneList.add( new String(s) );
		}

		return cloneList;
	}

	public String pathString(){
		String s = "";
		s += "Shortest route:\n\n";

		for( String l: path ){
			s += l + "\n";
		} 
		s += "\nTime = " + getDist() + " min.";

		return s;
	}
}