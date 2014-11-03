package practica3;

import java.util.concurrent.atomic.AtomicInteger;

public class EjemploMuestraPrimosEnVector2a {
	// ===========================================================================

	  // -------------------------------------------------------------------------
	  public static void main( String args[] ) {
	    int     numHebras;
	    long    t1, t2;
	    double  tt;
	    /*
	    long    vectorNumeros[] = {
	                200000033L, 200000039L, 200000051L, 200000069L, 
	                200000081L, 200000083L, 200000089L, 200000093L, 
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	                4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
	    };
	    
	    */
	    long    vectorNumeros[] = {
		    200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
		    200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
	    	200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
	    };


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

	    
	    //
	    // Implementacion secuencial.
	    //
	    System.out.println( "" );
	    System.out.println( "Implementacion secuencial." );
	    t1 = System.nanoTime();
	    for( int i = 0; i < vectorNumeros.length; i++ ) {
	    	if( esPrimo( vectorNumeros[ i ] ) ) {
	    		System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
	    	}
	    }
	    t2 = System.nanoTime();
	    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	    System.out.println( "Tiempo secuencial (seg.):                    " + tt );


	    // Implementacion paralela con distribucion ciclica.
	    //
	    System.out.println( "" );
	    System.out.println( "Implementacion ciclica." );
	    t1 = System.nanoTime();
	    Thread[] hebras=new Thread[numHebras];

	    for(int i=0;i<numHebras;i++){
	    	hebras[i]=new MiHebraPrimoDistCiclica(i, numHebras, vectorNumeros);
	    	hebras[i].start();
	    }

	    for(int i=0;i<numHebras;i++){
	    	try {
	    		hebras[i].join();
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    }


	    t2 = System.nanoTime();
	    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	    System.out.println( "Tiempo ciclico (seg.):                    " + tt );


	    //////////////////////////////////////////////////////

	    //Implementacion paralela con distribucion por bloques.
	    System.out.println( "" );
	    System.out.println( "Implementacion por bloques." );
	    t1 = System.nanoTime();
	    Thread[] hebrasBloques=new Thread[numHebras];
	    for(int i=0;i<numHebras;i++){
	    	hebrasBloques[i]=new MiHebraPrimoDistPorBloques(i,numHebras,vectorNumeros);
	    	hebrasBloques[i].start();
	    }

	    for(int i=0;i<numHebras;i++){
	    	try {
	    		hebrasBloques[i].join();
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    }

	    t2 = System.nanoTime();
	    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	    System.out.println( "Tiempo por bloques (seg.):                    " + tt );

	    //////////////////////////////////////////////////

	    //Implementación con distribución dinámica.
	    System.out.println("Implementación con distribución dinàmica.");
	    t1 = System.nanoTime();
	    Thread[] hebrasDinamicas = new Thread[numHebras];
	    
	    AtomicInteger indice = new AtomicInteger(0);
	    
	    for(int i = 0; i < numHebras; i++) {
	    	hebrasDinamicas[i] = new MiHebraPrimoDistDinamica(indice, vectorNumeros);
	    	hebrasDinamicas[i].start();
	    }
	    
	    for(int i = 0; i < numHebras; i++) {
	    	try {
				hebrasDinamicas[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    t2 = System.nanoTime();
	    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
	    System.out.println( "Tiempo dinámico (seg.):                    " + tt );

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

class MiHebraPrimoDistCiclica extends Thread{
	  long    vectorNumeros[];
	  int miId;
	  int numHebras;
	  
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
	  
	  public MiHebraPrimoDistCiclica(int miId,int numHebras,long[] vectorNumeros){
		  this.vectorNumeros=vectorNumeros;
		  this.miId=miId;
		  this.numHebras=numHebras;
	  }
	  
	  public void run(){
		  for(int i=miId;i<vectorNumeros.length;i+=numHebras){
			  if( esPrimo( vectorNumeros[ i ] ) ) {
			        System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
			      }
		  }
	  }
	  
}

class MiHebraPrimoDistPorBloques extends Thread{
	  long    vectorNumeros[];
	  int miId;
	  int numHebras;
	  
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
	  
	  public MiHebraPrimoDistPorBloques(int miId,int numHebras,long[] vectorNumeros){
		  this.vectorNumeros=vectorNumeros;
		  this.miId=miId;
		  this.numHebras=numHebras;
	  }
	  
	  public void run(){
		  int n=vectorNumeros.length;
		  int tam=(n+numHebras-1)/numHebras;
		  int ini=tam*miId;
		  int fin=Math.min(n,ini+tam);
		  for(int i=ini;i<fin;i++){
			 if( esPrimo( vectorNumeros[ i ] ) ) {
				        System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
				      }
			}
		  
	  }
	  
}

class MiHebraPrimoDistDinamica extends Thread {
	AtomicInteger indice;
	long[] vectorNumeros;
	
	public MiHebraPrimoDistDinamica(AtomicInteger indice, long[] vectorNumeros) {
		this.vectorNumeros = vectorNumeros;
		this.indice = indice;
	}
	
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
	
	public void run() {
		int i = indice.getAndIncrement();
		while(i < vectorNumeros.length) {
			if(esPrimo(vectorNumeros[i])) {
				System.out.println("Encontrado primo: " + vectorNumeros[i]);
			}
			i = indice.getAndIncrement();
		}
	}
}
