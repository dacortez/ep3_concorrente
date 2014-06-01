package dacortez.diningSavages;

import java.util.concurrent.Semaphore;

public class PotMonitor {
	
	// Capacidade C > 0 de porções do pote
	private int capacity;
	
	// Número de vezes que o pote deve esvaziar
	private int repetitions;
	
	// Modo de execução do monitor (uniforme ou com pesos)
	private SimulationMode mode;
	
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
			
	public PotMonitor(int capacity, int repetitions, SimulationMode mode) {
		this.capacity = capacity;
		this.repetitions = repetitions;
		this.mode = mode;
		potFull = new ConditionVariable();
		potEmpty = new ConditionVariable();
		lock = new Semaphore(1, true);
		portions = 0;
	}
	
	public boolean repetitionsIsFinished() {
		return repetitions == 0;
	}
	
	public void eatPortion(Savage savage) {
		acquireLock(savage);
		if (repetitions == 0) {
			lock.release();
			return;
		}
		while (portions == 0) {
			// Primeiro selvagem percebeu o pote vazio
			if (empty(potFull)) {
				System.out.println("<Selvagem " + savage.getName() + " notou o pote vazio>");
				printTotals();
			}
			signal(potEmpty);
			thread = savage;
			if (mode == SimulationMode.UNIFORM)
				wait(potFull);
			else if (mode == SimulationMode.WITH_WEIGHTS)
				wait(potFull, savage.getRank());
			if (repetitions == 0) {
				lock.release();
				return;
			}
		}
		savage.updateTotalEaten();
		if (--portions == 0 && --repetitions == 0) {
			printTotals();
			signal_all(potFull);
			signal_all(potEmpty);
		}
		lock.release();
	}

	private void printTotals() {
		for (Savage savage : DiningSavages.getSavages())
			System.out.println("Selvagem " + savage.getName() + " comeu " + savage.getTotalEaten() + " vezes");
		for (Cook cook : DiningSavages.getCooks())
			System.out.println("Cozinheiro " + cook.getName() + " encheu " + cook.getTotalFilled() + " vezes");
	}
	
	public void makePortions(Cook cook) {
		acquireLock(cook);
		if (repetitions == 0) {
			lock.release();
			return;
		}
		while (portions > 0) {
			thread = cook;
			wait(potEmpty);
			if (repetitions == 0) {
				lock.release();
				return;
			}
		}
	
		// Cozinheiro encheu o pote
		portions = capacity;
		cook.updateTotalFilled();
		System.out.println("[Cozinheiro " + cook.getName() + " encheu o pote]");
	
		signal_all(potFull);
		lock.release();
	}
	
	private void acquireLock(Thread thread) {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			System.err.println("Falha ao tentar adquirir a trava: " + thread.getName());
			e.printStackTrace();
		}
	}
	
	//////////////// Métodos exigidos pelo enunciado do EP ////////////////
	
	private boolean empty(ConditionVariable cv) {
		return cv.isEmpty();
	}
	
	private void wait(ConditionVariable cv)  {
		cv.wait(thread, lock);
	}
	
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
