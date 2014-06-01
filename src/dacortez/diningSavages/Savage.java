package dacortez.diningSavages;

public class Savage extends Thread implements Rankable {
	
	// Peso do selvagem
	private int weight;
	
	// Pote (monitor) disponível ao selvagem
	private PotMonitor pot;
	
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	public Savage() {
		super();
		weight = 0;
	}
	
	public Savage(int weight) {
		super();
		this.weight = weight;
	}
	
	@Override
	public int getRank() {
		return weight;
	}

	@Override
	public void run() {
		while (true) {
			try {
				pot.eatPortion(this);
			} catch (InterruptedException e) {
				System.err.println("Selvagem " + getId());
				e.printStackTrace();
			}
		}
	}
}
