package visitor;

public class FloatType extends TypeDescriptor {

	public FloatType() {
	}

	/**
	 * Two types are compatible if they are the same, or if the other type is an
	 * IntType.
	 *
	 * @param type the type to check for compatibility
	 * @return true if the given type is compatible with this type, false otherwise
	 */
	@Override
	public boolean compatibile(TypeDescriptor type) {
		return type.getClass() == this.getClass() || type.getClass() == IntType.class;
	}

	@Override
	public TypeDescriptor getType() {
		return new FloatType();
	}

}
