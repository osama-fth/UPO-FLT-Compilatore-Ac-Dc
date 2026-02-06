package ast;

import visitor.IVisitor;

public class NodeDecl extends NodeDecSt {

	NodeId id;
	LangType type;
	NodeExpr init;

	public NodeDecl(NodeId id, LangType type, NodeExpr init) {
		super();
		this.id = id;
		this.type = type;
		this.init = init;
	}

	public NodeId getId() {
		return id;
	}

	public LangType getType() {
		return type;
	}

	public NodeExpr getInit() {
		return init;
	}

	@Override
	public String toString() {
		return "NodeDecl [id=" + id + ", type=" + type + ", init=" + init + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
}
