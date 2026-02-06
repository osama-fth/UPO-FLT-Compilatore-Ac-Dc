package ast;

import visitor.IVisitor;

public class NodeConst extends NodeExpr {
	private String valore;
	private LangType tipo;

	public NodeConst(String valore, LangType tipo) {
		this.valore = valore;
		this.tipo = tipo;
	}

	public LangType getTipo() {
		return this.tipo;
	}

	public String getValore() {
		return valore;
	}

	@Override
	public String toString() {
		return "NodeConst [valore=" + valore + ", tipo=" + tipo + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
}