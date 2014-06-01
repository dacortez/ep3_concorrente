package dacortez.diningSavages;

public class Savage extends Thread implements Rankable {
	
	// Peso do selvagem
	private int weight;
	
	// Pote (monitor) disponível ao selvagem
	private PotMonitor pot;
	
	public void setPot(PotMonitor pot) {
		this.pot = pot;
	}
	
	public Savage(String name) {
		super(name);
		weight = 0;
	}
	
	public Savage(String name, int weight) {
		super(name);
		this.weight = weight;
	}
	
	@Override
	public int getRank() {
		return weight;
	}

	@Override
	public void run() {
		while (true)
			pot.eatPortion(this);
	}
}
