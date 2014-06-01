package dacortez.diningSavages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiningSavages {

	// Número N > 1 de selvagens
	private int numberOfSavages;
	
	// Número M > 1 de cozinheiros
	private int numberOfCooks;
	
	// Capacidade C > 1 do pote
	private int capacity;
			
	// Lista de selvagens
	private static List<Savage> savages;
	
	// Lista de cozinheiros
	private static List<Cook> cooks;
	
	public static List<Savage> getSavages() {
		return savages;
	}
	
	public static List<Cook> getCooks() {
		return cooks;
	}
	
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			return;
		}
		DiningSavages problem = new DiningSavages();
		if (problem.parseFile(args[0])) {
			SimulationMode mode = args[2].startsWith("P") ? SimulationMode.WITH_WEIGHTS : SimulationMode.UNIFORM;
			problem.simulate(Integer.parseInt(args[1]), mode);
		}
	}

	private static void printUsage() {
		System.out.println("Uso: java -jar DiningSavages <arquivo> <R> <U|P>");
		System.out.println("onde:");
		System.out.println("  arquivo \t Arquivo de entrada");
		System.out.println("        R \t Número de repetições");
		System.out.println("        U \t Situação uniforme");
		System.out.println("        P \t Situação com peso");
	}
	
	public boolean parseFile(String file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			tryToParseFile(br);
			return true;
		} catch (IOException e) {
			System.err.println("Arquivo de entrada não pôde ser lido.");
			e.printStackTrace();
			return false;
		} catch (NumberFormatException e) {
			System.err.println("Arquivo de entrada mal formatado.");
			e.printStackTrace();
			return false;
		}
	}

	private void tryToParseFile(BufferedReader br) throws IOException {
		numberOfSavages = Integer.parseInt(br.readLine());
		createSavages(br.readLine().split("\\s"));
		capacity = Integer.parseInt(br.readLine());
		numberOfCooks = Integer.parseInt(br.readLine());
		createCooks();
	}

	private void createSavages(String[] weights) {
		savages = new ArrayList<Savage>(numberOfSavages);
		for (int i = 0; i < weights.length; ++i)
			savages.add(new Savage("S" + (i + 1), Integer.parseInt(weights[i])));
	}
	
	private void createCooks() {
		cooks = new ArrayList<Cook>(numberOfCooks);
		for (int i = 0; i < numberOfCooks; ++i)
			cooks.add(new Cook("C" + (i + 1)));
	}
	
	public void simulate(int repetitions, SimulationMode mode) {
		printInput(repetitions, mode);
		PotMonitor pot = new PotMonitor(capacity, repetitions, mode);
		setPotToCooks(pot);
		setPotToSavages(pot);
		startCooks();
		startSavages();
	}
	
	private void printInput(int repetitions, SimulationMode mode) {
		System.out.println("N = " + numberOfSavages);
		System.out.println("C = " + capacity);
		System.out.println("M = " + numberOfCooks);
		System.out.println("R = " + repetitions);
		System.out.println("U|W = " + mode);
		for (Savage savage : savages) 
			System.out.println("Peso(" + savage.getName() + ") = " + savage.getRank());
	}
	
	private void setPotToCooks(PotMonitor pot) {
		for (Cook cook : cooks) 
			cook.setPot(pot);
	}
	
	private void setPotToSavages(PotMonitor pot) {
		for (Savage savage : savages) 
			savage.setPot(pot);
	}
	
	private void startCooks() {
		for (Cook cook : cooks) 
			cook.start();
	}

	private void startSavages() {
		for (Savage savage : savages) 
			savage.start();
	}
}
