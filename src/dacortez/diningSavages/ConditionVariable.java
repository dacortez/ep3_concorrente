package dacortez.diningSavages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Variável de condição a ser utilizada pelo monitor.
 * Contém uma fila de threads que aguardam a condição ser verdadeira
 * na variável. As threads podem aguardar com prioridade, desde que 
 * implementem a interface Rankable. Threads com maior prioridade 
 * (rank) aguardam no começo da fila. 
 * 
 * @author Daniel Augusto Cortez
 * @version 07.06.2014
 */
public class ConditionVariable {

	// Fila de threads aguardando na variável de condição
	private List<Thread> queue;
	
	// Um semáforo privado por thread
	private HashMap<Thread, Semaphore> sems;

	public ConditionVariable() {
		queue = new ArrayList<Thread>();
		sems = new HashMap<Thread, Semaphore>();
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public void wait(Thread thread, Semaphore monitorLock) {
		queue.add(thread);
		releaseLockAndAcquireThreadSemaphore(thread, monitorLock);
	}
	
	/**
	 * Insere a thread na fila com a prioridade rank. Quanto maior o rank,
	 * maior a prioridade da thread, isto é, a fila é ordenada em ordem 
	 * decrescente de rank.
	 * 
	 * @param thread Thread a ser inserida na fila.
	 * @param rank Prioridade da thread.
	 * @param monitorLock Lock associado ao monitor que usa a fila.
	 */
	public void wait(Thread thread, int rank, Semaphore monitorLock) {
		addToQueueUsingRank(thread, rank);
		releaseLockAndAcquireThreadSemaphore(thread, monitorLock);
	}

	private void addToQueueUsingRank(Thread thread, int rank) {
		for (int i = 0; i < queue.size(); ++i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable) {
				if (((Rankable)element).getRank() < rank) {
					queue.add(i, thread);
					return;
				}
			}
		}
		queue.add(thread);
	}
	
	private void releaseLockAndAcquireThreadSemaphore(Thread thread, Semaphore monitorLock) {
		if (!sems.containsKey(thread)) 
			sems.put(thread, new Semaphore(0));
		Semaphore threadSem = sems.get(thread);
		monitorLock.release();
		try {
			threadSem.acquire();
			monitorLock.acquire();
		} catch (InterruptedException e) {
			System.err.println("Erro ao tentar adquirir semaforo privado: " + thread.getName());
			e.printStackTrace();
		}
	}
	
	public void signal() {
		if (!queue.isEmpty()) {
			Thread first = queue.remove(0);
			Semaphore threadSem = sems.get(first);
			threadSem.release();
		}
	}
	
	public void signalAll() {
		while (!queue.isEmpty()) {
			Thread first = queue.remove(0);
			Semaphore threadSem = sems.get(first);
			threadSem.release();
		}
	}
	
	/**
	 * Devolve a menor prioridade das threads aguardando na fila da variável.
	 * 
	 * @return Rank da thread de menor prioridade (final da fila). 
	 * Retorna -1 se a fila estiver vazia, ou nenhuma thread tiver prioridade.
	 */
	public int getMinRank() {
		for (int i = queue.size() - 1; i >= 0; --i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable)
				return ((Rankable)element).getRank();
		}
		return -1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("==========================\n");
		for (Thread thread : queue)
			sb.append(thread.toString()).append("\n");
		sb.append("==========================");
		return sb.toString();
	}
}
