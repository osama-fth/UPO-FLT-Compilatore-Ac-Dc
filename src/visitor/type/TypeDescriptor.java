package visitor.type;

public abstract class TypeDescriptor {
	abstract public boolean compatibile(TypeDescriptor tipo);
	abstract public TypeDescriptor getType();
}
