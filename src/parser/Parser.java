package parser;

import java.util.ArrayList;

import ast.LangOperation;
import ast.LangType;
import ast.NodeAssign;
import ast.NodeBinOp;
import ast.NodeConst;
import ast.NodeDecSt;
import ast.NodeDecl;
import ast.NodeDeref;
import ast.NodeExpr;
import ast.NodeId;
import ast.NodePrint;
import ast.NodeProgram;
import ast.NodeStm;
import scanner.LexicalException;
import scanner.Scanner;
import token.Token;
import token.TokenType;

public class Parser {

	private Scanner scanner;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	public NodeProgram parse() throws SyntacticException {
		return parsePrg();
	}

	private Token match(TokenType type) throws SyntacticException {
		try {
			Token token = scanner.peekToken();

			if (type.equals(token.getTipo())) {
				return scanner.nextToken();
			} else {
				throw new SyntacticException(
						"Atteso " + type + ", ma trovato " + token.getTipo() + " a riga " + token.getRiga());
			}
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}
	}

	/**
	 * Restituisce il prossimo token senza consumarlo.
	 */
	private Token getNextToken() throws SyntacticException {
		try {
			return scanner.peekToken();
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}
	}

	/**
	 * Prg -> DSs $
	 */
	private NodeProgram parsePrg() throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case TYFLOAT, TYINT, ID, PRINT, EOF -> {
			ArrayList<NodeDecSt> decSts = parseDSs();
			match(TokenType.EOF);
			return new NodeProgram(decSts);
		}
		default -> throw new SyntacticException(
				"Token " + tk.getTipo() + " a riga " + tk.getRiga() + " non è inizio di programma valido");
		}
	}

	/**
	 * DSs -> Dcl DSs | Stm DSs | ϵ
	 */
	private ArrayList<NodeDecSt> parseDSs() throws SyntacticException {
		Token tk = getNextToken();
		ArrayList<NodeDecSt> nodeDecSts = new ArrayList<>();

		switch (tk.getTipo()) {
		case TYFLOAT, TYINT -> { // DSs -> Dcl DSs
			nodeDecSts.add(parseDcl());
			nodeDecSts.addAll(parseDSs());
		}

		case ID, PRINT -> { // DSs -> Stm DSs
			nodeDecSts.add(parseStm());
			nodeDecSts.addAll(parseDSs());
		}

		case EOF -> { // DSs -> ϵ
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}

		return nodeDecSts;
	}

	/**
	 * Dcl -> Ty ID DclP
	 */
	private NodeDecl parseDcl() throws SyntacticException {
		LangType type = parseTy();
		NodeId nodeId = new NodeId(match(TokenType.ID).getVal());
		NodeExpr init = parseDclP();
		return new NodeDecl(nodeId, type, init);
	}

	/**
	 * DclP -> ; | = Exp ;
	 */
	private NodeExpr parseDclP() throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case SEMI -> {
			match(TokenType.SEMI);
			return null;
		}

		case ASSIGN -> {
			match(TokenType.ASSIGN);
			NodeExpr init = parseExp();
			match(TokenType.SEMI);
			return init;
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso ';' o '='");
		}
	}

	/**
	 * Stm -> id Op Exp ; | print id ;
	 */
	private NodeStm parseStm() throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case ID -> {
			NodeId nodeId = new NodeId(match(TokenType.ID).getVal());
			Token tkOp = parseOp();
			NodeExpr nodeExpr = parseExp();
			match(TokenType.SEMI);

			if (tkOp.getTipo() == TokenType.ASSIGN) {
				return new NodeAssign(nodeId, nodeExpr);
			} else {
				LangOperation langOper = switch (tkOp.getVal()) {
				case "+=" -> LangOperation.PLUS;
				case "-=" -> LangOperation.MINUS;
				case "*=" -> LangOperation.TIMES;
				case "/=" -> LangOperation.DIVIDE;
				default -> throw new SyntacticException(
						"Operatore assegnamento non valido: " + tkOp.getVal() + " a riga " + tkOp.getRiga());
				};

				NodeExpr newRight = new NodeBinOp(langOper, new NodeDeref(nodeId), nodeExpr);
				return new NodeAssign(nodeId, newRight);
			}
		}

		case PRINT -> {
			match(TokenType.PRINT);
			NodeId nodeId = new NodeId(match(TokenType.ID).getVal());
			match(TokenType.SEMI);
			return new NodePrint(nodeId);
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso ID o 'print'");
		}
	}

	/**
	 * Exp -> Tr ExpP
	 */
	private NodeExpr parseExp() throws SyntacticException {
		NodeExpr term = parseTr();
		return parseExpP(term);
	}

	/**
	 * ExpP -> + Tr ExpP | - Tr ExpP | ϵ
	 */
	private NodeExpr parseExpP(NodeExpr left) throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case PLUS -> {
			match(TokenType.PLUS);
			return parseExpP(new NodeBinOp(LangOperation.PLUS, left, parseTr()));
		}

		case MINUS -> {
			match(TokenType.MINUS);
			return parseExpP(new NodeBinOp(LangOperation.MINUS, left, parseTr()));
		}

		case SEMI -> {
			return left;
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso '+', '-' o ';'");
		}
	}

	/**
	 * Tr -> Val TrP
	 */
	private NodeExpr parseTr() throws SyntacticException {
		NodeExpr nodeExpr = parseVal();
		return parseTrP(nodeExpr);
	}

	/**
	 * TrP -> * Val TrP | / Val TrP | ϵ
	 */
	private NodeExpr parseTrP(NodeExpr left) throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case TIMES -> {
			match(TokenType.TIMES);
			return parseTrP(new NodeBinOp(LangOperation.TIMES, left, parseVal()));
		}

		case DIVIDE -> {
			match(TokenType.DIVIDE);
			return parseTrP(new NodeBinOp(LangOperation.DIVIDE, left, parseVal()));
		}

		case PLUS, MINUS, SEMI -> {
			return left;
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso '*', '/', '+', '-' o ';'");
		}
	}

	/**
	 * Ty -> float | int
	 */
	private LangType parseTy() throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case TYFLOAT -> {
			match(TokenType.TYFLOAT);
			return LangType.FLOAT;
		}

		case TYINT -> {
			match(TokenType.TYINT);
			return LangType.INT;
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso 'float' o 'int'");
		}
	}

	/**
	 * Val -> INT | FLOAT | ID
	 */
	private NodeExpr parseVal() throws SyntacticException {
		Token tk = getNextToken();

		switch (tk.getTipo()) {
		case INT -> {
			Token matched = match(TokenType.INT);
			return new NodeConst(matched.getVal(), LangType.INT);
		}

		case FLOAT -> {
			Token matched = match(TokenType.FLOAT);
			return new NodeConst(matched.getVal(), LangType.FLOAT);
		}

		case ID -> {
			Token matched = match(TokenType.ID);
			return new NodeDeref(new NodeId(matched.getVal()));
		}

		default -> throw new SyntacticException(
				"Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga() + ", atteso valore (INT, FLOAT o ID)");
		}
	}

	/**
	 * Op -> = | opAss
	 */
	private Token parseOp() throws SyntacticException {
		Token tk = getNextToken();

		if (tk.getTipo() == TokenType.ASSIGN || tk.getTipo() == TokenType.OP_ASSIGN) {
			return match(tk.getTipo());
		} else {
			throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga()
					+ ", atteso '=' o operatore di assegnamento");
		}
	}
}
