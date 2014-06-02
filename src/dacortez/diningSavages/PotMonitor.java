package dacortez.diningSavages;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Monitor para o problema do jantar dos selvagens que garante
 * a sincronização para acesso ao pote de comida. 
 * 
 * @author Daniel Augusto Cortez
 * @version 02.06.2014
 */
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
	
	// Número de porções no pote
	private int portions;
	
	// Número de vezes que o pote foi enchido.
	private int filled;
	
	// Arquivo com pontos do gráfico 1
	private PrintStream graphOne;
	
	// Arquivo com pontos do gráfico 2
	private PrintStream graphTwo;

	// Referência a thread atual rodando no monitor 
	private Thread thread;
			
	public PotMonitor(int capacity, int repetitions, SimulationMode mode) {
		this.capacity = capacity;
		this.repetitions = repetitions;
		this.mode = mode;
		potFull = new ConditionVariable();
		potEmpty = new ConditionVariable();
		lock = new Semaphore(1, true);
		portions = filled = 0;
		setupGraphOne();
		setupGraphTwo();
	}

	private void setupGraphOne() {
		try {
			graphOne = new PrintStream("grafico_1.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Não foi possível criar arquivo grafico_1.txt");
			graphOne = null;
		}
	}
	
	private void setupGraphTwo() {
		try {
			graphTwo = new PrintStream("grafico_2.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Não foi possível criar arquivo grafico_2.txt");
			graphTwo = null;
		}
	}
	
	public boolean repetitionsIsFinished() {
		return (repetitions == 0);
	}
	
	public void eatPortion(Savage savage) {
		acquireLock(savage);
		if (repetitions > 0) {
			while (portions == 0) {
				// Primeiro selvagem percebeu o pote vazio
				if (empty(potFull)) {
					System.out.println("(Selvagem " + savage.getName() + " notou o pote vazio)");
					printTotals();
				}
				signal(potEmpty);
				waitForPotToBeFull(savage);
				if (repetitions == 0) {
					lock.release();
					return;
				}
			}
			savage.updateTotalEaten();
			if (--portions == 0 && --repetitions == 0) {
				printTotalsAndSignalAll();
				writePointsOfGraphTwo();
			}
		}
		lock.release();
	}

	private void waitForPotToBeFull(Savage savage) {
		thread = savage;
		if (mode == SimulationMode.UNIFORM)
			wait(potFull);
		else if (mode == SimulationMode.WITH_WEIGHTS)
			wait(potFull, savage.getRank());
	}

	private void printTotalsAndSignalAll() {
		System.out.println("Finalizando...");
		printTotals();
		signal_all(potFull);
		signal_all(potEmpty);
	}
	
	private void writePointsOfGraphTwo() {
		if (graphTwo != null) {
			List<Savage> savages = DiningSavages.getSavages();
			for (int i = 0; i < savages.size(); ++i) 
				graphTwo.println((i + 1) + "\t" + savages.get(i).getTotalEaten());
		}
	}

	private void printTotals() {
		printSavagesTotal();
		printCooksTotal();
	}

	private void printSavagesTotal() {
		for (Savage savage : DiningSavages.getSavages())
			System.out.println("Selvagem " + savage.getName() + " comeu " + savage.getTotalEaten() + " vezes");
	}
	
	private void printCooksTotal() {
		for (Cook cook : DiningSavages.getCooks())
			System.out.println("Cozinheiro " + cook.getName() + " encheu " + cook.getTotalFilled() + " vezes");
	}
	
	public void makePortions(Cook cook) {
		acquireLock(cook);
		if (repetitions > 0) {
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
			appendPointToGraphOne();
			signal_all(potFull);
		}
		lock.release();
	}
	
	private void appendPointToGraphOne() {
		if (graphOne != null) {
			filled++;
			graphOne.println(filled + "\t" + averageFilled());
		}
	}
	
	private double averageFilled() {
		List<Cook> cooks = DiningSavages.getCooks();
		int total = 0;
		for (Cook cook : cooks)
			total += cook.getTotalFilled();
		return ((double) total / cooks.size());
	}
	
	private void acquireLock(Thread thread) {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			System.err.println("Falha ao tentar adquirir a trava: " + thread.getName());
			e.printStackTrace();
		}
	}
	
	// Métodos exigidos pelo enunciado do EP ///////////////////////
	
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
