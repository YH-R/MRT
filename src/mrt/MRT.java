package mrt;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class MRT extends JFrame{

	private static ArrayList<Edge> edgeSet;
	private static ArrayList<Vertex> vertexSet;

	private static ArrayList<Edge> cloneEdgeSet;
	private static ArrayList<Vertex> cloneVertexSet;

	private static String[] allStationNames;

	private static String sV = "";
	private static String eV = "";


	//swing components

	private JComboBox box1;
	private JComboBox box2;
	private JLabel l1, l2;
	private JButton button;

	
	public MRT(){
		super( "Route Finder" );
		setSize( 220, 155 );
		setResizable( false );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		setLayout( new FlowLayout( FlowLayout.CENTER, 20, 10 ) );


		Scanner sc = null;
		Scanner st = null;
		edgeSet = new ArrayList<Edge>();
		vertexSet = new ArrayList<Vertex>();
		cloneEdgeSet = new ArrayList<Edge>();
		cloneVertexSet = new ArrayList<Vertex>();


		try{
			String currentLine;
			InputStream is = getClass().getResourceAsStream("Weights.csv");
			sc = new Scanner( is );
			
			String token1;
			String token2;
			int token3;

			while ( sc.hasNextLine() ) {
				currentLine = sc.nextLine();

				st = new Scanner(currentLine).useDelimiter("\\s,");

				token1 = st.next();
				token2 = st.next();
				token3 = st.nextInt();

				Edge e = new Edge( token1, token2, token3 );
				boolean b = true;
				
				for( Edge edge: edgeSet ){
					if( edge.matches( e ) )
						b = false;
				}

				if( b )
					edgeSet.add( e );
			}					//finished creating edge set

			sc.close();
		} 
		catch( Exception e ) {
			e.printStackTrace();
		}


		boolean s1exists = false;
		boolean s2exists = false;

		for( Edge e : edgeSet ) {
			//System.out.println( e.toString() );

			for( Vertex v: vertexSet ){
				if( e.getS1().matches( v.getS() ) ){
					s1exists = true;
					v.addEdge( e );
				}
			}

			if ( !s1exists ){
				vertexSet.add( new Vertex( e.getS1(), e ) );
			}

			s1exists = false;


			for( Vertex v: vertexSet ){
				if( e.getS2().matches( v.getS() ) ){
					s2exists = true;
					v.addEdge( e );
				}
			}

			if ( !s2exists ){
				vertexSet.add( new Vertex( e.getS2(), e ) );
			}

			s2exists = false;	
		}

		int order = vertexSet.size();
		allStationNames = new String[order];
		for( int i = 0; i < order; i++ ){
			allStationNames[i] = vertexSet.get(i).getS();
		}

		//Ask user input

		l1 = new JLabel( "Start: " );
		add(l1);

		box1 = new JComboBox( allStationNames );
		add( box1 );

		l2 = new JLabel( "End: " );
		add(l2);

		box2 = new JComboBox( allStationNames );
		add( box2 );

		button = new JButton( "Find" );
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				calculate( (String)box1.getSelectedItem(), (String)box2.getSelectedItem() );
			}
		});      
		add( button );

		setVisible( true );
	}




	public static void main(String[] args){
		new MRT();
	}




	public static void removeV( String s ){
		for( int i = cloneEdgeSet.size()-1; i >= 0; i-- ){
			if( cloneEdgeSet.get(i).containsStation( s ) ){
				cloneEdgeSet.remove(i);
//				System.out.println( "Removed " + s );
			}
		}

//		System.out.println( "cloneEdgeSet.size() = " + cloneEdgeSet.size() );

		for( int i = cloneVertexSet.size()-1; i >= 0; i-- ){
			if( cloneVertexSet.get(i).getS().equals(s) ){
				ArrayList<String> neighborList = cloneVertexSet.get(i).getNeighborList();

/*				for( String s: neighborList ){
					System.out.println(s);
				}
*/
				for( int j = neighborList.size()-1; j >= 0; j-- ){
					for( int k = cloneVertexSet.size()-1; k >= 0; k-- ){
						if( cloneVertexSet.get(k).getS().equals( neighborList.get(j) ) )
							cloneVertexSet.get(k).deleteEdge(s);
					}
				}

				cloneVertexSet.remove(i);
			}
		}

//		System.out.println( "cloneVertexSet.size() = " + cloneVertexSet.size() );
	}


	public static void removeE( Edge e ){
		for( int i = cloneEdgeSet.size()-1; i >= 0; i-- ){
			if( cloneEdgeSet.get(i).matches(e) ){
				cloneEdgeSet.remove(i);
			}
		}

		for( int i = cloneVertexSet.size()-1; i >= 0; i-- ){
			Vertex v = cloneVertexSet.get(i);

			if( v.getS().equals( e.getS1() ) ){
				v.deleteEdge(e.getS2());
			}
			else if( v.getS().equals( e.getS2() ) ){
				v.deleteEdge(e.getS1());
			}
		}
	}




	public static void simplify1(){
		boolean b = true;

		while( b ){
			b = false;

			for( int i = cloneVertexSet.size()-1; i >= 0; i-- ){
				if( !cloneVertexSet.get(i).getS().equals(sV) && !cloneVertexSet.get(i).getS().equals(eV) && cloneVertexSet.get(i).degree() == 1 ){
					removeV( cloneVertexSet.get(i).getS() );
					b = true;
				}
//				System.out.println( "Checked " + cloneVertexSet.get(i).getS() );
			}

		}
	}


	public static void remove2a( Vertex v, Edge e ){
		int min = Math.min( v.getIE().get(0).getT()+v.getIE().get(1).getT(), e.getT() );

		removeE( e );

		remove2b( v, false, min );
	}

	public static void remove2b( Vertex v, boolean b, int x ){
		Edge e;

		if(b)

			e = new Edge( v.getNeighbor(v.getIE().get(0)), v.getNeighbor(v.getIE().get(1)), v.getIE().get(0).getT()+v.getIE().get(1).getT() );
		else
			e = new Edge( v.getNeighbor(v.getIE().get(0)), v.getNeighbor(v.getIE().get(1)), x );


		for( Vertex temp: cloneVertexSet ){
			if( temp.getS().equals( e.getS1() ) || temp.getS().equals( e.getS2() ) ){
				temp.addEdge( e );
			}
		}

		cloneEdgeSet.add(e);

		removeV( v.getS() );
	}


	public static void simplify2(){
		Vertex v;

		for( int i = cloneVertexSet.size()-1; i >= 0; i-- ){
			v = cloneVertexSet.get(i);
			if( !v.getS().equals(sV) && !v.getS().equals(eV) && v.degree() == 2 ){
				boolean adjacent = false;
				Edge temp = null;

				for( Edge e: cloneEdgeSet ){
					if( e.matches( v.getNeighbor(v.getIE().get(0)), v.getNeighbor(v.getIE().get(1)) ) ){
						adjacent = true;
						temp = e;
					}
				}

				if( adjacent ){
					remove2a( v, temp );
				}
				else{
					remove2b( v, true, 0 );
				}	
			
			
			}
		}
	}


	public static void cloneList(){
		for( Edge e: edgeSet ){
			cloneEdgeSet.add( e.clone() );
		}

		for( Vertex v: vertexSet ){
			cloneVertexSet.add( v.clone() );
		}
	}


	public static void deleteClone(){
		cloneEdgeSet.clear();
		cloneVertexSet.clear();
	}


	public static int algo1(){	//gets index of vertex in cloneVertexSet that is unidentified and has smallest Dist
		int n = 0;

		int min = Vertex.BIGNUMBER;

		for( int i = 0; i < cloneVertexSet.size(); i++ ){
			if( cloneVertexSet.get(i).getDist() <= min && !cloneVertexSet.get(i).getID() ){
				min = cloneVertexSet.get(i).getDist();
				n = i;
			}
		}

		cloneVertexSet.get(n).setID(true);

		return n;
	}


	public static void algo2( int n ){
		Vertex v = cloneVertexSet.get(n);

		for( int i = 0; i < v.degree(); i++ ){
			for( int j = cloneVertexSet.size()-1; j >= 0; j-- ){
				if( cloneVertexSet.get(j).getS().equals( v.getNeighborList().get(i) ) ){

					//System.out.println( "Comparing " + v.getS() + " and " + cloneVertexSet.get(j).getS() );

					if(!cloneVertexSet.get(j).getID()){
						if( cloneVertexSet.get(j).getDist() > v.getDist() + v.getEdge(cloneVertexSet.get(j).getS()).getT() ){
							cloneVertexSet.get(j).setDist( v.getDist() + v.getEdge(cloneVertexSet.get(j).getS()).getT() );
							cloneVertexSet.get(j).setPath( v.getPath() );
							cloneVertexSet.get(j).addPath( cloneVertexSet.get(j).getS() );

							//System.out.println( "Distance of " + cloneVertexSet.get(j).getS() + " set to " + cloneVertexSet.get(j).getDist() + "." );
						}
					}
				}
			}
		}
	}

	public static void calculate( String start, String end ){
		sV = start;
		eV = end;

		cloneList();


		simplify1();
		simplify2();

		
		for( Vertex v : cloneVertexSet ) {
			System.out.println( v.getS() + " has " + v.degree() + " neighbors." );
		}

		for( Edge e: cloneEdgeSet ){
			System.out.println( e.toString() );
		}

		

		//algorithm

		for( int i = cloneVertexSet.size()-1; i >= 0; i-- ){
			if( cloneVertexSet.get(i).getS().equals(sV) ){
				cloneVertexSet.get(i).setDist(0);
				cloneVertexSet.get(i).addPath( cloneVertexSet.get(i).getS() );
			}
		}


		boolean incomplete = true;

		while( incomplete ){
			algo2( algo1() );

			incomplete = false;

			for( Vertex v: cloneVertexSet ){
				if(!v.getID()){
					incomplete = true;
				}
			}
		}

		for( Vertex v: cloneVertexSet ){
			if( v.getS().equals(eV) ){
				output(v);
			}
		}

		deleteClone();
	}


	public static void output( Vertex v ){
		JOptionPane.showMessageDialog(null,v.pathString(),sV+" to "+eV,JOptionPane.INFORMATION_MESSAGE);
	}
}