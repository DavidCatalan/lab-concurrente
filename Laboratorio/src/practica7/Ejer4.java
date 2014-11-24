package practica7;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// ============================================================================
class Ejer4 {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long            t1, t2;
    double          tt;
    int             numHebras;
    String          nombreFichero;
    MayorPrimo4     p;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <nombreFichero>" );
      System.exit( -1 );
    }
    try {
      numHebras     = Integer.parseInt( args[ 0 ] );
      nombreFichero = args[ 1 ];
    } catch( NumberFormatException ex ) {
      numHebras     = -1;
      nombreFichero = "";
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    System.out.println( "Nombre del fichero que procesar: " + nombreFichero );

    //
    // Implementacion secuencial sin temporizar.
    //
    p = new MayorPrimo4();
    obtenMayorPrimo_Secuencial( nombreFichero, p );

    //
    // Implementacion secuencial.
    //
    System.out.println();
    t1 = System.nanoTime();
    p = new MayorPrimo4();
    obtenMayorPrimo_Secuencial( nombreFichero, p );
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial.                           " );
    System.out.println( " Tiempo(s): " + tt );
    System.out.println( "  Mayor primo en fichero: " + p.dameMayorPrimo() );

    // Implementacion paralela
    System.out.println();
    t1 = System.nanoTime();

    BlockingQueue<Tarea4> colaTarea=new ArrayBlockingQueue<Tarea4>(numHebras);
    p=new MayorPrimo4();
    Productor productor=new Productor(nombreFichero, colaTarea, numHebras);
    productor.start();
    
    Thread[] vHilos = new Thread[numHebras];
    
    for(int i=0;i<numHebras;i++){
    	vHilos[i] = new Consumidor(colaTarea, p);
    	vHilos[i].start();
    }
    
    for(int i=0;i<numHebras;i++){
    	try {
			vHilos[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    
    
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion con BlockingQueue.                           " );
    System.out.println( " Tiempo(s): " + tt );
    System.out.println( "  Mayor primo en fichero: " + p.dameMayorPrimo() );
    //-------------------------
    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  public static void obtenMayorPrimo_Secuencial( 
                         String nombreFichero, MayorPrimo4 p ) {
    BufferedReader br; 
    String         linea;

    // Procesa el fichero.
    try {
      br = new BufferedReader( new FileReader( nombreFichero ) );
      while( ( linea = br.readLine() ) != null ) {
        procesaLinea( linea, p );
      }
      br.close();
    } catch( FileNotFoundException ex ) {
      ex.printStackTrace();
    } catch( IOException ex ) {
      ex.printStackTrace();
    }
  }

 
  
  
  // -------------------------------------------------------------------------
  public static void procesaLinea( String linea, MayorPrimo4 p ) {
    String palabras[], palabraActual;
    long   numero;
  
    // Procesa la linea actual: "linea".
    palabras = linea.split( "\\b" );
    for( int j = 0; j < palabras.length; j++ ) {
      palabraActual = palabras[ j ].trim();
      if( palabraActual.length() > 0 ) {
        // Procesa el dato, si es numero y si es primo.
        try {
          // Quita la L final al numero, si existe y el resto es un numero.
          if( palabraActual.matches( "[0-9]+L" ) ) {
            palabraActual = palabraActual.substring( 0, 
                                palabraActual.length()-1 );
          }
          numero = Long.parseLong( palabraActual );
          if( esPrimo( numero ) ) {
            p.actualizaMayorPrimo( numero );
          }
        } catch( NumberFormatException ex ) {
          // Nada que hacer: La palabra no es numero.
        }
      }
    }
  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean primo;
    if( num < 2 ) {
      primo = false;
    } else {
      primo = true;
      long i = 2;
      while( ( i < num )&&( primo ) ) {
        primo = ( num % i != 0 );
        i++;
      }
    }
    return( primo );
  }
}


// ============================================================================
class MayorPrimo4 {
// ============================================================================
  long mayorPrimo;

  // --------------------------------------------------------------------------
  public MayorPrimo4() {
    mayorPrimo = -1;
  }

  // --------------------------------------------------------------------------
  public synchronized void actualizaMayorPrimo( long numero ) {
    if( numero > mayorPrimo ) {
      mayorPrimo = numero;
    }
  }

  // --------------------------------------------------------------------------
  public synchronized long dameMayorPrimo() {
    return mayorPrimo;
  }
}

//============================================================================
class Tarea4 {
//============================================================================
	String linea;
	boolean esVeneno;
	// -----------------------------------------------------------------------
	public Tarea4(String linea,boolean esVeneno) {
		this.linea = linea;
		this.esVeneno=esVeneno;
	}	
}

class Productor extends Thread{
	String nombreFichero;
	BlockingQueue<Tarea4> colaTarea;
	int numHebras;
	
	public Productor(String nombreFichero,BlockingQueue<Tarea4> colaTarea, int numHebras){
		this.nombreFichero=nombreFichero;
		this.colaTarea=colaTarea;
		this.numHebras = numHebras;
		
	}
	
	public void run(){
		BufferedReader br; 
		  String  linea;

		  try {
			  br = new BufferedReader( new FileReader( nombreFichero ) );
			  while( ( linea = br.readLine() ) != null ) {
				  Tarea4 t = new Tarea4(linea, false);
				  try {
					  colaTarea.put(t);
				  } catch (InterruptedException e) {
					  e.printStackTrace();
				  }

			  }
			  Tarea4 t = new Tarea4("", true);
			  try {
				  for (int i = 0; i < numHebras; i++) {
					  colaTarea.put(t);
				  }
			  } catch (InterruptedException e) {
				  e.printStackTrace();
			  }

			  br.close();
		  } catch( FileNotFoundException ex ) {
			  ex.printStackTrace();
		  } catch( IOException ex ) {
			  ex.printStackTrace();
		  }
	}
}

// ============================================================================================
class Consumidor extends Thread {
// ============================================================================================
	BlockingQueue<Tarea4> colaTareas;
	MayorPrimo4 p;
	
	public Consumidor(BlockingQueue<Tarea4> colaTareas, MayorPrimo4 p) {
		this.colaTareas = colaTareas;
		this.p = p;
	}
	
	public void run() {
		try {
			Tarea4 t = colaTareas.take();
			while ( ! t.esVeneno ) {
				String linea = t.linea;
				procesaLinea(linea, p);
				t = colaTareas.take();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	// -------------------------------------------------------------------------
	private void procesaLinea( String linea, MayorPrimo4 p ) {
		String palabras[], palabraActual;
		long   numero;

		// Procesa la linea actual: "linea".
		palabras = linea.split( "\\b" );
		for( int j = 0; j < palabras.length; j++ ) {
			palabraActual = palabras[ j ].trim();
			if( palabraActual.length() > 0 ) {
				// Procesa el dato, si es numero y si es primo.
				try {
					// Quita la L final al numero, si existe y el resto es un numero.
					if( palabraActual.matches( "[0-9]+L" ) ) {
						palabraActual = palabraActual.substring( 0, 
								palabraActual.length()-1 );
					}
					numero = Long.parseLong( palabraActual );
					if( esPrimo( numero ) ) {
						p.actualizaMayorPrimo( numero );
					}
				} catch( NumberFormatException ex ) {
					// Nada que hacer: La palabra no es numero.
				}
			}
		}
	}

	// -------------------------------------------------------------------------
	private boolean esPrimo( long num ) {
		boolean primo;
		if( num < 2 ) {
			primo = false;
		} else {
			primo = true;
			long i = 2;
			while( ( i < num )&&( primo ) ) {
				primo = ( num % i != 0 );
				i++;
			}
		}
		return( primo );
	}
}
