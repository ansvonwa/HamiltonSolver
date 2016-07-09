import java.io.File;
import java.io.IOException;

public class RecCounterMax63Vertices extends PathCounter {
	private int size;
	private Graph graph;
	private int[][] connections;
	private IntArrayMap buffer = new IntArrayMap(3);
	private int maxBitsToBuf, bufMod = 5;
	private long[] connectionMask;


	public static void main(String[] args) throws IOException {
		Graph g = new Graph(new File(args.length >= 1 ? args[0] : "graphs/graph52a.txt"));
		RecCounterMax63Vertices rc63 = new RecCounterMax63Vertices(g);
		System.out.println(rc63.countPaths());
	}

	public RecCounterMax63Vertices(Graph graph) {
		this.graph = graph;
		connections = graph.connections;
		size = graph.size();
		connectionMask = new long[size];
		for (int i = 0; i < connections.length; i++) {
			for (int j = 0; j < connections[i].length; j++) {
				connectionMask[i] += 1l << connections[i][j];
			}
		}
		maxBitsToBuf = size*2/3;
	}

	public long countPaths() {
		if (size <= 0 || size > 63)
			throw new IndexOutOfBoundsException();
		return countRecGDetection(graph.start, 0l, 0);
	}

	 private long countRecGDetection(int vertex, long alreadyConnected, int depth) {
		if (vertex == graph.end) {
			return (alreadyConnected + 1l + (1l << vertex)) >>> size;
		}
		alreadyConnected ^= 1l << vertex;
		if (depth < maxBitsToBuf && depth % bufMod == 0) {
			Number b = buffer.get(alreadyConnected, vertex);
			if (b != null)
				return (long) b;
		}
		long c = 0;
		outer:
		for (int v : connections[vertex]) {
			if ((alreadyConnected & (1l << v)) == 0) {
				for (int otherV : connections[vertex]) {
					//skip vertices that would lead to "small" impasses
					if (otherV != v
							&& (alreadyConnected & (1l << otherV)) == 0
							&& numberOfSetBits(connectionMask[otherV] & (~alreadyConnected))
									<= (otherV == graph.end ? 0 : 1)) {
						continue outer;
					}
				}
				c += countRecGDetection(v, alreadyConnected, depth + 1);
			}
		}
		if (depth < maxBitsToBuf && depth % bufMod == 0) {
			buffer.put(alreadyConnected, vertex, c);
		}
		return c;
	}
	
	static int numberOfSetBits(long l) {
		return numberOfSetBits((int) l) + numberOfSetBits((int) (l >>> 32));
	}

	static int numberOfSetBits(int i) {
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}
}
