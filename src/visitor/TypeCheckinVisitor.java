package visitor;

import ast.LangOperation;
import ast.LangType;
import ast.NodeAssign;
import ast.NodeBinOp;
import ast.NodeConst;
import ast.NodeDecSt;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;
import symbolTable.SymbolTable;

public class TypeCheckinVisitor implements IVisitor {

	private TypeDescriptor resType;

	public TypeCheckinVisitor() {
		this.resType = null;
	}

	public TypeDescriptor getResType() {
		return this.resType.getType();
	}

	@Override
	public void visit(NodeProgram node) {
		for (NodeDecSt nodeDecSt : node.getDecSts()) {
			nodeDecSt.accept(this);
		}

		if (this.resType.getClass() == IntType.class || this.resType.getClass() == FloatType.class) {
			this.resType = new VoidType();
		}
	}

	@Override
	public void visit(NodeAssign node) {
		node.getId().accept(this);
		TypeDescriptor idType = this.resType;

		if (idType.getClass() != ErrorType.class) {

			node.getExpr().accept(this);
			TypeDescriptor exprType = this.resType;

			if (exprType.getClass() != ErrorType.class) {

				if (!idType.compatibile(exprType)) {
					this.resType = new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!");
				} else {
					this.resType = idType.getType();
				}
			}
		}

	}

	@Override
	public void visit(NodeId node) {
		SymbolTable.Attributes attributes = SymbolTable.lookUp(node.getName());

		if (attributes == null) {
			this.resType = new ErrorType("Errore semantico: " + node.getName() + " non è stato dichiarato!");
		} else {
			node.setSymbolAttributes(attributes);
			this.resType = (attributes.getTipo() == LangType.INT) ? new IntType() : new FloatType();
		}
	}

	@Override
	public void visit(NodeDecl node) {
		this.visit(node.getId());

		if (this.resType.getClass() == ErrorType.class) {
			SymbolTable.Attributes attributes = new SymbolTable.Attributes();
			attributes.setTipo(node.getType());
			SymbolTable.enter(node.getId().getName(), attributes);
			node.getId().setSymbolAttributes(attributes);
			this.resType = (node.getType() == LangType.INT) ? new IntType() : new FloatType();
		} else {
			this.resType = new ErrorType("Errore semantico: " + node.getId().getName() + " già dichiarato!");
		}
	}

	@Override
	public void visit(NodeConst node) {
		this.resType = (node.getTipo() == LangType.INT) ? new IntType() : new FloatType();

	}

	@Override
	public void visit(NodeBinOp node) {
		node.getLeft().accept(this);
		TypeDescriptor leftTD = this.resType.getType();

		if (leftTD.getClass() != ErrorType.class) {
			node.getRight().accept(this);
			TypeDescriptor rightTD = this.resType.getType();

			if (rightTD.getClass() != ErrorType.class) {

				if (leftTD.getClass() == IntType.class && rightTD.getClass() == IntType.class) {
					this.resType = new IntType();
				} else {
					this.resType = new FloatType();
					if (node.getOp() == LangOperation.DIVIDE) {
						node.setOp(LangOperation.DIV_FLOAT);
					}
				}
			}
		}
	}

	@Override
	public void visit(NodePrint node) {
		this.visit(node.getId());
		this.resType = (this.resType instanceof ErrorType) ? this.resType : new VoidType();
	}

	@Override
	public void visit(NodeDeref node) {
		this.visit(node.getId());

	}
}
