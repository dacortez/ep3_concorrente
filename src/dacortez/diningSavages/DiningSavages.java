package dacortez.diningSavages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiningSavages {

	// Número de selvagens
	private int N;
	
	// Número de cozinheiros
	private int M;
	
	// Capacidade do pote
	private int C;
		
	// Monitor representando o pote de comida
	private PotMonitor pot;
	
	// Lista de selvagens criados
	private List<Savage> savages;
	
	// Lista de cozinheiros criados
	private List<Cook> cooks;
	
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			return;
		}
		DiningSavages problem = new DiningSavages();
		if (problem.parseFile(args[0])) {
			problem.simulate(Integer.parseInt(args[1]), args[2].charAt(0));
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
			e.printStackTrace();
			return false;
		}
	}

	private void tryToParseFile(BufferedReader br) throws IOException {
		N = Integer.parseInt(br.readLine());
		savages = new ArrayList<Savage>(N);
		for (String weight : br.readLine().split("\\s"))
			savages.add(new Savage(Integer.parseInt(weight)));
		
		C = Integer.parseInt(br.readLine());
		pot = new PotMonitor(C);
		
		M = Integer.parseInt(br.readLine());
		cooks = new ArrayList<Cook>(M);
		for (int i = 0; i < M; ++i)
			cooks.add(new Cook());
		
		for (Savage savage : savages) 
			savage.setPot(pot);
		
		for (Cook cook : cooks) 
			cook.setPot(pot);
	}
	
	public void simulate(int repetitions, char mode) {
		System.out.println(N);
		System.out.println(C);
		System.out.println(M);
		System.out.println(repetitions);
		System.out.println(mode);
		
		for (Savage savage : savages) 
			System.out.println(savage.getRank());
		
		for (Cook cook : cooks) 
			cook.start();
		
		for (Savage savage : savages) 
			savage.start();
	}
}
