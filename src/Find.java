import java.io.File;

public class Find {
	public static void main(String[] args) throws Exception {
		if (args.length != 1)
			throw new IllegalArgumentException("usage: Find <graphfile>");
		Graph g = new Graph(new File(args[0]));
		int size = g.size();
		PathCounter c;
		if (size <= 63) {
			c = new RecCounterMax63Vertices(g);
		} else {
			c = new BadRecCounter(g);
		}
		System.out.println(c.countPaths());
	}
}
