package dacortez.diningSavages;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class PotMonitor {
	
	private static int cfull = 0;
	
	// Capacidade C >= 1 de porções do pote.
	private int capacity;
	
	// Variável de condição associada ao pote cheio
	private Queue<Thread> potFull;
	
	// Variável de condição associada ao pote vazio
	private Queue<Thread> potEmpty;
	
	// Um semáforo por processo
	private HashMap<Thread, Semaphore> semaphores;
	
	// Lock para garantir exclusão mútua
	private Semaphore lock;
	
	// Número de porções no pote.
	private int portions;
	
	// Referência a thread rodando no monitor 
	private Thread thread;
			
	public PotMonitor(int capacity) {
		this.capacity = capacity;
		potFull = new LinkedList<Thread>();
		potEmpty = new LinkedList<Thread>();
		semaphores = new HashMap<Thread, Semaphore>();
		lock = new Semaphore(1);
		portions = 0;
	}
	
	public void eatPortion(Savage savage) throws InterruptedException {
		lock.acquire();
		while (portions == 0) {
			signal(potEmpty);
			thread = savage;
			wait(potFull);
		}
		portions--;
		lock.release();
	}
	
	public void makePortions(Cook cook) throws InterruptedException {
		lock.acquire();
		while (portions > 0) {
			thread = cook;
			wait(potEmpty);
		}
		portions = capacity;
		System.out.println("Pote cheio " + cfull++);
		signal_all(potFull);
		lock.release();
	}
	
	@SuppressWarnings("unused")
	private boolean empty(Queue<Thread> cv) {
		return cv.isEmpty();
	}
	
	private void wait(Queue<Thread> cv) throws InterruptedException {
		cv.add(thread);
		if (!semaphores.containsKey(thread))
			semaphores.put(thread, new Semaphore(0));
		Semaphore sem = semaphores.get(thread);
		lock.release();
		sem.acquire();
		lock.acquire();
	}
	
	@SuppressWarnings("unused")
	private void wait(Queue<Thread> cv, int rank) throws InterruptedException {
		// TODO
		wait(cv);
	}
	
	private void signal(Queue<Thread> cv) {
		if (!cv.isEmpty()) {
			Thread first = cv.poll();
			Semaphore sem = semaphores.get(first);
			sem.release();
		}
	}
	
	private void signal_all(Queue<Thread> cv) {
		while (!cv.isEmpty()) {
			Thread first = cv.poll();
			Semaphore sem = semaphores.get(first);
			sem.release();
		}
	}
	
	@SuppressWarnings("unused")
	private int minrank(Queue<Thread> cv) {
		// TODO
		return 0;
	}
}
