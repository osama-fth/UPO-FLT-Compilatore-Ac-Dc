package symbolTable;

import ast.LangType;

public class Attributes {
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
