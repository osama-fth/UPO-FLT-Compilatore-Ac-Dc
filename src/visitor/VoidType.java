package visitor;

public class VoidType extends TypeDescriptor {

	public VoidType() {
	}

	@Override
	public boolean compatibile(TypeDescriptor type) {
		return false;
	}

	@Override
	public TypeDescriptor getType() {
		return new VoidType();
	}

}
