package visitor.type;

public class IntType extends TypeDescriptor {

	public IntType() {
	}

	@Override
	public boolean compatibile(TypeDescriptor type) {
		return type.getClass() == this.getClass();
	}

	@Override
	public TypeDescriptor getType() {
		return new IntType();
	}

}
