package practica4;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula() {
    suma = 0.0;
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones1b extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones1b( int miId, int numHebras, long numRectangulos, 
                              Acumula a ) {
	  this.miId = miId;
	  this.numHebras = numHebras;
	  this.numRectangulos = numRectangulos;
	  this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
	  double baseRectangulo = 1.0 / ( ( double ) numRectangulos );

	  for(int i = miId; i < numRectangulos; i += numHebras) {
		  double x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
	      a.acumulaDato(4.0/( 1.0 + x*x));
	  }
  }
}


// ===========================================================================
class EjemploNumeroPI1a {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    MiHebraMultAcumulaciones1b  vt[];
    Acumula                     a;
    long                        t1, t2;
    double                      tSec, tPar;

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Comienzo del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version Secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo transcurrido (s.):      " + tSec );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    //
    
    System.out.println();
    System.out.println("Comienzo del cálculo paralelo con distribución cíclica.");
    t1 = System.nanoTime();
    a = new Acumula();
    vt = new MiHebraMultAcumulaciones1b[numHebras];
    for(int i = 0; i < numHebras; i++) {
    	vt[i] = new MiHebraMultAcumulaciones1b(i, numHebras, numRectangulos, a);
    	vt[i].start();
    }
    
    for(int i = 0; i < numHebras; i++) {
    	try {
			vt[i].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    pi = baseRectangulo * a.dameDato();
    
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    
    System.out.println( "Version Paralela. Numero PI: " + pi );
    System.out.println( "Tiempo transcurrido (s.):      " + tPar );
    
    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

