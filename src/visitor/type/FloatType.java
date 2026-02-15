package visitor.type;

public class FloatType extends TypeDescriptor {

	public FloatType() {
	}

	@Override
	public boolean compatibile(TypeDescriptor type) {
		return type.getClass() == this.getClass() || type.getClass() == IntType.class;
	}

	@Override
	public TypeDescriptor getTipo() {
		return new FloatType();
	}

}
