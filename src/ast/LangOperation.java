package ast;

public enum LangOperation {
	PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), DIV_FLOAT("5k / 0k"), OP_ASSIGN("=");

	private String operator;

	LangOperation(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return this.operator;
	}
}
