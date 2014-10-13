package practica1;

// ============================================================================
class CuentaIncrementos1a {
// ============================================================================
  long contador = 0;

  // --------------------------------------------------------------------------
  void incrementaContador() {
    contador++;
  }

  // --------------------------------------------------------------------------
  long dameContador() {
    return( contador );
  }
}

class HebraContador extends Thread {
	int miId;
	CuentaIncrementos1a contador;
	
	public HebraContador(int miId, CuentaIncrementos1a contador) {
		this.miId = miId;
		this.contador = contador;
	}
	
	public void run() {
		System.out.println("Hebra: " + miId + " empieza a contar.");
		for(int i = 0; i < 1000000; i++) {
			contador.incrementaContador();
		}
		System.out.println("Hebra: " + miId + " ha terminado de contar.");
	}
}

// ============================================================================
class EjemploIncrementos1a {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    System.out.println( "numHebras: " + numHebras );
    
    CuentaIncrementos1a contador = new CuentaIncrementos1a();
    
    System.out.println("contador: " + contador.dameContador());
    
    HebraContador[] vectorHebras = new HebraContador[numHebras];
    
    for(int i = 0; i < numHebras; i++) {
    	vectorHebras[i] = new HebraContador(i, contador);
    	vectorHebras[i].start();
    }
    
    for(int i = 0; i < numHebras; i++) {
    	try {
			vectorHebras[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    System.out.println("Contador: " + contador.dameContador());
  }
}

