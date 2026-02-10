package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ast.NodeProgram;
import parser.Parser;
import parser.SyntacticException;
import scanner.Scanner;

class TestParser {

	private static final String PATH = "src/test/data/testParser/";

	@Test
	void testParserCorretto1() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserCorretto1.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(() -> {
			parser.parse();
		});
	}

	@Test
	void testParserCorretto2() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserCorretto2.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(() -> {
			parser.parse();
		});
	}

	@Test
	void testParserEcc_0() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_0.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Token non atteso SEMI a riga 1", e.getMessage());
	}

	@Test
	void testParserEcc_1() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_1.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Token non atteso TIMES a riga 2", e.getMessage());
	}

	@Test
	void testParserEcc_2() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_2.txt");
		Parser parser = new Parser(scanner);
		SyntacticException exc = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Token non atteso INT a riga 3", exc.getMessage());
	}

	@Test
	void testParserEcc_3() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_3.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Token non atteso PLUS a riga 2", e.getMessage());
	}

	@Test
	void testParserEcc_4() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_4.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Atteso ID, ma trovato INT a riga 2", e.getMessage());
	}

	@Test
	void testParserEcc_5() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_5.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Atteso ID, ma trovato INT a riga 3", e.getMessage());
	}

	@Test
	void testParserEcc_6() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_6.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Atteso ID, ma trovato TYFLOAT a riga 3", e.getMessage());
	}

	@Test
	void testParserEcc_7() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_7.txt");
		Parser parser = new Parser(scanner);
		SyntacticException e = assertThrows(SyntacticException.class, () -> {
			parser.parse();
		});
		assertEquals("Atteso ID, ma trovato ASSIGN a riga 2", e.getMessage());
	}

	@Test
	void testParserSoloDich() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testSoloDich.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(() -> {
			parser.parse();
		});
	}

	@Test
	void testParserEcc_9() throws Exception {
		Scanner scanner = new Scanner(PATH + "/testSoloDichPrint.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(() -> {
			parser.parse();
		});
	}

	@Test
	void testASTSoloDichiarazioni() throws Exception {
		Scanner scanner = new Scanner(PATH + "testSoloDich.txt");
		Parser parser = new Parser(scanner);
		NodeProgram ast = parser.parse();

		assertNotNull(ast);
		String expected = "NodeProgram [decSts=[NodeDecl [id=NodeId [name=x], type=INT, init=null], NodeDecl [id=NodeId [name=floati], type=FLOAT, init=null]]]";
		assertEquals(expected, ast.toString());
	}

	@Test
	void testASTDichiarazioniEPrint() throws Exception {
		Scanner scanner = new Scanner(PATH + "testSoloDichPrint.txt");
		Parser parser = new Parser(scanner);
		NodeProgram ast = parser.parse();

		assertNotNull(ast);
		String expected = "NodeProgram [decSts=[NodeDecl [id=NodeId [name=temp], type=INT, init=null], NodePrint [id=NodeId [name=abc]]]]";
		assertEquals(expected, ast.toString());
	}
}
