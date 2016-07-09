
public class IntArrayMap {
	
	static final int DEFAULT_INITIAL_CAPACITY = 64;

	static final float DEFAULT_LOAD_FACTOR = 0.85f;

	static final int[] PRIMES = {1, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53};
	
	int[] keys;
	Number[] content;
	Number zeroValue = null;
	int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
	int size = 0;
	final int keySize;
	
	public IntArrayMap(int keySize) {
		this.keySize = keySize;
		keys = new int[DEFAULT_INITIAL_CAPACITY*keySize];
		content = new Number[DEFAULT_INITIAL_CAPACITY];
	}
	
	static int hash(int[] values) {
		int h = 0;
		for (int i = 0; i < values.length; i++) {
			h = (int) (h ^ (values[i] >>> 16) ^ (values[i] << 16));
			h ^= (h >>> 7) ^ (h >>> 4);
		}
		return h;
	}
	
	static int indexFor(int h, int length) {
		return h & (length - 1);
	}
	
	final public void put(int[] k, Number n) {
		if (k[0] == 0) {
			int i;
			for (i = 1; i < k.length; i++) {
				if (k[i] != 0)
					break;
			}
			if (i == k.length) {
				zeroValue = n;
				return;
			}
		}

		int index = indexFor(hash(k), content.length);
		w:
		while (true) {
			boolean zero = keys[index*keySize] == 0;
			int i;
			for (i = 0; i < keySize; i++)
				if (keys[index*keySize+i] != (zero ? 0 : k[i]))
					break;
			if (i == k.length)
				break w;
			index += PRIMES[(k[0] ^ k[keySize-1]) & 0xF];
			if (index >= content.length)
				index -= content.length;
		}
		
		System.arraycopy(k, 0, keys, index*keySize, keySize);
		content[index] = n;
		size++;
		if (size == threshold) {
			threshold *= 2;
			size = 0;
			int[] oldKeys = keys;
			Number[] oldContent = content;
			keys = new int[oldKeys.length*2];
			content = new Number[oldContent.length*2];
			for (int i = 0; i < oldContent.length; i++) {
				int j;
				for (j = 0; j < keySize; j++) {
					if (oldKeys[i*keySize+j] != 0)
						break;
				}
				if (j < keySize) {
					int[] kTmp = new int[keySize];
					System.arraycopy(oldKeys, i*keySize, kTmp, 0, keySize);
					put(kTmp, oldContent[i]);
				}
			}
		}
	}
	
	final public int size() {
		return size;
	}

	final public Number get(int[] k) {
		if (k[0] == 0) {
			int i;
			for (i = 1; i < k.length; i++) {
				if (k[i] != 0)
					break;
			}
			if (i == k.length)
				return zeroValue;
		}
		int index = indexFor(hash(k), content.length);
		w:
		while (true) {
			int i;
			for (i = 0; i < keySize; i++)
				if (keys[index*keySize+i] != k[i])
					break;
			if (i == k.length)
				break w;
			index += PRIMES[(k[0] ^ k[keySize-1]) & 0xF];
			if (index >= content.length)
				index -= content.length;
			for (i = 0; i < keySize; i++)
				if (keys[index*keySize+i] != 0)
					break;
			if (i == k.length)
				return null;
		}
		
		return content[index];
	}

	final public void put(long k, Number n) {
		put(new int[]{(int) (k>>>32), (int) k}, n);
	}

	final public Number get(long k) {
		return get(new int[]{(int) (k>>>32), (int) k});
	}

	final public void put(long k, int k2, Number n) {
		put(new int[]{(int) (k>>>32), (int) k, k2}, n);
	}

	final public Number get(long k, int k2) {
		return get(new int[]{(int) (k>>>32), (int) k, k2});
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append('[');
		if (size > 0 && size < 50) {//TODO
			for (int i = 0; i < content.length; i++) {
				int j;
				for (j = 0; j < keySize; j++)
					if (keys[i*keySize+j] != 0)
						break;
				if (j == keySize)
					continue;
				sb.append("(");
				for (j = 0; j < keySize; j++) {
//					sb.append(Integer.toBinaryString(keys[i*keySize+j]));
					sb.append((keys[i*keySize+j]));
					sb.append(",");
				}
				sb.delete(sb.length()-1, sb.length());
				sb.append(")->");
				sb.append(content[i].toString());
				sb.append(", ");
			}
			sb.delete(sb.length()-2, sb.length());
		} else if (size > 0) {
			sb.append("...");
		}
		sb.append(']');
		return sb.toString();
	}
}
