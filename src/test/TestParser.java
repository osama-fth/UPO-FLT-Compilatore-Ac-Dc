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
	private Scanner scanner;
	private Parser parser;

	private void inizializzaParser(String fileName) throws Exception {
		scanner = new Scanner(PATH + fileName);
		parser = new Parser(scanner);
	}

	@Test
	void testParserCorretto1() throws Exception {
		inizializzaParser("testParserCorretto1.txt");
		assertDoesNotThrow(() -> parser.parse());
	}

	@Test
	void testParserCorretto2() throws Exception {
		inizializzaParser("testParserCorretto2.txt");
		assertDoesNotThrow(() -> parser.parse());
	}

	@Test
	void testParserEcc_0() throws Exception {
		inizializzaParser("testParserEcc_0.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Token non atteso SEMI a riga 1, atteso '=' o operatore di assegnamento", e.getMessage());
	}

	@Test
	void testParserEcc_1() throws Exception {
		inizializzaParser("testParserEcc_1.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Token non atteso TIMES a riga 2, atteso valore (INT, FLOAT o ID)", e.getMessage());
	}

	@Test
	void testParserEcc_2() throws Exception {
		inizializzaParser("testParserEcc_2.txt");
		SyntacticException exc = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Token non atteso INT a riga 3", exc.getMessage());
	}

	@Test
	void testParserEcc_3() throws Exception {
		inizializzaParser("testParserEcc_3.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Token non atteso PLUS a riga 2, atteso '=' o operatore di assegnamento", e.getMessage());
	}

	@Test
	void testParserEcc_4() throws Exception {
		inizializzaParser("testParserEcc_4.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Atteso ID, ma trovato INT a riga 2", e.getMessage());
	}

	@Test
	void testParserEcc_5() throws Exception {
		inizializzaParser("testParserEcc_5.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Atteso ID, ma trovato INT a riga 3", e.getMessage());
	}

	@Test
	void testParserEcc_6() throws Exception {
		inizializzaParser("testParserEcc_6.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Atteso ID, ma trovato TYFLOAT a riga 3", e.getMessage());
	}

	@Test
	void testParserEcc_7() throws Exception {
		inizializzaParser("testParserEcc_7.txt");
		SyntacticException e = assertThrows(SyntacticException.class, () -> parser.parse());
		assertEquals("Atteso ID, ma trovato ASSIGN a riga 2", e.getMessage());
	}

	@Test
	void testParserSoloDich() throws Exception {
		inizializzaParser("testSoloDich.txt");
		assertDoesNotThrow(() -> parser.parse());
	}

	@Test
	void testParserEcc_9() throws Exception {
		inizializzaParser("testSoloDichPrint.txt");
		assertDoesNotThrow(() -> parser.parse());
	}

	@Test
	void testASTSoloDichiarazioni() throws Exception {
		inizializzaParser("testSoloDich.txt");
		NodeProgram ast = parser.parse();

		assertNotNull(ast);
		String astAtteso = "NodeProgram [decSts=[NodeDecl [id=NodeId [name=x], type=INT, init=null], NodeDecl [id=NodeId [name=floati], type=FLOAT, init=null]]]";
		assertEquals(astAtteso, ast.toString());
	}

	@Test
	void testASTDichiarazioniEPrint() throws Exception {
		inizializzaParser("testSoloDichPrint.txt");
		NodeProgram ast = parser.parse();

		assertNotNull(ast);
		String astAtteso = "NodeProgram [decSts=[NodeDecl [id=NodeId [name=temp], type=INT, init=null], NodePrint [id=NodeId [name=abc]]]]";
		assertEquals(astAtteso, ast.toString());
	}

	@Test
	void testASTGenerale() throws Exception {
		inizializzaParser("testParserCorretto1.txt");
		NodeProgram ast = parser.parse();

		assertNotNull(ast);
		String astAtteso = "NodeProgram [decSts=[NodePrint [id=NodeId [name=stampa]], NodeDecl [id=NodeId [name=numberfloat], type=FLOAT, init=NodeBinOp [op=+, left=NodeConst [valore=3.5, tipo=FLOAT], right=NodeConst [valore=8, tipo=INT]]], NodeDecl [id=NodeId [name=floati], type=INT, init=null], NodeAssign [id=NodeId [name=a], NodeExpr=NodeBinOp [op=+, left=NodeDeref [id=NodeId [name=a]], right=NodeBinOp [op=+, left=NodeConst [valore=5, tipo=INT], right=NodeConst [valore=3, tipo=INT]]]], NodeAssign [id=NodeId [name=b], NodeExpr=NodeBinOp [op=+, left=NodeDeref [id=NodeId [name=a]], right=NodeConst [valore=5, tipo=INT]]]]]";
		assertEquals(astAtteso, ast.toString());
	}
}
