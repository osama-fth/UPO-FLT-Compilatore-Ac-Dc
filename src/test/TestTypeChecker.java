package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ast.LangOperation;
import ast.LangType;
import ast.NodeAssign;
import ast.NodeBinOp;
import ast.NodeConst;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;
import parser.Parser;
import scanner.Scanner;
import symbolTable.SymbolTable;
import visitor.ErrorType;
import visitor.FloatType;
import visitor.IntType;
import visitor.TypeCheckinVisitor;
import visitor.VoidType;

class TestTypeChecker {

	private TypeCheckinVisitor typChecker;

	@BeforeEach
	void setUp() {
		typChecker = new TypeCheckinVisitor();
		SymbolTable.clearTable();
	}

	@Test
	void testDeclCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		typChecker.visit(node);
		assertEquals(IntType.class, typChecker.getResType().getClass());
		assertEquals(LangType.INT, node.getId().getSymbolAttributes().getTipo());
	}

	@Test
	void testDeclAlreadyDeclared() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		typChecker.visit(node);
		typChecker.visit(node);
		assertEquals(new ErrorType("Errore semantico: var1 già dichiarato!"), (typChecker.getResType()));
	}

	@Test
	void testPrintCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		NodePrint nodePrint = new NodePrint(new NodeId("var1"));
		typChecker.visit(node);
		typChecker.visit(nodePrint);
		assertEquals(VoidType.class, typChecker.getResType().getClass());
	}

	@Test
	void testPrintVariabileNotDichiarata() {
		NodePrint nodePrint = new NodePrint(new NodeId("var1"));
		typChecker.visit(nodePrint);
		assertEquals(new ErrorType("Errore semantico: var1 non è stato dichiarato!"), (typChecker.getResType()));
	}

	@Test
	void testNodeDerefTipoCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		typChecker.visit(node);
		NodeDeref nodeDeref = new NodeDeref(new NodeId("var1"));
		typChecker.visit(nodeDeref);
		assertEquals(IntType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeDerefVariableNonDechiarata() {
		NodeDeref nodeDeref = new NodeDeref(new NodeId("var1"));
		typChecker.visit(nodeDeref);
		assertEquals(new ErrorType("Errore semantico: var1 non è stato dichiarato!"), typChecker.getResType());
	}

	@Test
	void testConstFloat() {
		NodeConst n = new NodeConst("42.0", LangType.FLOAT);
		typChecker.visit(n);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testConstInt() {
		NodeConst n = new NodeConst("11", LangType.INT);
		typChecker.visit(n);
		assertEquals(IntType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeBinOpStessoTipo() {
		NodeBinOp node = new NodeBinOp(LangOperation.PLUS, new NodeConst("1", LangType.INT),
				new NodeConst("1", LangType.INT));
		typChecker.visit(node);
		assertEquals(IntType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeBinOpTipoCompatibile() {
		NodeBinOp node = new NodeBinOp(LangOperation.PLUS, new NodeConst("1", LangType.FLOAT),
				new NodeConst("1", LangType.INT));
		typChecker.visit(node);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeBinOpDivisioneSpeciale() {
		NodeBinOp node = new NodeBinOp(LangOperation.DIVIDE, new NodeConst("1", LangType.FLOAT),
				new NodeConst("1", LangType.INT));
		typChecker.visit(node);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
		assertEquals(LangOperation.DIV_FLOAT, node.getOp());
	}

	@Test
	void testNodeBinOpTipoIncompatibile() {
		NodeBinOp node = new NodeBinOp(LangOperation.DIVIDE, new NodeConst("1", LangType.INT),
				new NodeConst("1", LangType.FLOAT));
		typChecker.visit(node);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeAssignIdIntCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		typChecker.visit(node);
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), new NodeConst("1", LangType.INT));
		typChecker.visit(nodeAssign);
		assertEquals(IntType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeAssigIdIntExprFloatCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.INT, null);
		typChecker.visit(node);
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), new NodeConst("1.1", LangType.FLOAT));
		typChecker.visit(nodeAssign);
		assertEquals(new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!"),
				(typChecker.getResType()));
	}

	@Test
	void testNodeAssignIdFloatCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.FLOAT, null);
		typChecker.visit(node);
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), new NodeConst("1", LangType.FLOAT));
		typChecker.visit(nodeAssign);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeAssignIdFloatExprIntIncorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.FLOAT, null);
		typChecker.visit(node);
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), new NodeConst("1", LangType.INT));
		typChecker.visit(nodeAssign);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeAssignBinExpressionCorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.FLOAT, null);
		typChecker.visit(node);
		NodeBinOp nodeExpr = new NodeBinOp(LangOperation.DIVIDE, new NodeConst("1", LangType.FLOAT),
				new NodeConst("1", LangType.INT));
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), nodeExpr);
		typChecker.visit(nodeAssign);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testNodeAssignBinExpressionNoncorretto() {
		NodeDecl node = new NodeDecl(new NodeId("var1"), LangType.FLOAT, null);
		typChecker.visit(node);
		NodeBinOp nodeExpr = new NodeBinOp(LangOperation.DIVIDE, new NodeConst("1", LangType.INT),
				new NodeConst("1", LangType.FLOAT));
		typChecker.visit(nodeExpr);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
		NodeAssign nodeAssign = new NodeAssign(new NodeId("var1"), nodeExpr);
		typChecker.visit(nodeAssign);
		assertEquals(FloatType.class, typChecker.getResType().getClass());
	}

	@Test
	void testDicRipetute() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/1_dicRipetute.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: a già dichiarato!"), typChecker.getResType());
		});

	}

	@Test
	void testIdNonDec() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/2_idNonDec.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: b non è stato dichiarato!"), typChecker.getResType());
		});
	}

	@Test
	void testIdNonDec2() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/3_idNonDec.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: c non è stato dichiarato!"), typChecker.getResType());
		});
	}

	@Test
	void testTipoNonCompatibile() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/4_tipoNonCompatibile.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!"),
					typChecker.getResType());
		});
	}

	@Test
	void testCorretto() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/5_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}

	@Test
	void testCorretto2() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/6_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}

	@Test
	void testCorretto3() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner("src/test/data/testTypeChecker/7_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}
}
