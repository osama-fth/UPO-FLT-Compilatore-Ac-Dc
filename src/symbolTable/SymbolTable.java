package symbolTable;

import java.util.HashMap;

import ast.LangType;

public class SymbolTable {
	private static HashMap<String, Attributes> table;

	static {
		table = new HashMap<>();
	}

	public static class Attributes {
		private LangType tipo;
		private char register;

		public Attributes() {
		}

		public Attributes(LangType tipo, char register) {
			this.tipo = tipo;
			this.register = register;
		}

		public Attributes(LangType tipo) {
			this.tipo = tipo;
		}

		public Attributes(char register) {
			this.register = register;
		}

		public LangType getTipo() {
			return this.tipo;
		}

		public void setTipo(LangType type) {
			this.tipo = type;
		}

		public char getRegister() {
			return this.register;
		}

		public void setRegister(char register) {
			this.register = register;
		}

		@Override
		public String toString() {
			return "[" + this.tipo + ", " + this.register + "]";
		}
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
			sb.append(key).append(" = [").append(table.get(key).tipo).append("][").append(table.get(key).register)
					.append("]\n");
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
