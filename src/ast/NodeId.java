package ast;

import symbolTable.Attributes;
import visitor.IVisitor;

public class NodeId extends NodeAST {

	private String name;
	private Attributes attributes;

	public String getName() {
		return name;
	}

	public NodeId(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "NodeId [name=" + name + "]";
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes Attributes) {
		this.attributes = Attributes;
	}
}
