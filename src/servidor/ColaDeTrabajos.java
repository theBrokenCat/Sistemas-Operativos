package servidor;



import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ssoo.videos.Cola;

/**
 * 
 * Esta clase representa una cola donde vamos a introducir los Trabajos
 * 
 * @author 	Arturo Salvador:	arturo.samayor@alumnos.upm.es
 * @author 	Rub�n Braojos:		r.braojos@alumnos.upm.es
 * @version	4/12/2022
 *
 */
public class ColaDeTrabajos implements Cola{

	private BlockingQueue <Trabajo> cola;
	
	public ColaDeTrabajos() {
		cola = new ArrayBlockingQueue<Trabajo>(6);	//el 6 es el n�mero m�ximo de trabajos en la cola (no nos lo han dado)
	}
	
	
	/**
	 * M�todo que se encarga de introducir un trabajo en la cola de trabajos
	 * @param trabajo que se introducir� en la cola
	 */
	public void inTrabajo(Trabajo trabajo) {
		try {
			cola.put(trabajo);
		} catch (InterruptedException e) {
			System.out.println("[!] Imposible introducir trabajo");
			e.printStackTrace();
		}
	}
	
	/**
	 * M�todo que se encarga de extraer un trabajo de la cola de trabajos
	 * @return trabajo que en el caso de que se haya sacado bien el trabajo retornar� ese mismo trabajo,
	 * si ha habido un error devolver� nulo
	 * 
	 */
	public Trabajo outTrabajo() {
		 try {
			Trabajo trabajo = cola.take();
			return trabajo;
		} catch (InterruptedException e) {
			System.out.println("[!] Imposible sacar trabajo");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * M�todo que se encarga de comprobar si el elemento pasado existe dentro de la cola de trabajos
	 * @param t trabajo por el que buscar en la cola de trabajos
	 * @return true si existe en la cola de trabajos
	 * @return false si el trabajo no est� en la cola de trabajos
	 */
	public boolean contains(Trabajo t) {
		return cola.contains(t);
	}
	
	@Override
	public int numTrabajos() {
		return cola.size();
	}

}
