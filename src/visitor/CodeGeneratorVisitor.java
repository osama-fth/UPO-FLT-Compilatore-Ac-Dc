package visitor;

import ast.NodeAssign;
import ast.NodeBinOp;
import ast.NodeConst;
import ast.NodeDecSt;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;
import symbolTable.Attributes;
import symbolTable.SymbolTable;
import visitor.codegen.Registri;

public class CodeGeneratorVisitor implements IVisitor {

	private String codiceDc;
	private String log;

	public CodeGeneratorVisitor() {
		this.codiceDc = "";
		this.log = "";
	}

	public String getCodiceDc() {
		return codiceDc;
	}

	public String getLog() {
		return log;
	}

	@Override
	public void visit(NodeProgram node) {
		for (NodeDecSt nodeDecSt : node.getDecSts()) {
			if (!log.isEmpty()) {
				return;
			}
			nodeDecSt.accept(this);
		}
	}

	@Override
	public void visit(NodeDecl node) {
		if (!log.isEmpty()) {
			return;
		}

		char registro = Registri.newRegister();
		if (registro == ' ') {
			log = "Errore: registri esauriti per la variabile " + node.getId().getName();
			return;
		}

		Attributes attr = SymbolTable.lookUp(node.getId().getName());
		attr.setRegistro(registro);

		if (node.getInit() != null) {
			node.getInit().accept(this);
			if (!log.isEmpty()) {
				return;
			}
			codiceDc += "s" + registro + " ";
		}
	}

	@Override
	public void visit(NodeAssign node) {
		if (!log.isEmpty()) {
			return;
		}

		node.getExpr().accept(this);
		if (!log.isEmpty()) {
			return;
		}

		char registro = node.getId().getAttributes().getRegistro();
		codiceDc += "s" + registro + " ";
	}

	@Override
	public void visit(NodePrint node) {
		if (!log.isEmpty()) {
			return;
		}

		char registro = node.getId().getAttributes().getRegistro();
		codiceDc += "l" + registro + " p P ";
	}

	@Override
	public void visit(NodeBinOp node) {
		if (!log.isEmpty()) {
			return;
		}

		node.getLeft().accept(this);
		if (!log.isEmpty()) {
			return;
		}

		node.getRight().accept(this);
		if (!log.isEmpty()) {
			return;
		}

		switch (node.getOp()) {
		case PLUS -> codiceDc += "+ ";
		case MINUS -> codiceDc += "- ";
		case TIMES -> codiceDc += "* ";
		case DIVIDE -> codiceDc += "/ ";
		case DIV_FLOAT -> codiceDc += "5 k / 0 k ";
		default -> log = "Operazione non supportata: " + node.getOp();
		}
	}

	@Override
	public void visit(NodeDeref node) {
		if (!log.isEmpty()) {
			return;
		}

		char registro = node.getId().getAttributes().getRegistro();
		codiceDc += "l" + registro + " ";
	}

	@Override
	public void visit(NodeConst node) {
		if (!log.isEmpty()) {
			return;
		}

		codiceDc += node.getValore() + " ";
	}

	@Override
	public void visit(NodeId node) {
	}
}
