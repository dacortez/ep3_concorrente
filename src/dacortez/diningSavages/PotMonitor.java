package dacortez.diningSavages;

import java.util.concurrent.Semaphore;

public class PotMonitor {
	
	private static int cfull = 0;
	
	// Capacidade C >= 1 de porções do pote.
	private int capacity;
	
	// Variável de condição associada ao pote cheio
	private ConditionVariable potFull;
	
	// Variável de condição associada ao pote vazio
	private ConditionVariable potEmpty;
	
	// Lock para garantir exclusão mútua
	private Semaphore lock;
	
	// Número de porções no pote.
	private int portions;
	
	// Referência a thread atual rodando no monitor 
	private Thread thread;
			
	public PotMonitor(int capacity) {
		this.capacity = capacity;
		potFull = new ConditionVariable();
		potEmpty = new ConditionVariable();
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
	private boolean empty(ConditionVariable cv) {
		return cv.isEmpty();
	}
	
	private void wait(ConditionVariable cv)  {
		cv.wait(thread, lock);
	}
	
	@SuppressWarnings("unused")
	private void wait(ConditionVariable cv, int rank) {
		cv.wait(thread, rank, lock);
	}
	
	private void signal(ConditionVariable cv) {
		cv.signal();
	}
	
	private void signal_all(ConditionVariable cv) {
		cv.signalAll();
	}
	
	@SuppressWarnings("unused")
	private int minrank(ConditionVariable cv) {
		return cv.getMinRank();
	}
}
