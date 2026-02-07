package ast;

import visitor.IVisitor;

public class NodeConst extends NodeExpr {
	private String value;
	private LangType type;

	public NodeConst(String valore, LangType tipo) {
		this.value = valore;
		this.type = tipo;
	}

	public LangType getTipo() {
		return this.type;
	}

	public String getValore() {
		return value;
	}

	@Override
	public String toString() {
		return "NodeConst [valore=" + value + ", tipo=" + type + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
}