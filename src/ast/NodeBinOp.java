package ast;

import visitor.IVisitor;

public class NodeBinOp extends NodeExpr {
	private LangOperation op;
	private NodeExpr left;
	private NodeExpr right;

	public NodeBinOp(LangOperation op, NodeExpr left, NodeExpr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public LangOperation getOp() {
		return op;
	}

	public NodeExpr getLeft() {
		return left;
	}

	public NodeExpr getRight() {
		return right;
	}

	public void setOp(LangOperation op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return "NodeBinOp [op=" + op + ", left=" + left + ", right=" + right + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}

}