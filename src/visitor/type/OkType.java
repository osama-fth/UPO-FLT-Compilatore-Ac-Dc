package visitor.type;

public class OkType extends TypeDescriptor {

	public OkType() {
	}

	@Override
	public boolean compatibile(TypeDescriptor type) {
		return false;
	}

	@Override
	public TypeDescriptor getTipo() {
		return new OkType();
	}

}
