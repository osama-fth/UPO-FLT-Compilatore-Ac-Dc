package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import parser.Parser;
import scanner.Scanner;
import ast.NodeProgram;
import symbolTable.SymbolTable;
import visitor.CodeGeneratorVisitor;
import visitor.TypeCheckinVisitor;
import visitor.Registri;

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
		assertFalse(codeGen.getCodiceDc().isEmpty());
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
		assertFalse(codeGen.getCodiceDc().isEmpty());
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
		assertFalse(codeGen.getCodiceDc().isEmpty());
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
	}
}
