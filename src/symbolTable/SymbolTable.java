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
		private char registro;

		
		public Attributes(LangType tipo) {
			this.tipo = tipo;
		}

		public LangType getTipo() {
			return this.tipo;
		}

		public void setTipo(LangType type) {
			this.tipo = type;
		}

		public char getRegistro() {
			return this.registro;
		}

		public void setRegistro(char register) {
			this.registro = register;
		}

		@Override
		public String toString() {
			return "[" + this.tipo + ", " + this.registro + "]";
		}
	}

	public static void enter(String id, Attributes entry) {
		table.put(id, entry);
	}

	public static Attributes lookUp(String id) {
		return table.get(id);
	}

	public static void clearTable() {
		table.clear();
	}
}
