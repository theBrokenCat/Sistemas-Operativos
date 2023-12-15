package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import ssoo.videos.Dvd;
import ssoo.videos.Encargo;
import ssoo.videos.MenuRaiz;
import ssoo.videos.Video;
import ssoo.videos.servidor.Peticion;

/**
 * 
 * Esta clase se encargar� de recibir las peticiones de los clientes para posteriormente
 * crear de �stas unos trabajos que ser�n modificados por los Transcodificadores
 * 
 * @author 	Arturo Salvador:	arturo.samayor@alumnos.upm.es
 * @author 	Rub�n Braojos:		r.braojos@alumnos.upm.es
 * @version	4/12/2022
 *
 */
public class HiloEncargo implements Runnable{
	
	private Peticion peticion;
	private ColaDeTrabajos colaTrabajos;
	private static Trabajo t_aux;		//hace referencia al trabajo que vamos a meter en la cola de trabajos
										//lo creo aqu� para que el hilo auxiliar pueda acceder a �ste
	
	private ConcurrentMap<Integer,Trabajo> mapaTrabajos;
	
	public HiloEncargo(Peticion peticion, ColaDeTrabajos colaTrabajos, ConcurrentHashMap<Integer,Trabajo> mapaTrabajos) {
		
		this.peticion = peticion;
		this.colaTrabajos = colaTrabajos;
		this.mapaTrabajos = mapaTrabajos;
		
	}

	/**
	 * 
	 * @param encargo es el encargo que est� dentro de la petici�n enviada por el cliente
	 * @param numTrabajos es la cantidad de trabajos que tiene cada encargo
	 * @param sem es el sem�foro que vamos a usar para detener esta clase para esperar
	 * a la terminaci�n del resto de hilos transcodificadores
	 * 
	 */
	@Override
	public void run() {
		
		
		Encargo encargo = peticion.getEncargo();

		int numTrabajos;
		numTrabajos = encargo.getVideos().size();

		List<Trabajo> 	listaTrabajos = new ArrayList<Trabajo>();	//lista donde guardaremos los trabajos de la petici�n
		List<Video>		listaVideos = new ArrayList<Video>();		//lista en la que se guardar�n los v�deos transcodificados
		
		Semaphore sem = new Semaphore(-numTrabajos+1);	//sem�foro que esperar� tantas veces como trabajos haya en el encargo
		
		
		//hilo que se va a encargar de meter los trabajos en la cola de trabajos
		//Explicaci�n del por qu�: si la cola de trabajos se llena, el hilo Encargo se quedar� bloqueado, sin poder 
		//recibir la notificaci�n de si alguno de sus trabajos ya metidos en la cola han terminado o no
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				//recorro mi lista de v�deos para crear Trabajos y meterlos en la cola de Trabajos
				for(Video v:encargo.getVideos()) {
						
					synchronized(mapaTrabajos) {		//tengo que asegurarme de que el mismo trabajo no se meta dos veces por hilos distintos
						
						if(mapaTrabajos.containsKey(v.hashCode())){	//si el trabajo ya existe en la cola de Trabajos 
							mapaTrabajos.get(v.hashCode()).addSemaphore(sem);	//cogemos el Trabajo ya existente y le a�adimos el sem�foro de este HiloEncargo
							listaTrabajos.add(mapaTrabajos.get(v.hashCode()));	//cogemos el Trabajo ya existente y lo metemos en la lista de Trabajos
						}else {
							t_aux = new Trabajo(v, sem);			//creo un trabajo auxiliar con el video y el sem�foro del HiloEncargo
							mapaTrabajos.put(v.hashCode(), t_aux);	//meto el trabajo en el mapa de Trabajos
							colaTrabajos.inTrabajo(t_aux);			//meto el trabajo en la cola de Trabajos
							listaTrabajos.add(t_aux);				//meto el trabajo en mi lista de Trabajos
						}
					}
				}
			}
			
		}); 
		
		//hacemos correr al hilo encargado de meter los trabajos en la cola
		t1.start();
		
		
		//espera a que todos los transcodificadores terminen
		try {
			sem.acquire();	//esperamos hasta que terminen todos los v�deos de ser transcodificados
			t1.join();		//esperamos a la terminaci�n del hilo encargado de meter los trabajos a la cola
			
			//metemos en listaVideos los v�deos transcodificados, situados en cada Trabajo
			for(Trabajo j:listaTrabajos) {	
				listaVideos.add(j.getVideoTranscodificado());
			}
			
			
			//**********CREACI�N DEL DVD Y ENV�O**********
			sendDvd(encargo, listaVideos);
			
		} catch (InterruptedException e1) {
			System.out.println("[!] Error en HiloEncargo");
			e1.printStackTrace();
		}
		
	}
	
	/**
	 * M�todo encargado de crear el Men� Ra�z y pas�rselo junto con los v�deos transcodificados
	 * al cliente
	 * 
	 * @param encargo
	 * @param lista_videos
	 */
	private void sendDvd(Encargo encargo, List<Video> listaVideos) {
		
				MenuRaiz menu = new MenuRaiz(listaVideos);
				Dvd dvd = new Dvd(encargo.getTitulo(), menu, listaVideos);
				
				peticion.getCliente().enviar(dvd);
	}
	
}
