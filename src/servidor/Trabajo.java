package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import ssoo.videos.Video;

/**
 * 
 * Esta clase es la representación de un Trabajo, ésta contiene un vídeo y el semáforo del HiloEncargo que 
 * ha creado este trabajo para que lo use el HiloTranscodificador
 * 
 * @author 	Arturo Salvador:	arturo.samayor@alumnos.upm.es
 * @author 	Rubén Braojos:		r.braojos@alumnos.upm.es
 * @version	4/12/2022
 *
 */
public class Trabajo {
	
	private Video video;
	private Video videoTranscodificado;
	private List<Semaphore> listaSemaforos;	//se guardan todos los semáforos de todos los distintos hilosEncargo
	
	/**
	 * 
	 * @param video el que se usa para ser transcodificado
	 * @param sem es el semáforo pasado por el HiloEncargo para que lo use el HiloTranscodificador
	 */
	public Trabajo(Video video, Semaphore semaphore) {
		this.video = video;
		listaSemaforos = new ArrayList<Semaphore>();
		listaSemaforos.add(semaphore);
	}
	
	
	/**
	 * 
	 * @return video del trabajo
	 */
	public Video getVideo() {
		return video;
	}
	
	/**
	 * 
	 * @return sem semáforo del HiloEncargo
	 */
	public List<Semaphore> getSemaphoreList() {
		return listaSemaforos;
	}
	
	/**
	 * 
	 * @param sem es el semáforo del nuevo Encargo
	 */
	public void addSemaphore(Semaphore sem) {
		listaSemaforos.add(sem);
	}

	public void setVideoTranscodificado(Video v) {
		this.videoTranscodificado = v;
	}
	
	public Video getVideoTranscodificado() {
		return videoTranscodificado;
	}
	
}
