package symbolTable;

import java.util.HashMap;

public class SymbolTable {
	private static HashMap<String, Attributes> table;

	static {
		table = new HashMap<>();
	}

	public static void enter(String id, Attributes entry) {
		table.put(id, entry);
	}

	public static Attributes lookUp(String id) {
		return table.get(id);
	}

	public static String toStr() {
		StringBuilder sb = new StringBuilder();
		for (String key : table.keySet()) {
			sb.append(key).append(": ").append(table.get(key)).append("\n");
		}
		return sb.toString();
	}

	public static int size() {
		return table.size();
	}

	public static void clearTable() {
		table.clear();
	}
}
