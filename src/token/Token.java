package token;

public class Token {

	private int riga;
	private TokenType tipo;
	private String val;

	public Token(int riga, TokenType tipo, String valore) {
		this.riga = riga;
		this.tipo = tipo;
		this.val = valore;
	}

	public Token(int riga, TokenType tipo) {
		this.riga = riga;
		this.tipo = tipo;
	}

	public int getRiga() {
		return riga;
	}

	public TokenType getTipo() {
		return tipo;
	}

	public String getVal() {
		return val;
	}

	@Override
	public String toString() {
		if (getTipo() == TokenType.INT || getTipo() == TokenType.FLOAT || getTipo() == TokenType.ID
				|| getTipo() == TokenType.OP_ASSIGN) {
			return "<" + getTipo().toString() + ", r:" + getRiga() + ", val:" + getVal() + ">";
		}

		return "<" + getTipo().toString() + ", r:" + getRiga() + ">";
	}
}
