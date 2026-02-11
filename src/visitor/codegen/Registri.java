package visitor.codegen;

public class Registri {
	private static char registroCorrente = 'a';

	public static char newRegister() {
		if (registroCorrente > 'z') {
			return ' ';
		}
		return registroCorrente++;
	}

	public static void reset() {
		registroCorrente = 'a';
	}
}
