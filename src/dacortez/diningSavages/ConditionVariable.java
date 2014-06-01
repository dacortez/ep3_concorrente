package dacortez.diningSavages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConditionVariable {

	// Fila de threads aguardando na variável de condição
	private List<Thread> queue;
	
	// Um semáforo por thread
	private HashMap<Thread, Semaphore> sems;

	public ConditionVariable() {
		queue = new ArrayList<Thread>();
	}
	
	public boolean empty() {
		return queue.isEmpty();
	}
	
	public void wait(Thread thread, Semaphore lock) throws InterruptedException {
		queue.add(thread);
		releaseLockAndAcquireThreadSemaphore(thread, lock);
	}
	
	public void wait(Thread thread, int rank, Semaphore lock) throws InterruptedException {
		addToQueueWithRank(thread, rank);
		releaseLockAndAcquireThreadSemaphore(thread, lock);
	}

	private void addToQueueWithRank(Thread thread, int rank) {
		for (int i = 0; i < queue.size(); ++i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable) {
				if (((Rankable)element).getRank() > rank) {
					queue.add(i, thread);
					return;
				}
			}
		}
		queue.add(thread);
	}
	
	private void releaseLockAndAcquireThreadSemaphore(Thread thread, Semaphore lock) throws InterruptedException {
		if (!sems.containsKey(thread))
			sems.put(thread, new Semaphore(0));
		Semaphore threadSem = sems.get(thread);
		lock.release();
		threadSem.acquire();
		lock.acquire();
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
	
	public int minRank() {
		for (int i = 0; i < queue.size(); ++i) {
			Thread element = queue.get(i);
			if (element instanceof Rankable)
				return ((Rankable)element).getRank();
		}
		return -1;
	}
}
