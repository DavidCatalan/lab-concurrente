package practica7;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ============================================================================
class Ejer1 {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long            t1, t2;
    double          tt;
    int             numHebras;
    String          nombreFichero;
    MayorPrimo      p;
    ExecutorService exec;
    
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
    
    exec = Executors.newFixedThreadPool(numHebras);
    System.out.println( "Nombre del fichero que procesar: " + nombreFichero );

    //
    // Implementacion secuencial sin temporizar.
    //
    p = new MayorPrimo();
    obtenMayorPrimo_Secuencial( nombreFichero, p );

    //
    // Implementacion secuencial.
    //
    System.out.println();
    t1 = System.nanoTime();
    p = new MayorPrimo();
    obtenMayorPrimo_Secuencial( nombreFichero, p );
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial.                           " );
    System.out.println( " Tiempo(s): " + tt );
    System.out.println( "  Mayor primo en fichero: " + p.dameMayorPrimo() );

    //
    // Implementación con ThreadPool
    //
    
    System.out.println();
    t1 = System.nanoTime();
    p = new MayorPrimo();
    obtenMayorPrimo_ThreadPool(nombreFichero, p, exec);
    
    
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion con ThreadPool.                           " );
    System.out.println( " Tiempo(s): " + tt );
    System.out.println( "  Mayor primo en fichero: " + p.dameMayorPrimo() );

    System.out.println();
    System.out.println( "Fin de programa." );
    
  }
  // -------------------------------------------------------------------------
  public static void obtenMayorPrimo_ThreadPool(String nombreFichero, MayorPrimo p, 
		  ExecutorService exec) {
	  BufferedReader br;
	  String linea;
	  
	  try {
		  br = new BufferedReader( new FileReader( nombreFichero ) );
		  while( ( linea = br.readLine() ) != null ) {
			  exec.execute(new Tarea(linea, p));
		  }
		  br.close();
	  } catch ( FileNotFoundException ex ) {
		  ex.printStackTrace();
	  } catch ( IOException ex ) {
		  ex.printStackTrace();
	  }
	  
	  exec.shutdown();
	  
	  while( ! exec.isTerminated() ) {
		  
	  }
  }
  // -------------------------------------------------------------------------
  public static void obtenMayorPrimo_Secuencial( 
                         String nombreFichero, MayorPrimo p ) {
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
  public static void procesaLinea( String linea, MayorPrimo p ) {
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
class MayorPrimo {
// ============================================================================
  long mayorPrimo;

  // --------------------------------------------------------------------------
  public MayorPrimo() {
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
class Tarea implements Runnable {
//============================================================================
	String linea;
	MayorPrimo p;
	
	// -----------------------------------------------------------------------
	public Tarea(String linea, MayorPrimo p) {
		this.linea = linea;
		this.p = p;
	}
	// ------------------------------------------------------------------------
	@Override
	public void run() {
		// TODO Auto-generated method stub
		procesaLinea(linea, p);
	}
	
	// -------------------------------------------------------------------------
	private void procesaLinea( String linea, MayorPrimo p ) {
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
