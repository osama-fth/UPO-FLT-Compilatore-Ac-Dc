package visitor.type;

public class ErrorType extends TypeDescriptor {
	private String msg;

	public ErrorType(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	@Override
	public boolean compatibile(TypeDescriptor type) {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == this.getClass() && ((ErrorType) obj).msg.equals(this.msg);
	}

	@Override
	public TypeDescriptor getType() {
		return new ErrorType(this.msg);
	}
}
