package test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ast.NodeProgram;
import parser.Parser;
import scanner.Scanner;
import symbolTable.SymbolTable;
import visitor.CodeGeneratorVisitor;
import visitor.Registri;
import visitor.TypeCheckinVisitor;

class TestCodeGenerator {

	private static final String PATH = "src/test/data/testCodeGenerator/";

	@BeforeEach
	void setUp() {
		SymbolTable.clearTable();
		Registri.reset();
	}

	@Test
	void testAssign() throws Exception {
		Scanner scanner = new Scanner(PATH + "1_assign.txt");
		Parser parser = new Parser(scanner);
		NodeProgram program = parser.parse();

		TypeCheckinVisitor typeChecker = new TypeCheckinVisitor();
		typeChecker.visit(program);

		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("1 6 / sa la p P", codeGen.getCodiceDc().trim());
	}

	@Test
	void testDivisioni() throws Exception {
		Scanner scanner = new Scanner(PATH + "2_divsioni.txt");
		Parser parser = new Parser(scanner);
		NodeProgram program = parser.parse();

		TypeCheckinVisitor typeChecker = new TypeCheckinVisitor();
		typeChecker.visit(program);

		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("0 sa la 1 + sa 6 sb 1.0 6 5 k / 0 k la lb / + sc la p P lb p P lc p P",
				codeGen.getCodiceDc().trim());
	}

	@Test
	void testGenerale() throws Exception {
		Scanner scanner = new Scanner(PATH + "3_generale.txt");
		Parser parser = new Parser(scanner);
		NodeProgram program = parser.parse();

		TypeCheckinVisitor typeChecker = new TypeCheckinVisitor();
		typeChecker.visit(program);

		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("5 3 + sa la 0.5 + sb la p P lb 4 5 k / 0 k sb lb p P lb 1 - sc lc lb * sc lc p P",
				codeGen.getCodiceDc().trim());
	}

	@Test
	void testRegistriFiniti() throws Exception {
		Scanner scanner = new Scanner(PATH + "4_registriFiniti.txt");
		Parser parser = new Parser(scanner);
		NodeProgram program = parser.parse();

		TypeCheckinVisitor typeChecker = new TypeCheckinVisitor();
		typeChecker.visit(program);

		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertFalse(codeGen.getLog().isEmpty());
		assertTrue(codeGen.getLog().contains("registri esauriti"));
		assertEquals("Errore: registri esauriti per la variabile uno", codeGen.getLog().trim());
	}
}
