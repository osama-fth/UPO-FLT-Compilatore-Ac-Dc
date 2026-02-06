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

	/**
	 * Analizza i dati in ingresso per creare un "NodeProgram". Questo metodo avvia
	 * l'analisi del codice e verifica che non ci siano errori di scrittura.
	 */
	public NodeProgram parse() throws SyntacticException {
		return this.parsePrg();
	}

	/**
	 * Confronta il prossimo elemento (token) ricevuto con il tipo di elemento
	 * atteso. Se i due tipi corrispondono, l'elemento viene accettato e rimosso
	 * dalla coda. In caso contrario, genera un'eccezione (SyntacticException) per
	 * segnalare l'errore.
	 */
	private Token match(TokenType expected) throws SyntacticException {
		try {
			Token token = this.scanner.peekToken();

			if (expected.equals(token.getTipo())) {
				return this.scanner.nextToken();
			} else {
				throw new SyntacticException(
						"Atteso " + expected + ", ma trovato " + token.getTipo() + " a riga " + token.getRiga());
			}
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}
	}

	/**
	 * Analizza il simbolo iniziale "Prg" della grammatica. Prg -> DSs $
	 */
	private NodeProgram parsePrg() throws SyntacticException {
		Token tk = getNextToken(scanner);
		ArrayList<NodeDecSt> node = parseDSs();

		switch (tk.getTipo()) {
		case TYFLOAT, TYINT, ID, PRINT, EOF -> { // Prg -> DSs $
			match(TokenType.EOF);
			return new NodeProgram(node);
		}
		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "DSs" della grammatica. DSs -> Dcl DSs | Stm DSs | ϵ
	 */
	private ArrayList<NodeDecSt> parseDSs() throws SyntacticException {
		Token tk;
		ArrayList<NodeDecSt> nodeDecSts = new ArrayList<>();
		NodeDecSt node;

		try {
			tk = scanner.peekToken();
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}

		switch (tk.getTipo()) {
		case TYFLOAT, TYINT -> { // DSs -> Dcl DSs
			node = parseDcl();
		}

		case ID, PRINT -> { // DSs -> Stm DSs
			node = parseStm();
		}

		case EOF -> { // DSs -> ϵ
			return nodeDecSts;
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}

		nodeDecSts.add(node);
		nodeDecSts.addAll(parseDSs());
		return nodeDecSts;
	}

	/**
	 * Analizza il simbolo non-terminale "Dcl" della grammatica. Regola: Dcl -> Ty ID DclP
	 */
	private NodeDecl parseDcl() throws SyntacticException {
		Token tk = getNextToken(scanner);

		if (tk.getTipo() == TokenType.TYFLOAT || tk.getTipo() == TokenType.TYINT) { // Dcl -> Ty ID DclP
			LangType type = parseTy();
			NodeId nodeId = new NodeId(match(TokenType.ID).getValore());
			NodeExpr init = parseDclP();
			return new NodeDecl(nodeId, type, init);
		} else {
			throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale DclP della grammatica. DclP -> ; | = Exp ;
	 */
	private NodeExpr parseDclP() throws SyntacticException {
		Token tk = getNextToken(scanner);

		switch (tk.getTipo()) { // DclP -> ;
		case SEMI -> {
			match(TokenType.SEMI);
			return null;
		}

		case ASSIGN -> { // DclP -> = Exp ;
			match(TokenType.ASSIGN);
			NodeExpr init = parseExp();
			match(TokenType.SEMI);
			return init;
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "Stm" (Istruzione) della grammatica. Stm -> id Op Exp ; | print id ;
	 */
	private NodeStm parseStm() throws SyntacticException {
		Token tk = getNextToken(scanner);

		switch (tk.getTipo()) {
		case ID -> { // Stm -> id Op Exp ;
			NodeId nodeId = new NodeId(match(TokenType.ID).getValore());
			Token tkOp = parseOp();
			NodeExpr nodeExpr = parseExp();
			match(TokenType.SEMI);

			if (tkOp.getTipo() == TokenType.ASSIGN) {
				return new NodeAssign(nodeId, nodeExpr);
			} else if (tkOp.getTipo() == TokenType.OP_ASSIGN) {
				String opSymbol = tkOp.getValore();
				LangOperation langOper;

				switch (opSymbol) {
				case "+=" -> {
					langOper = LangOperation.PLUS;
				}

				case "-=" -> {
					langOper = LangOperation.MINUS;
				}

				case "*=" -> {
					langOper = LangOperation.TIMES;
				}

				case "/=" -> {
					langOper = LangOperation.DIVIDE;
				}

				default -> throw new SyntacticException(
						"Operatore assegnamento non valido: " + opSymbol + " a riga " + tkOp.getRiga());
				}

				NodeExpr newRight = new NodeBinOp(langOper, new NodeDeref(nodeId), nodeExpr);
				return new NodeAssign(nodeId, newRight);

			} else {
				throw new SyntacticException("Token non atteso " + tkOp.getTipo() + " a riga " + tkOp.getRiga());
			}
		}

		case PRINT -> { // Stm print id ;
			match(TokenType.PRINT);
			NodeId nodeId = new NodeId(match(TokenType.ID).getValore());
			match(TokenType.SEMI);
			return new NodePrint(nodeId);
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "Exp" (Espressione) della grammatica. Exp -> Tr ExpP
	 */
	private NodeExpr parseExp() throws SyntacticException {
		Token tk = getNextToken(scanner);

		TokenType type = tk.getTipo();

		if (type == TokenType.ID || type == TokenType.FLOAT || type == TokenType.INT) {
			NodeExpr term = parseTr();
			return parseExpP(term);

		} else {
			throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "ExpP" (il resto dell'espressione) della grammatica. ExpP -> + Tr ExpP | - Tr ExpP | ϵ
	 */
	private NodeExpr parseExpP(NodeExpr left) throws SyntacticException {
		Token tk = getNextToken(scanner);
		LangOperation op;

		switch (tk.getTipo()) {

		case PLUS -> { // ExpP -> + Tr ExpP
			match(TokenType.PLUS);
			op = LangOperation.PLUS;
		}

		case MINUS -> { // ExpP -> - Tr ExpP
			match(TokenType.MINUS);
			op = LangOperation.MINUS;
		}

		case SEMI -> { // ExpP -> ;
			return left;
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}

		return parseExpP(new NodeBinOp(op, left, parseTr()));
	}

	/**
	 * Analizza il simbolo non-terminale "Tr" (Termine) della grammatica. Tr -> Val TrP
	 */
	private NodeExpr parseTr() throws SyntacticException {
		Token tk;

		try {
			tk = scanner.peekToken();
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}

		TokenType type = tk.getTipo();

		if (type == TokenType.ID || type == TokenType.FLOAT || type == TokenType.INT) { // Tr -> Val TrP
			NodeExpr nodeExpr = parseVal();
			return parseTrP(nodeExpr);

		} else {
			throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "TrP" (il resto del termine) della grammatica. 
	 * Regola: TrP -> * Val TrP | / Val TrP | ϵ 
	 */
	private NodeExpr parseTrP(NodeExpr left) throws SyntacticException {
		Token tk = getNextToken(scanner);
		LangOperation op;

		switch (tk.getTipo()) {

		case TIMES -> { // TrP -> * Val TrP
			match(TokenType.TIMES);
			op = LangOperation.TIMES;
		}

		case DIVIDE -> { // TrP -> / Val TrP
			match(TokenType.DIVIDE);
			op = LangOperation.DIVIDE;
		}

		case PLUS, MINUS, SEMI -> { // TrP -> ϵ
			return left;
		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}

		return parseTrP(new NodeBinOp(op, left, parseVal()));

	}

	/**
	 * Analizza il simbolo non-terminale "Ty" (Tipo) della grammatica. Regola: Ty -> float | int 
	 */
	private LangType parseTy() throws SyntacticException {
		Token tk = getNextToken(scanner);

		switch (tk.getTipo()) {

		case TYFLOAT -> { // Ty -> float
			match(TokenType.TYFLOAT);
			return LangType.FLOAT;
		}

		case TYINT -> { // Ty -> int
			match(TokenType.TYINT);
			return LangType.INT;

		}

		default -> throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizza il simbolo non-terminale "Val" (Valore) della grammatica. Regola: Val -> INT | FLOAT | ID
	 */
	private NodeExpr parseVal() throws SyntacticException {
		Token tk;
		try {
			tk = scanner.peekToken();
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}

		TokenType type = tk.getTipo();

		switch (type) {

		case INT, FLOAT -> {
			Token matched = match(type);
			LangType langType = (type == TokenType.INT) ? LangType.INT : LangType.FLOAT;
			return new NodeConst(matched.getValore(), langType);
		}

		case ID -> {
			Token matched = match(TokenType.ID);
			return new NodeDeref(new NodeId(matched.getValore()));
		}

		default -> throw new SyntacticException("Token non atteso " + type + " a riga " + tk.getRiga());
		}
	}

	/**
	 * Analizzail simbolo non-terminale Op della grammatica. Op -> = | opAss
	 */
	private Token parseOp() throws SyntacticException {
		Token tk;
		try {
			tk = scanner.peekToken();
		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}

		TokenType type = tk.getTipo();

		if (type == TokenType.ASSIGN || type == TokenType.OP_ASSIGN) {
			return match(type);

		} else {
			throw new SyntacticException("Token non atteso " + tk.getTipo() + " a riga " + tk.getRiga());
		}
	}

	private Token getNextToken(Scanner scanner) throws SyntacticException {
		try {
			return scanner.peekToken();

		} catch (LexicalException e) {
			throw new SyntacticException(e.getMessage());
		}
	}
}
