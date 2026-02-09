package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ast.NodeProgram;
import parser.Parser;
import scanner.Scanner;
import symbolTable.SymbolTable;
import visitor.ErrorType;
import visitor.TypeCheckinVisitor;
import visitor.VoidType;

class TestTypeChecker {

	private static final String PATH = "src/test/data/testTypeChecker/";
	private TypeCheckinVisitor typChecker;

	@BeforeEach
	void setUp() {
		typChecker = new TypeCheckinVisitor();
		SymbolTable.clearTable();
	}

	@Test
	void testDicRipetute() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "1_dicRipetute.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: a già dichiarato!"), typChecker.getResType());
		});
	}

	@Test
	void testIdNonDec() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "2_idNonDec.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: b non è stato dichiarato!"), typChecker.getResType());
		});
	}

	@Test
	void testIdNonDec2() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "3_idNonDec.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: c non è stato dichiarato!"), typChecker.getResType());
		});
	}

	@Test
	void testTipoNonCompatibile() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "4_tipoNonCompatibile.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!"),
					typChecker.getResType());
		});
	}

	@Test
	void testCorretto() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "5_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}

	@Test
	void testCorretto2() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "6_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}

	@Test
	void testCorretto3() {
		assertDoesNotThrow(() -> {
			Parser parser = new Parser(new Scanner(PATH + "7_corretto.txt"));
			NodeProgram result = parser.parse();
			typChecker.visit(result);
			assertEquals(VoidType.class, typChecker.getResType().getClass());
		});
	}
}
