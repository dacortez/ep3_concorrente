package dacortez.diningSavages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

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
	
	public void wait(Thread thread, Semaphore lock) {
		queue.add(thread);
		releaseLockAndAcquireThreadSemaphore(thread, lock);
	}
	
	public void wait(Thread thread, int rank, Semaphore lock) {
		addToQueueWithRank(thread, rank);
		releaseLockAndAcquireThreadSemaphore(thread, lock);
	}

	private void addToQueueWithRank(Thread thread, int rank) {
		for (int i = 0; i < queue.size(); ++i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable) {
				if (((Rankable)element).getRank() < rank) {
					queue.add(i, thread);
					//System.out.println(this);
					return;
				}
			}
		}
		queue.add(thread);
	}
	
	private void releaseLockAndAcquireThreadSemaphore(Thread thread, Semaphore lock) {
		if (!sems.containsKey(thread)) 
			sems.put(thread, new Semaphore(0));
		Semaphore threadSem = sems.get(thread);
		lock.release();
		try {
			threadSem.acquire();
			lock.acquire();
		} catch (InterruptedException e) {
			System.err.println("Erro ao adquirir semaforo: " + thread.getName());
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
	
	public int getMinRank() {
		for (int i = 0; i < queue.size(); ++i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable)
				return ((Rankable)element).getRank();
		}
		return -1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FILA =====================\n");
		for (Thread thread : queue)
			sb.append(thread.toString()).append("\n");
		sb.append("==========================");
		return sb.toString();
	}
}
