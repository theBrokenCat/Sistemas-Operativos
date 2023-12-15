package servidor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import ssoo.videos.Transcodificador;

/**
 * 
 * Esta clase se encarga de recoger los trabajos de la cola de trabajos y transcodificar
 * los vídeos que tiene cada trabajo
 * 
 * @author 	Arturo Salvador:	arturo.samayor@alumnos.upm.es
 * @author 	Rubén Braojos:		r.braojos@alumnos.upm.es
 * @version	4/12/2022
 *
 */
public class HiloTranscodificador implements Runnable{
	
	private ColaDeTrabajos colaTrabajos;
	private ConcurrentMap<Integer,Trabajo> mapaTrabajos;
	
	public HiloTranscodificador(ColaDeTrabajos colaTrabajos, ConcurrentHashMap<Integer,Trabajo> mapaTrabajos) {
		this.colaTrabajos = colaTrabajos;
		this.mapaTrabajos = mapaTrabajos;
	}

	/**
	 * 
	 * @param trabajo lo usaremos como auxiliar para depositar aquí el trabajo extraído de la cola
	 * @param transcodificador lo usaremos para transcodificar los vídeos
	 */
	@Override
	public void run() {
		Trabajo trabajo;
		Transcodificador transcodificador = new Transcodificador();
		
		while(true) {
			trabajo = colaTrabajos.outTrabajo();	//sacamos un trabajo de la cola
			
			//procesa el video que le ha dicho el HiloDeTrabajos y se mete en el trabajo el video transcodificado
			trabajo.setVideoTranscodificado(transcodificador.transcodificar(trabajo.getVideo()));
			
			mapaTrabajos.remove(trabajo.getVideo().hashCode(), trabajo);	//elimina del mapa el trabajo ya transcodificado
			
			
			//notificar a todos los hilos de Encargo que ha terminado de procesar el video
			for(Semaphore s:trabajo.getSemaphoreList()) {
				s.release();
			}
			
		}
	}

}
