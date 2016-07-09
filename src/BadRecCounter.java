import java.io.File;
import java.io.IOException;

public class BadRecCounter extends PathCounter {
	int count, connected = 0;
	private Graph graph;
	private boolean[] alreadyConnected;
	private int[][] connections;

	public static void main(String[] args) throws IOException {
		if (args.length != 1)
			throw new IllegalArgumentException("usage: BadRecCounter <graphfile>");
		Graph g = new Graph(new File(args[0]));
		BadRecCounter f = new BadRecCounter(g);
		f.countPaths();
		System.out.println(f.count);
	}

	public BadRecCounter(Graph graph) {
		this.graph = graph;
		alreadyConnected = new boolean[graph.size()];
		connections = graph.connections;
	}

	public long countPaths() {
		countRec(graph.start);
		return count;
	}

	private void countRec(int vertex) {
		if (vertex == graph.end) {
			if (connected == alreadyConnected.length - 1) {
				count++;
			}
			return;
		}
		alreadyConnected[vertex] = true;
		connected++;
		for (int v : connections[vertex]) {
			if (!alreadyConnected[v])
				countRec(v);
		}
		alreadyConnected[vertex] = false;
		connected--;
	}
}
