package visitor;

import ast.NodeAssign;
import ast.NodeBinOp;
import ast.NodeConst;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;

public interface IVisitor {
	public abstract void visit(NodeProgram node);

	public abstract void visit(NodeAssign node);

	public abstract void visit(NodeId node);

	public abstract void visit(NodeDecl node);

	public abstract void visit(NodeConst node);

	public abstract void visit(NodeBinOp node);

	public abstract void visit(NodePrint node);

	public abstract void visit(NodeDeref node);
}
