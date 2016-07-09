import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

public class TestAll {
	public static void main(String[] args) throws IOException {
		new TestAll(g -> new RecCounterMax63Vertices(g));
	}
	
	interface PathCounterProvider {
		PathCounter provide(Graph g);
	}

	public TestAll(PathCounterProvider pcp) throws IOException {
		File graphs = new File("graphs");
		HashMap<File, Long> solutions = new HashMap<>();
		Files.lines(new File(graphs, "Beispielloesungen1.txt").toPath())
				.filter(s -> s.matches("graph[0-9]*[a-z].txt [0-9]+"))
				.forEach(s -> solutions.put(
						new File(graphs, s.substring(0, s.indexOf(' '))),
						Long.parseLong(s.substring(s.indexOf(' ')+1))));
		long start = System.currentTimeMillis();
		File[] files = graphs.listFiles();
		Arrays.sort(files);
		for (File f : files) {
			long s = System.currentTimeMillis();
			if (!f.getName().matches("graph[0-9]*[a-z].txt"))
				continue;
			System.out.print(f.getName()+" => ");
			Graph g = new Graph(new File(graphs, f.getName()));
			PathCounter pc = pcp.provide(g);
			try {
				long count = pc.countPaths();
				if (solutions.containsKey(f)) {
					if (solutions.get(f) != count) {
						System.out.println("wrong solution");
					} else {
						System.out.println("succeeded");
					}
				} else {
					System.out.println("unknown, but no error");
				}
			} catch (Exception e) {
				System.out.println("failed");
			}
			System.out.println("time: "+(System.currentTimeMillis()-s));
		}
		System.out.println((System.currentTimeMillis()-start)/1000.0);
	}
}
