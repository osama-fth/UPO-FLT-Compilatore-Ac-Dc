package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ast.NodeProgram;
import parser.Parser;
import scanner.Scanner;
import symbolTable.SymbolTable;
import visitor.TypeCheckingVisitor;
import visitor.type.ErrorType;
import visitor.type.OkType;

class TestTypeChecker {

	private static final String PATH = "src/test/data/testTypeChecker/";
	private TypeCheckingVisitor typChecker;
	private Scanner scanner;
	private Parser parser;

	@BeforeEach
	void setUp() {
		typChecker = new TypeCheckingVisitor();
		SymbolTable.pulisciTabella();
	}

	private void inizializzaParser(String fileName) throws Exception {
		scanner = new Scanner(PATH + fileName);
		parser = new Parser(scanner);
	}

	@Test
	void testDicRipetute() throws Exception {
		inizializzaParser("1_dicRipetute.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(new ErrorType("Errore semantico: a già dichiarato!"), typChecker.getResType());
	}

	@Test
	void testIdNonDec() throws Exception {
		inizializzaParser("2_idNonDec.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(new ErrorType("Errore semantico: b non è stato dichiarato!"), typChecker.getResType());
	}

	@Test
	void testIdNonDec2() throws Exception {
		inizializzaParser("3_idNonDec.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(new ErrorType("Errore semantico: c non è stato dichiarato!"), typChecker.getResType());
	}

	@Test
	void testTipoNonCompatibile() throws Exception {
		inizializzaParser("4_tipoNonCompatibile.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!"),
				typChecker.getResType());
	}

	@Test
	void testCorretto() throws Exception {
		inizializzaParser("5_corretto.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(OkType.class, typChecker.getResType().getClass());
	}

	@Test
	void testCorretto2() throws Exception {
		inizializzaParser("6_corretto.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(OkType.class, typChecker.getResType().getClass());
	}

	@Test
	void testCorretto3() throws Exception {
		inizializzaParser("7_corretto.txt");
		NodeProgram result = parser.parse();
		typChecker.visit(result);

		assertEquals(OkType.class, typChecker.getResType().getClass());
	}
}
