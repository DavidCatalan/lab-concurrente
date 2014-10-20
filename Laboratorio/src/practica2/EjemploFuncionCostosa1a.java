package practica2;

// ============================================================================
class EjemploFuncionCostosa1a {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     n, numHebras;
    long    t1, t2;
    double  tt, sumaX, sumaY;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <tamanyo>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    // Crea los vectores.
    double vectorX[] = new double[ n ];
    double vectorY[] = new double[ n ];

    //
    // Implementacion secuencial.
    //
    inicializaVectorX( vectorX );
    inicializaVectorY( vectorY );
    t1 = System.nanoTime();
    for( int i = 0; i < n; i++ ) {
      vectorY[ i ] = evaluaFuncion( vectorX[ i ] );
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + tt );
    //// imprimeResultado( vectorX, vectorY );
    // Comprueba el resultado. 
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );


    System.out.println( "Fin del programa secuencial" );
    
    //Parte Concurrente ciclica--------------------------------
    System.out.println("Inicio del programa concurrente con distribución cíclica.");
    
    Thread[] vHebras = new Thread[numHebras];
    
    inicializaVectorX(vectorX);
    inicializaVectorY(vectorY);
    t1 = System.nanoTime();
    
    for(int i = 0; i < numHebras; i++) {
    	vHebras[i] = new HebraCiclica(i, numHebras, vectorX, vectorY);
    	vHebras[i].start();
    }
    
    for(int i = 0; i < numHebras; i++) {
    	try {
			vHebras[i].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo concurrente cíclico (seg.):                    " + tt );
    
    sumaX = sumaVector( vectorX );
    sumaY = sumaVector( vectorY );
    System.out.println( "Suma del vector X:          " + sumaX );
    System.out.println( "Suma del vector Y:          " + sumaY );
    
    System.out.println("Fin programa concurrente cíclico.");
  
//Fin parte concurrente ciclica---------
  
 //Parte concurrente por bloques---------
  System.out.println("Inicio del programa concurrente con distribución por bloques.");
  
  inicializaVectorX(vectorX);
  inicializaVectorY(vectorY);
  t1 = System.nanoTime();
  
  for(int i = 0; i < numHebras; i++) {
  	vHebras[i] = new HebraBloques(i, numHebras, vectorX, vectorY);
  	vHebras[i].start();
  }
  
  for(int i = 0; i < numHebras; i++) {
  	try {
			vHebras[i].join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  t2 = System.nanoTime();
  tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
  System.out.println( "Tiempo concurrente por bloques (seg.):                    " + tt );
  
  sumaX = sumaVector( vectorX );
  sumaY = sumaVector( vectorY );
  System.out.println( "Suma del vector X:          " + sumaX );
  System.out.println( "Suma del vector Y:          " + sumaY );
  
  System.out.println("Fin programa concurrente por bloques.");
  
  }
  
  // --------------------------------------------------------------------------
  static void inicializaVectorX( double vectorX[] ) {
    if( vectorX.length == 1 ) {
      vectorX[ 0 ] = 0.0;
    } else {
      for( int i = 0; i < vectorX.length; i++ ) {
        vectorX[ i ] = 10.0 * ( double ) i / ( ( double ) vectorX.length - 1 );
      }
    }
  }

  // --------------------------------------------------------------------------
  static void inicializaVectorY( double vectorY[] ) {
    for( int i = 0; i < vectorY.length; i++ ) {
      vectorY[ i ] = 0.0;
    }
  }

  // --------------------------------------------------------------------------
  static double sumaVector( double vector[] ) {
    double  suma = 0.0;
    for( int i = 0; i < vector.length; i++ ) {
      suma += vector[ i ];
    }
    return suma;
  }

  // --------------------------------------------------------------------------
  static double evaluaFuncion( double x ) {
    return Math.sin( Math.exp( -x ) + Math.log( 1 + x ) );
  }

  // --------------------------------------------------------------------------
  static void imprimeVector( double vector[] ) {
    for( int i = 0; i < vector.length; i++ ) {
      System.out.println( " vector[ " + i + " ] = " + vector[ i ] );
    }
  }

  // --------------------------------------------------------------------------
  static void imprimeResultado( double vectorX[], double vectorY[] ) {
    for( int i = 0; i < Math.min( vectorX.length, vectorY.length ); i++ ) {
      System.out.println( "  i: " + i + 
                          "  x: " + vectorX[ i ] +
                          "  y: " + vectorY[ i ] );
    }
  }
}

class HebraCiclica extends Thread {
	double[] vectorX, vectorY;
	int miId, numHebras;
	
	public HebraCiclica(int miId, int numHebras, double[] vectorX, double[] vectorY) {
		this.vectorX = vectorX;
		this.vectorY = vectorY;
		this.miId = miId;
		this.numHebras = numHebras;
	}
	
	public void run() {
		for(int i = miId; i < vectorX.length; i += numHebras) {
			vectorY[ i ] = Math.sin( Math.exp( -vectorX[i] ) + Math.log( 1 + vectorX[i] ) );
		}
	}
}

class HebraBloques extends Thread{
	double[] vectorX,vectorY;
	int miId,numHebras;
	
	public HebraBloques(int miId, int numHebras, double[] vectorX, double[] vectorY) {
		this.vectorX = vectorX;
		this.vectorY = vectorY;
		this.miId = miId;
		this.numHebras = numHebras;
	}
	
	public void run(){
		int n=vectorX.length;
		int tam=(n+numHebras-1)/numHebras;
		int ini=tam*miId;
		int fin=Math.min(n,ini+tam);
		for(int i=ini;i<fin;i++){
			double elemento=vectorX[i];
			vectorY[ i ] = Math.sin( Math.exp( -elemento ) + Math.log( 1 + elemento ) );
		}
	}
}

