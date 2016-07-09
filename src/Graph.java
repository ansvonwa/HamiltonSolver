import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Graph {
	int start, end;
	int[][] connections;
	private static final boolean SHORTEN_CHAINS = false;//works, but disabled for debug purposes

	public Graph(File source) throws IOException {
		Scanner s = new Scanner(source);
		String[] startAndEnd = s.nextLine().split(" ");
		start = Integer.parseInt(startAndEnd[0]);
		end = Integer.parseInt(startAndEnd[1]);
		ArrayList<ArrayList<Integer>> conns = new ArrayList<>();
		while (s.hasNextLine()) {
			String[] otherVertices = s.nextLine().split(" ");
			ArrayList<Integer> al = new ArrayList<>();
			conns.add(al);
			for (int i = 1; i < otherVertices.length; i++) {
				al.add(Integer.parseInt(otherVertices[i]));
			}
		}
		s.close();
		for (int i = 0; i < conns.size(); i++) {
			ArrayList<Integer> c = conns.get(i);
			for (int j = 0; j < c.size(); j++) {
				if (!conns.get(c.get(j)).contains(i))
					conns.get(c.get(j)).add(i);
			}
			// c.sort((a, b) -> a - b);
		}
		if (SHORTEN_CHAINS) {
			for (int i = 0; i < conns.size(); i++) {
				if (start == i || end == i)
					continue;
				ArrayList<Integer> m = conns.get(i);
				if (m.size() == 2) {
					int ir = m.get(0);
					int il = m.get(1);
					ArrayList<Integer> r = conns.get(ir), l = conns.get(il);
					if ((ir != start && ir != end && r.size() == 2) || (il != start && il != end && l.size() == 2)
							|| ((ir == start || ir == end) && r.size() == 1)
							|| ((il == start || il == end) && l.size() == 1)) {// remove m
						l.remove((Integer) i);
						r.remove((Integer) i);
						r.add(il);
						l.add(ir);
						conns.set(i, null);
					}
				}
			}
			int shift = 0;
			for (int i = 0; i < conns.size() - shift; i++) {
				while (conns.get(i + shift) == null) {
					if (shift++ == conns.size())
						break;
				}
				if (shift > 0) {
					if (i + shift == start)
						start -= shift;
					if (i + shift == end)
						end -= shift;
					ArrayList<Integer> e = conns.get(i + shift);
					conns.set(i, e);
					conns.set(i + shift, null);
					for (int j : e) {
						conns.get(j).set(conns.get(j).indexOf(i + shift), i);
					}
				}
			}
			for (int i = 0; i < shift; i++) {
				conns.remove(conns.size() - 1);
			}
		}
//		for (ArrayList<Integer> line : conns) {//never possible to get back to start vertex
//			line.remove(new Integer(start));
//		}
//		conns.get(end).clear();//never possible to leave end vertex
		for (ArrayList<Integer> c : conns) {
			c.sort((a, b) -> a - b);
		}
		connections = new int[conns.size()][];
		for (int i = 0; i < conns.size(); i++) {
			ArrayList<Integer> c = conns.get(i);
			connections[i] = new int[c.size()];
			for (int j = 0; j < c.size(); j++) {
				connections[i][j] = c.get(j);
			}
		}
	}

	public int size() {
		return connections.length;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("[from ");
		sb.append(start);
		sb.append(" to ");
		sb.append(end);
		sb.append("][");
		for (int i = 0; i < connections.length; i++) {
			sb.append(i);
			sb.append(':');
			sb.append(Arrays.toString(connections[i]));
			if (i < connections.length - 1)
				sb.append(", ");
		}
		sb.append(']');
		return sb.toString();
	}
}
