package scanner;

@SuppressWarnings("serial")
public class LexicalException extends Exception {

	// Costruttore per carattere illegale generico
	public LexicalException(int riga, char carattere) {
		super("Errore lessicale a riga " + riga + ": carattere illegale '" + carattere + "'");
	}

	// Costruttore per errori specifici con stringa e motivo
	public LexicalException(int riga, String stringa, String motivo) {
		super("Errore lessicale a riga " + riga + ": " + motivo + " - '" + stringa + "'");
	}

	// Costruttore per errori generici con solo messaggio
	public LexicalException(int riga, String messaggio) {
		super("Errore lessicale a riga " + riga + ": " + messaggio);
	}
}
