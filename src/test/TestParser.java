package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import parser.Parser;
import parser.SyntacticException;
import scanner.Scanner;

class TestParser {

	private static final String PATH = "src/test/data/testParser/";

	@Test
	void testParserCorretto1() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserCorretto1.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(parser::parse);
	}

	@Test
	void testParserCorretto2() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserCorretto2.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(parser::parse);
	}

	@Test
	void testParserEcc_0() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_0.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_1() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_1.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_2() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_2.txt");
		Parser parser = new Parser(scanner);
		SyntacticException exc = assertThrows(SyntacticException.class, parser::parse);
		assertEquals("Token non atteso INT a riga 3", exc.getMessage());
	}

	@Test
	void testParserEcc_3() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_3.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_4() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_4.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_5() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_5.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_6() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_6.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserEcc_7() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testParserEcc_7.txt");
		Parser parser = new Parser(scanner);
		assertThrows(SyntacticException.class, parser::parse);
	}

	@Test
	void testParserSoloDich() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testSoloDich.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(parser::parse, "Unexpected Exception");
	}

	@Test
	void testParserEcc_9() throws IOException {
		Scanner scanner = new Scanner(PATH + "/testSoloDichPrint.txt");
		Parser parser = new Parser(scanner);
		assertDoesNotThrow(parser::parse, "Unexpected Exception");
	}
}
