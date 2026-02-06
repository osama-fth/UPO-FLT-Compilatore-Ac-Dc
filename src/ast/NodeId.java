package ast;

import symbolTable.SymbolTable;
import visitor.IVisitor;

public class NodeId extends NodeAST {

	private String name;
	private SymbolTable.Attributes symbolAttributes;

	public SymbolTable.Attributes getSymbolAttributes() {
		return symbolAttributes;
	}

	public void setSymbolAttributes(SymbolTable.Attributes symbolAttributes) {
		this.symbolAttributes = symbolAttributes;
	}

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
}
