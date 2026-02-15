package ast;

public enum LangOperation {
	PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), OP_ASSIGN("="), DIV_FLOAT("5k / 0k");

	private String operator;

	LangOperation(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return this.operator;
	}
}
