package mrt;

public class Edge{

	private String station1, station2;
	private int time;
	

	public Edge( String station1, String station2, int time ){
		this.station1 = station1;
		this.station2 = station2;
		this.time = time;
	}


	public String getS1(){
		return station1;
	}


	public String getS2(){
		return station2;
	}


	public int getT(){
		return time;
	}



	public boolean containsStation( String station ){
		return ( station1.equals(station) || station2.equals(station) ) ;
	}


	public boolean matches( String s1, String s2 ){
		return ( containsStation(s1) && containsStation(s2) );
	}


	public boolean matches( Edge e ){
		return matches( e.getS1(), e.getS2() );
	}


	public Edge clone(){
		return new Edge( station1, station2, time );
	}


	public String toString(){
		return "The time to travel from " + station1 + " to " + station2 + " is " + time + " minutes. ";
	}
}