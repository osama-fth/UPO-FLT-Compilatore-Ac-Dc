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
import visitor.TypeCheckingVisitor;
import visitor.codegen.Registri;

class TestCodeGenerator {

	private static final String PATH = "src/test/data/testCodeGenerator/";
	Scanner scanner;
	Parser parser;
	NodeProgram program;
	TypeCheckingVisitor typeChecker;

	@BeforeEach
	void setUp() {
		SymbolTable.pulisciTabella();
		Registri.reset();
	}

	void inizializzaCodeGenerator(String fileName) throws Exception {
		scanner = new Scanner(PATH + fileName);
		parser = new Parser(scanner);
		program = parser.parse();
		typeChecker = new TypeCheckingVisitor();
		typeChecker.visit(program);
	}

	@Test
	void testAssign() throws Exception {
		inizializzaCodeGenerator("1_assign.txt");
		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("1 6 / sa la p P", codeGen.getCodiceDc().trim());
	}

	@Test
	void testDivisioni() throws Exception {
		inizializzaCodeGenerator("2_divsioni.txt");
		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("0 sa la 1 + sa 6 sb 1.0 6 5 k / 0 k la lb / + sc la p P lb p P lc p P",
				codeGen.getCodiceDc().trim());
	}

	@Test
	void testGenerale() throws Exception {
		inizializzaCodeGenerator("3_generale.txt");
		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertTrue(codeGen.getLog().isEmpty());
		assertEquals("5 3 + sa la 0.5 + sb la p P lb 4 5 k / 0 k sb lb p P lb 1 - sc lc lb * sc lc p P",
				codeGen.getCodiceDc().trim());
	}

	@Test
	void testRegistriFiniti() throws Exception {
		inizializzaCodeGenerator("4_registriFiniti.txt");
		CodeGeneratorVisitor codeGen = new CodeGeneratorVisitor();
		codeGen.visit(program);

		assertFalse(codeGen.getLog().isEmpty());
		assertTrue(codeGen.getLog().contains("registri esauriti"));
		assertEquals("Errore: registri esauriti per la variabile uno", codeGen.getLog().trim());
	}
}
