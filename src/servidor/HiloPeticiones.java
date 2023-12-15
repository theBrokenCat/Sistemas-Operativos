package servidor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ssoo.videos.servidor.Peticion;
import ssoo.videos.servidor.ReceptorPeticiones;


/**
 * 
 * Esta clase se encarga de recibir las peticiones de los clientes y lanzar los HilosEncargo necesarios
 * además de ejecutar los hilos transcodificadores	(Se trata de la clase principal)
 * 
 * @author 	Arturo Salvador:	arturo.samayor@alumnos.upm.es
 * @author 	Rubén Braojos:		r.braojos@alumnos.upm.es
 * @version	4/12/2022
 *
 */
public class HiloPeticiones {
	
	private static List<Thread> listaTranscodificadores;
	//private static ConcurrentMap<Integer,Trabajo> mapaTrabajos;	
	
	public static void main(String args[]) throws IOException {
		final int MAX_CODIFICADORES = Runtime.getRuntime().availableProcessors()-1;	//número de procesadores de mi ordenador 
		
		ColaDeTrabajos colaTrabajos = new ColaDeTrabajos(); 									//Cola donde situaremos todos los Trabajos
		ConcurrentMap<Integer,Trabajo> mapaTrabajos = new ConcurrentHashMap<Integer,Trabajo>();	//mapa donde comprobaremos la existencia de los trabajos
		
		//iniciamos los Transcodificadores
		initTranscodificadores(MAX_CODIFICADORES, colaTrabajos, (ConcurrentHashMap<Integer, Trabajo>) mapaTrabajos);
		

		ReceptorPeticiones o = new ReceptorPeticiones();
		while(true) {
			Peticion peticion = o.recibirPeticion();//devuelve una petición
			
			//Lanzamos un Hilo Encargo pasándole la petición, la cola de Trabajos, el Cliente y el mapa de Trabajos
			Thread encargo = new Thread(new HiloEncargo(peticion, colaTrabajos, (ConcurrentHashMap<Integer, Trabajo>) mapaTrabajos));
			encargo.start();
		}
		
	}
	
	/**
	 * 
	 * @param max_cod se trata del número máximo de Transcodificadores
	 * @param colaTrabajos es la cola donde se van a situar todos los trabajos
	 * 
	 */
	private static void initTranscodificadores(final int max_cod, ColaDeTrabajos colaTrabajos, ConcurrentHashMap<Integer,Trabajo> mapaTrabajos) {
		
			//creo una lista donde estarán todos los transcodificadores
			listaTranscodificadores = new ArrayList<Thread>(max_cod);
				
			//meto el número de transcodificadores que tendremos en la lista
			for(int i=0; i<max_cod;i++) {
				listaTranscodificadores.add(new Thread(new HiloTranscodificador(colaTrabajos, mapaTrabajos)));
				listaTranscodificadores.get(i).start();
			}
	}
	
}
