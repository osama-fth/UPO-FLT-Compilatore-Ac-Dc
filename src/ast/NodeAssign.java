package ast;

import visitor.IVisitor;

public class NodeAssign extends NodeStm {
	private NodeId id;
	private NodeExpr expr;

	public NodeAssign(NodeId id, NodeExpr expr) {
		this.id = id;
		this.expr = expr;
	}

	public NodeExpr getExpr() {
		return this.expr;
	}

	public NodeId getId() {
		return id;
	}

	@Override
	public String toString() {
		return "NodeAssign [id=" + id + ", NodeExpr=" + expr + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
}
