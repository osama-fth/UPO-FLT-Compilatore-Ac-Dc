package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.HashSet;

import token.Token;
import token.TokenType;

public class Scanner {
	final char EOF = (char) -1;
	private int riga;
	private PushbackReader buffer;
	private Token nextTk = null;

	public HashSet<Character> skpChars;
	public HashSet<Character> letters;
	public HashSet<Character> digits;
	public HashMap<Character, TokenType> operTkType;
	public HashMap<Character, TokenType> delimTkType;
	public HashMap<String, TokenType> keyWordsTkType;

	public Scanner(String fileName) throws FileNotFoundException {
		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		init();
	}

	private void init() {
		skpChars = new HashSet<>();
		skpChars.add(' ');
		skpChars.add('\n');
		skpChars.add('\t');
		skpChars.add('\r');
		skpChars.add(EOF);

		letters = new HashSet<>();
		for (char c = 'a'; c <= 'z'; c++) {
			letters.add(c);
		}

		digits = new HashSet<>();
		for (char d = '0'; d <= '9'; d++) {
			digits.add(d);
		}

		operTkType = new HashMap<>();
		operTkType.put('+', TokenType.PLUS);
		operTkType.put('-', TokenType.MINUS);
		operTkType.put('*', TokenType.TIMES);
		operTkType.put('/', TokenType.DIVIDE);

		delimTkType = new HashMap<>();
		delimTkType.put('=', TokenType.ASSIGN);
		delimTkType.put(';', TokenType.SEMI);

		keyWordsTkType = new HashMap<>();
		keyWordsTkType.put("print", TokenType.PRINT);
		keyWordsTkType.put("int", TokenType.TYINT);
		keyWordsTkType.put("float", TokenType.TYFLOAT);
	}

	public Token nextToken() throws LexicalException {

		if (nextTk != null) {
			Token temp = nextTk;
			nextTk = null;
			return temp;
		}

		try {
			char nextChar = peekChar();
			// salta whitespace e newline (incrementa riga su '\n')
			while (skpChars.contains(nextChar) && nextChar != EOF) {
				if (nextChar == '\n') {
					riga++;
				}
				readChar();
				nextChar = peekChar();
			}
			if (nextChar == EOF) {
				return new Token(riga, TokenType.EOF);
			}
			if (letters.contains(nextChar)) {
				return scanId();
			}
			if (operTkType.containsKey(nextChar)) {
				return scanOperator();
			}
			if (delimTkType.containsKey(nextChar)) {
				char c = readChar();
				return new Token(riga, delimTkType.get(c));
			}
			if (digits.contains(nextChar)) {
				return scanNumber();
			}
			throw new LexicalException(riga, nextChar);
		} catch (IOException e) {
			throw new LexicalException(riga, "errore di lettura del file");
		}
	}

	// ID: deve iniziare con lettera, poi può contenere lettere o cifre
	private Token scanId() throws IOException {
		StringBuilder sb = new StringBuilder();
		char c = peekChar();
		// prima lettera obbligatoria (siamo qui perché letters.contains(c) era true)
		sb.append(readChar());
		c = peekChar();
		// dopo la prima lettera, accetta lettere o cifre
		while (letters.contains(c) || digits.contains(c)) {
			sb.append(readChar());
			c = peekChar();
		}
		String sr = sb.toString();
		if (keyWordsTkType.containsKey(sr)) {
			return new Token(riga, keyWordsTkType.get(sr));
		}
		return new Token(riga, TokenType.ID, sr);
	}

	private Token scanOperator() throws IOException {
		char op = readChar();
		char nextChar = peekChar();
		if (nextChar == '=') {
			readChar(); // consuma '='
			String opAssign = "" + op + "=";
			return new Token(riga, TokenType.OP_ASSIGN, opAssign);
		}
		return new Token(riga, operTkType.get(op));
	}

	private Token scanNumber() throws IOException, LexicalException {
		StringBuilder sb = new StringBuilder();
		char nextChar = peekChar();

		if (nextChar == '0') {
			sb.append(readChar());
			nextChar = peekChar();

			// leading zero seguito da cifra è errore
			if (digits.contains(nextChar)) {
				// consuma tutte le cifre
				while (digits.contains(nextChar)) {
					sb.append(readChar());
					nextChar = peekChar();
				}
				// se c'è un punto, consuma anche la parte decimale
				if (nextChar == '.') {
					sb.append(readChar()); // consuma '.'
					nextChar = peekChar();
					// consuma le cifre decimali
					while (digits.contains(nextChar)) {
						sb.append(readChar());
						nextChar = peekChar();
					}
				}
				// controlla se dopo le cifre c'è una lettera
				if (letters.contains(nextChar)) {
					while (letters.contains(nextChar) || digits.contains(nextChar)) {
						sb.append(readChar());
						nextChar = peekChar();
					}
					throw new LexicalException(riga, sb.toString(), "identificatore con numero iniziale");
				}
				throw new LexicalException(riga, sb.toString(), "numero con zero iniziale non permesso");
			} else if (nextChar == '.') {
				sb.append(readChar()); // '.'
				return scanFloat(sb);
			} else if (letters.contains(nextChar)) {
				while (letters.contains(nextChar) || digits.contains(nextChar)) {
					sb.append(readChar());
					nextChar = peekChar();
				}
				throw new LexicalException(riga, sb.toString(), "identificatore con numero iniziale");
			}

			return new Token(riga, TokenType.INT, sb.toString());
		}

		// parte intera
		while (digits.contains(nextChar)) {
			sb.append(readChar());
			nextChar = peekChar();
		}
		if (nextChar == '.') {
			sb.append(readChar());
			return scanFloat(sb);
		} else if (letters.contains(nextChar)) {
			while (letters.contains(nextChar) || digits.contains(nextChar)) {
				sb.append(readChar());
				nextChar = peekChar();
			}
			throw new LexicalException(riga, sb.toString(), "identificatore con numero iniziale");
		}
		return new Token(riga, TokenType.INT, sb.toString());
	}

	private Token scanFloat(StringBuilder sb) throws IOException, LexicalException {
		char nextChar = peekChar();
		int decimali = 0;
		String pref = sb.toString(); // include parte intera e '.'

		while (digits.contains(nextChar)) {
			sb.append(readChar());
			decimali++;
			if (decimali > 5) {
				// consuma eventuali altre cifre per messaggio più chiaro
				while (digits.contains(peekChar())) {
					sb.append(readChar());
				}
				throw new LexicalException(riga, sb.toString(), "numero float con più di 5 cifre decimali");
			}
			nextChar = peekChar();
		}

		if (decimali == 0) {
			throw new LexicalException(riga, pref, "numero float deve avere almeno una cifra dopo il punto");
		}

		if (nextChar == '.') {
			sb.append(readChar());
			while (digits.contains(peekChar()) || letters.contains(peekChar())) {
				sb.append(readChar());
			}
			throw new LexicalException(riga, sb.toString(), "numero float con più di un punto decimale");

		}

		return new Token(riga, TokenType.FLOAT, sb.toString());
	}

	public Token peekToken() throws LexicalException {

		if (nextTk == null) {
			nextTk = nextToken();
		}

		return nextTk;
	}

	private char readChar() throws IOException {
		return ((char) this.buffer.read());
	}

	private char peekChar() throws IOException {
		char c = (char) buffer.read();
		buffer.unread(c);
		return c;
	}
}