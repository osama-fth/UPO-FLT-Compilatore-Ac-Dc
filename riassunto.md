# 📘 Analisi Completa del Compilatore AcDc

## 🎯 Panoramica del Flusso di Compilazione

Il compilatore AcDc trasforma codice sorgente in codice target attraverso 5 fasi principali:

```
Codice Sorgente → Scanner → Parser → Type Checker → Code Generator → Codice Target
```

---

## 1️⃣ SCANNER (Analisi Lessicale)

### 📦 Package: `scanner`

### Classe: Scanner.java

**Scopo:** Trasforma il file sorgente in una sequenza di token.

**Funzionamento:**
1. Legge il file carattere per carattere usando un `PushbackReader`
2. Raggruppa i caratteri in token significativi
3. Tiene traccia del numero di riga corrente
4. Implementa il metodo `peekToken()` per guardare il prossimo token senza consumarlo

**Campi principali:**
```java
private int riga;                               // Numero riga corrente
private PushbackReader buffer;                  // Buffer per lettura caratteri
private Token nextTk;                           // Token successivo (per peek)
private HashSet<Character> skpChars;           // Caratteri da ignorare (spazi, tab, newline)
private HashSet<Character> letters;            // Lettere valide
private HashSet<Character> digits;             // Cifre valide
private HashMap<Character, TokenType> operTkType;    // Operatori (+, -, *, /)
private HashMap<Character, TokenType> delimTkType;   // Delimitatori (;, =)
private HashMap<String, TokenType> keyWordsTkType;   // Parole chiave (int, float, print)
```

**Metodi principali:**

- **`nextToken()`**: Restituisce il prossimo token, scansionando il buffer
- **`peekToken()`**: Guarda il prossimo token senza consumarlo (implementato con cache)
- **`scanId()`**: Riconosce identificatori (es: `variabile`, `x`, `temp`)
- **`scanNumber()`**: Riconosce numeri interi e float
- **`scanFloat()`**: Gestisce la parte decimale dei float
- **`scanOperator()`**: Riconosce operatori e operatori di assegnamento (+=, -=, *=, /=)

**Regole lessicali:**
- **Identificatori**: iniziano con lettera, seguiti da lettere/cifre
- **Numeri interi**: cifre senza zero iniziale (tranne lo zero stesso)
- **Float**: numero.cifre (max 5 cifre decimali, almeno una cifra dopo il punto)
- **Operatori**: `+`, `-`, `*`, `/`
- **Operatori di assegnamento**: `=`, `+=`, `-=`, `*=`, `/=`
- **Delimitatori**: `;`
- **Parole chiave**: `int`, `float`, `print`

**Gestione errori:**
```java
throw new LexicalException("Errore lessicale a riga " + riga + ": " + messaggio);
```

### Classe: `LexicalException.java`

**Scopo:** Eccezione personalizzata per errori lessicali.

**Esempi di errori:**
- Carattere illegale: `^`, `#`, `@`
- Numero con zero iniziale: `0123`
- Float malformato: `12.` (manca cifra dopo il punto)
- Float con troppe cifre decimali: `12.123456` (più di 5 cifre)

---

## 2️⃣ TOKEN (Rappresentazione dei Token)

### 📦 Package: `token`

### Classe: `Token.java`

**Scopo:** Rappresenta un token riconosciuto dallo scanner.

**Campi:**
```java
private int riga;           // Riga in cui appare il token
private TokenType tipo;     // Tipo del token (INT, FLOAT, ID, PLUS, etc.)
private String val;         // Valore lessicale del token
```

**Esempio:**
```java
// Token per: int x = 5;
Token(1, TokenType.TYINT, "int")
Token(1, TokenType.ID, "x")
Token(1, TokenType.ASSIGN, "=")
Token(1, TokenType.INT, "5")
Token(1, TokenType.SEMI, ";")
```

### Enum: `TokenType.java`

**Scopo:** Definisce tutti i tipi di token possibili.

**Categorie:**
```java
// Tipi di dato
TYINT, TYFLOAT

// Valori
INT, FLOAT, ID

// Operatori aritmetici
PLUS, MINUS, TIMES, DIVIDE

// Assegnamento
ASSIGN, OP_ASSIGN  // = e +=, -=, *=, /=

// Delimitatori
SEMI  // ;

// Comandi
PRINT

// Fine file
EOF
```

---

## 3️⃣ PARSER (Analisi Sintattica)

### 📦 Package: `parser`

### Classe: Parser.java

**Scopo:** Verifica che la sequenza di token rispetti la grammatica e costruisce l'AST (Abstract Syntax Tree).

**Grammatica del linguaggio:**
```
Prg  -> DSs $
DSs  -> Dcl DSs | Stm DSs | ϵ
Dcl  -> Ty ID DclP
DclP -> ; | = Exp ;
Stm  -> ID Op Exp ; | print ID ;
Ty   -> int | float
Op   -> = | +=|-=|*=|/=
Exp  -> Tr ExpP
ExpP -> + Tr ExpP | - Tr ExpP | ϵ
Tr   -> Val TrP
TrP  -> * Val TrP | / Val TrP | ϵ
Val  -> INT | FLOAT | ID
```

**Campo:**
```java
private Scanner scanner;  // Scanner per ottenere i token
```

**Metodi principali:**

#### `parse()`
Metodo pubblico che avvia il parsing:
```java
public NodeProgram parse() throws SyntacticException {
    return parsePrg();
}
```

#### `match(TokenType type)`
Verifica che il prossimo token sia del tipo atteso:
```java
private Token match(TokenType type) throws SyntacticException {
    Token token = scanner.peekToken();
    if (type.equals(token.getTipo())) {
        return scanner.nextToken();  // Consuma il token
    } else {
        throw new SyntacticException("Atteso " + type + ", ma trovato " + token.getTipo());
    }
}
```

#### `getNextToken()`
Ottiene il prossimo token senza consumarlo:
```java
private Token getNextToken() throws SyntacticException {
    return scanner.peekToken();
}
```

#### Metodi di parsing (uno per ogni non-terminale):

**`parsePrg()`** - Programma completo
```java
// Prg -> DSs $
private NodeProgram parsePrg() {
    ArrayList<NodeDecSt> decSts = parseDSs();
    match(TokenType.EOF);
    return new NodeProgram(decSts);
}
```

**`parseDSs()`** - Dichiarazioni e statement
```java
// DSs -> Dcl DSs | Stm DSs | ϵ
private ArrayList<NodeDecSt> parseDSs() {
    Token tk = getNextToken();
    switch (tk.getTipo()) {
        case TYFLOAT, TYINT -> // Dichiarazione
        case ID, PRINT -> // Statement
        case EOF -> // Fine
    }
}
```

**`parseDcl()`** - Dichiarazione
```java
// Dcl -> Ty ID DclP
private NodeDecl parseDcl() {
    LangType type = parseTy();
    NodeId id = new NodeId(match(TokenType.ID).getVal());
    NodeExpr init = parseDclP();
    return new NodeDecl(id, type, init);
}
```

**`parseDclP()`** - Inizializzazione opzionale
```java
// DclP -> ; | = Exp ;
private NodeExpr parseDclP() {
    switch (tk.getTipo()) {
        case SEMI -> return null;  // Nessuna inizializzazione
        case ASSIGN -> {
            match(TokenType.ASSIGN);
            NodeExpr init = parseExp();
            match(TokenType.SEMI);
            return init;
        }
    }
}
```

**`parseStm()`** - Statement
```java
// Stm -> ID Op Exp ; | print ID ;
private NodeStm parseStm() {
    switch (tk.getTipo()) {
        case ID -> {
            // Assegnamento normale o con operatore
            NodeId id = new NodeId(match(TokenType.ID).getVal());
            Token op = parseOp();
            NodeExpr expr = parseExp();
            // Se op è +=, trasforma in: id = id + expr
        }
        case PRINT -> {
            match(TokenType.PRINT);
            NodeId id = new NodeId(match(TokenType.ID).getVal());
            return new NodePrint(id);
        }
    }
}
```

**`parseExp()` e `parseExpP()`** - Espressioni (+ e -)
```java
// Exp -> Tr ExpP
private NodeExpr parseExp() {
    NodeExpr term = parseTr();
    return parseExpP(term);
}

// ExpP -> + Tr ExpP | - Tr ExpP | ϵ
private NodeExpr parseExpP(NodeExpr left) {
    switch (tk.getTipo()) {
        case PLUS -> return parseExpP(new NodeBinOp(PLUS, left, parseTr()));
        case MINUS -> return parseExpP(new NodeBinOp(MINUS, left, parseTr()));
        case SEMI -> return left;  // Fine espressione
    }
}
```

**`parseTr()` e `parseTrP()`** - Termini (* e /)
```java
// Tr -> Val TrP
private NodeExpr parseTr() {
    NodeExpr val = parseVal();
    return parseTrP(val);
}

// TrP -> * Val TrP | / Val TrP | ϵ
private NodeExpr parseTrP(NodeExpr left) {
    switch (tk.getTipo()) {
        case TIMES -> return parseTrP(new NodeBinOp(TIMES, left, parseVal()));
        case DIVIDE -> return parseTrP(new NodeBinOp(DIVIDE, left, parseVal()));
        // Altri casi: ritorna left
    }
}
```

**`parseVal()`** - Valori
```java
// Val -> INT | FLOAT | ID
private NodeExpr parseVal() {
    switch (tk.getTipo()) {
        case INT -> return new NodeConst(match(TokenType.INT).getVal(), INT);
        case FLOAT -> return new NodeConst(match(TokenType.FLOAT).getVal(), FLOAT);
        case ID -> return new NodeDeref(new NodeId(match(TokenType.ID).getVal()));
    }
}
```

**`parseTy()`** - Tipi
```java
// Ty -> int | float
private LangType parseTy() {
    switch (tk.getTipo()) {
        case TYINT -> { match(TokenType.TYINT); return LangType.INT; }
        case TYFLOAT -> { match(TokenType.TYFLOAT); return LangType.FLOAT; }
    }
}
```

**`parseOp()`** - Operatori di assegnamento
```java
// Op -> = | +=|-=|*=|/=
private Token parseOp() {
    Token tk = getNextToken();
    if (tk.getTipo() == ASSIGN || tk.getTipo() == OP_ASSIGN) {
        return match(tk.getTipo());
    }
}
```

### Classe: `SyntacticException.java`

**Scopo:** Eccezione per errori sintattici.

**Costruttori:**
```java
public SyntacticException(int riga, String atteso, TokenType ottenuto)
public SyntacticException(String msg)
```

**Esempi di errori:**
- Token mancante: "Atteso SEMI, ma trovato EOF a riga 3"
- Token inaspettato: "Token non atteso TIMES a riga 2"

---

## 4️⃣ AST (Abstract Syntax Tree)

### 📦 Package: `ast`

L'AST rappresenta la struttura del programma in forma ad albero. Ogni nodo implementa l'interfaccia `Node` con il metodo `accept(IVisitor)` per il pattern Visitor.

### Interfaccia: `Node.java`

```java
public interface Node {
    void accept(IVisitor visitor);
}
```

### Gerarchia dei nodi:

```
Node (interface)
├── NodeProgram
├── NodeDecSt (interface)
│   ├── NodeDecl
│   └── NodeStm (interface)
│       ├── NodeAssign
│       └── NodePrint
└── NodeExpr (abstract)
    ├── NodeBinOp
    ├── NodeConst
    └── NodeDeref
        └── NodeId (usato anche standalone)
```

### Nodi principali:

#### `NodeProgram.java`
**Scopo:** Radice dell'AST, contiene tutte le dichiarazioni e statement.

```java
public class NodeProgram implements Node {
    private ArrayList<NodeDecSt> decSts;  // Lista di dichiarazioni/statement
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

**Esempio AST:**
```
NodeProgram
├── NodeDecl (int x)
├── NodeAssign (x = 5)
└── NodePrint (x)
```

#### `NodeDecl.java` (implements `NodeDecSt`)
**Scopo:** Rappresenta una dichiarazione di variabile.

```java
public class NodeDecl implements NodeDecSt {
    private NodeId id;        // Nome variabile
    private LangType type;    // Tipo (INT o FLOAT)
    private NodeExpr init;    // Inizializzazione opzionale
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

**Esempi:**
```java
// int x;
NodeDecl(NodeId("x"), INT, null)

// float y = 3.5;
NodeDecl(NodeId("y"), FLOAT, NodeConst("3.5", FLOAT))
```

#### `NodeAssign.java` (implements `NodeStm`)
**Scopo:** Rappresenta un assegnamento.

```java
public class NodeAssign implements NodeStm {
    private NodeId id;          // Variabile di destinazione
    private NodeExpr nodeExpr;  // Espressione da assegnare
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

**Esempi:**
```java
// x = 5;
NodeAssign(NodeId("x"), NodeConst("5", INT))

// x += 3;  (trasformato in: x = x + 3)
NodeAssign(NodeId("x"), NodeBinOp(PLUS, NodeDeref(NodeId("x")), NodeConst("3", INT)))
```

#### `NodePrint.java` (implements `NodeStm`)
**Scopo:** Rappresenta un comando print.

```java
public class NodePrint implements NodeStm {
    private NodeId id;  // Variabile da stampare
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

#### `NodeBinOp.java` (extends `NodeExpr`)
**Scopo:** Rappresenta un'operazione binaria.

```java
public class NodeBinOp extends NodeExpr {
    private LangOperation op;   // Operatore (PLUS, MINUS, TIMES, DIVIDE)
    private NodeExpr left;      // Operando sinistro
    private NodeExpr right;     // Operando destro
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

**Esempio:**
```java
// 5 + 3 * 2
NodeBinOp(PLUS, 
    NodeConst("5", INT),
    NodeBinOp(TIMES, NodeConst("3", INT), NodeConst("2", INT))
)
```

#### `NodeConst.java` (extends `NodeExpr`)
**Scopo:** Rappresenta una costante numerica.

```java
public class NodeConst extends NodeExpr {
    private String valore;   // Valore come stringa
    private LangType tipo;   // INT o FLOAT
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

#### `NodeDeref.java` (extends `NodeExpr`)
**Scopo:** Rappresenta il dereferenziamento di una variabile (lettura del suo valore).

```java
public class NodeDeref extends NodeExpr {
    private NodeId id;  // Variabile da leggere
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

**Esempio:**
```java
// x + 5  (x è dereferenziato per leggere il suo valore)
NodeBinOp(PLUS, NodeDeref(NodeId("x")), NodeConst("5", INT))
```

#### `NodeId.java`
**Scopo:** Rappresenta un identificatore (nome di variabile).

```java
public class NodeId implements Node {
    private String name;            // Nome della variabile
    private Attributes attributes;  // Attributi (tipo, registro)
    
    public void accept(IVisitor visitor) {
        visitor.visit(this);
    }
}
```

### Enum: `LangType.java`

```java
public enum LangType {
    INT,
    FLOAT
}
```

### Enum: `LangOperation.java`

```java
public enum LangOperation {
    PLUS,    // +
    MINUS,   // -
    TIMES,   // *
    DIVIDE   // /
}
```

---

## 5️⃣ SYMBOL TABLE (Tabella dei Simboli)

### 📦 Package: `symbolTable`

### Classe: `SymbolTable.java`

**Scopo:** Memorizza le variabili dichiarate e i loro attributi durante la compilazione.

**Implementazione:**
```java
public class SymbolTable {
    private static HashMap<String, Attributes> symbolTable = new HashMap<>();
    
    // Metodi statici
    public static void enter(String id, Attributes attributes)
    public static Attributes lookup(String id)
    public static void clearTable()
}
```

**Funzionamento:**
- È una classe statica (singleton implicito)
- Usa una `HashMap<String, Attributes>` per memorizzare variabili
- Chiave: nome della variabile
- Valore: attributi della variabile

**Metodi:**

- **`enter(String id, Attributes attr)`**: Inserisce una nuova variabile
- **`lookup(String id)`**: Cerca una variabile, restituisce `null` se non esiste
- **`clearTable()`**: Pulisce la tabella (usato tra i test)

**Esempio:**
```java
// int x = 5;
SymbolTable.enter("x", new Attributes(LangType.INT));

// Controllo se la variabile esiste
Attributes attr = SymbolTable.lookup("x");
if (attr == null) {
    // Errore: variabile non dichiarata
}
```

### Classe: `Attributes.java`

**Scopo:** Memorizza gli attributi di una variabile.

```java
public class Attributes {
    private LangType type;     // Tipo della variabile (INT o FLOAT)
    private char registro;     // Registro assegnato (per la generazione codice)
    
    public Attributes(LangType type) {
        this.type = type;
        this.registro = ' ';  // Sarà assegnato dal code generator
    }
    
    // Getter e setter
    public LangType getType()
    public void setType(LangType type)
    public char getRegistro()
    public void setRegistro(char registro)
}
```

---

## 6️⃣ TYPE CHECKER (Analisi Semantica)

### 📦 Package: `visitor`

### Classe: `TypeCheckingVisitor.java`

**Scopo:** Verifica la correttezza semantica del programma: tipi compatibili, variabili dichiarate, ecc.

**Implementa:** `IVisitor` (pattern Visitor)

**Campi:**
```java
private ResType resType;  // Risultato del type checking (OkType o ErrorType)
```

**Metodo pubblico:**
```java
public ResType getResType()  // Restituisce il risultato del type checking
```

### Metodi visit (uno per ogni tipo di nodo):

#### `visit(NodeProgram node)`
```java
public void visit(NodeProgram node) {
    for (NodeDecSt decSt : node.getDecSts()) {
        decSt.accept(this);
        if (resType instanceof ErrorType) {
            return;  // Ferma alla prima errore
        }
    }
    resType = new OkType();  // Tutto ok
}
```

#### `visit(NodeDecl node)`
```java
public void visit(NodeDecl node) {
    String id = node.getId().getName();
    
    // 1. Controlla se la variabile è già dichiarata
    if (SymbolTable.lookup(id) != null) {
        resType = new ErrorType("Errore semantico: " + id + " già dichiarato!");
        return;
    }
    
    // 2. Inserisci nella symbol table
    SymbolTable.enter(id, new Attributes(node.getType()));
    node.getId().setAttributes(SymbolTable.lookup(id));
    
    // 3. Se c'è inizializzazione, controlla il tipo
    if (node.getInit() != null) {
        node.getInit().accept(this);
        
        LangType exprType = ((TypeDecorator) resType).getTipo();
        if (!node.getType().equals(exprType)) {
            resType = new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!");
        }
    }
}
```

**Controlli:**
- Dichiarazione duplicata
- Tipo dell'inizializzazione compatibile

#### `visit(NodeAssign node)`
```java
public void visit(NodeAssign node) {
    String id = node.getId().getName();
    
    // 1. Controlla se la variabile è dichiarata
    Attributes attr = SymbolTable.lookup(id);
    if (attr == null) {
        resType = new ErrorType("Errore semantico: " + id + " non è stato dichiarato!");
        return;
    }
    
    node.getId().setAttributes(attr);
    
    // 2. Controlla il tipo dell'espressione
    node.getNodeExpr().accept(this);
    
    LangType exprType = ((TypeDecorator) resType).getTipo();
    if (!attr.getType().equals(exprType)) {
        resType = new ErrorType("Errore semantico: assegnamento a tipo non corrispondente!");
    }
}
```

**Controlli:**
- Variabile dichiarata
- Tipo compatibile nell'assegnamento

#### `visit(NodePrint node)`
```java
public void visit(NodePrint node) {
    String id = node.getId().getName();
    
    // Controlla se la variabile è dichiarata
    Attributes attr = SymbolTable.lookup(id);
    if (attr == null) {
        resType = new ErrorType("Errore semantico: " + id + " non è stato dichiarato!");
        return;
    }
    
    node.getId().setAttributes(attr);
    resType = new OkType();
}
```

#### `visit(NodeBinOp node)`
```java
public void visit(NodeBinOp node) {
    // 1. Visita l'operando sinistro
    node.getLeft().accept(this);
    LangType leftType = ((TypeDecorator) resType).getTipo();
    
    // 2. Visita l'operando destro
    node.getRight().accept(this);
    LangType rightType = ((TypeDecorator) resType).getTipo();
    
    // 3. Determina il tipo risultante
    // Se almeno un operando è FLOAT, il risultato è FLOAT
    if (leftType == LangType.FLOAT || rightType == LangType.FLOAT) {
        resType = new FloatType();
    } else {
        resType = new IntType();
    }
}
```

**Regola dei tipi:**
- `INT op INT = INT`
- `FLOAT op INT = FLOAT`
- `INT op FLOAT = FLOAT`
- `FLOAT op FLOAT = FLOAT`

#### `visit(NodeDeref node)`
```java
public void visit(NodeDeref node) {
    String id = node.getId().getName();
    
    // Controlla se la variabile è dichiarata
    Attributes attr = SymbolTable.lookup(id);
    if (attr == null) {
        resType = new ErrorType("Errore semantico: " + id + " non è stato dichiarato!");
        return;
    }
    
    node.getId().setAttributes(attr);
    
    // Il tipo è quello della variabile
    if (attr.getType() == LangType.INT) {
        resType = new IntType();
    } else {
        resType = new FloatType();
    }
}
```

#### `visit(NodeConst node)`
```java
public void visit(NodeConst node) {
    // Il tipo è quello della costante
    if (node.getTipo() == LangType.INT) {
        resType = new IntType();
    } else {
        resType = new FloatType();
    }
}
```

#### `visit(NodeId node)`
```java
public void visit(NodeId node) {
    // Non fa nulla, gli ID sono gestiti nei nodi parent
}
```

### Gerarchia dei tipi di risultato:

```
ResType (interface)
├── OkType
├── ErrorType
└── TypeDecorator (abstract)
    ├── IntType
    └── FloatType
```

**`OkType`**: Type checking riuscito
**`ErrorType`**: Errore semantico con messaggio
**`IntType`/`FloatType`**: Tipo di un'espressione

---

## 7️⃣ CODE GENERATOR (Generazione del Codice)

### 📦 Package: `visitor`

### Classe: CodeGeneratorVisitor.java

**Scopo:** Genera codice target (linguaggio dc - desktop calculator) a partire dall'AST verificato.

**Implementa:** `IVisitor`

**Campi:**
```java
private String codiceDc;  // Codice generato
private String log;       // Log degli errori (es: registri esauriti)
```

**Metodi pubblici:**
```java
public String getCodiceDc()  // Restituisce il codice generato
public String getLog()       // Restituisce eventuali errori
```

### Linguaggio target (dc):

Il compilatore genera codice per la calcolatrice desktop `dc`, che usa notazione polacca inversa (RPN):

**Istruzioni dc:**
- `numero`: Push di un numero sullo stack
- `+`, `-`, `*`, `/`: Operazioni aritmetiche
- `sx`: Store nello stack frame x
- `lx`: Load dallo stack frame x
- `p`: Pop e stampa
- `P`: Pop (senza stampare)
- `k`: Imposta precisione decimale

### Metodi visit:

#### `visit(NodeProgram node)`
```java
public void visit(NodeProgram node) {
    for (NodeDecSt decSt : node.getDecSts()) {
        if (!log.isEmpty()) {
            return;  // Ferma se ci sono errori
        }
        decSt.accept(this);
    }
}
```

#### `visit(NodeDecl node)`
```java
public void visit(NodeDecl node) {
    String id = node.getId().getName();
    Attributes attr = SymbolTable.lookup(id);
    
    // 1. Assegna un registro
    Character reg = Registri.newRegister();
    if (reg == null) {
        log = "Errore: registri esauriti per la variabile " + id;
        return;
    }
    attr.setRegistro(reg);
    
    // 2. Se c'è inizializzazione, genera codice
    if (node.getInit() != null) {
        node.getInit().accept(this);
        codiceDc += "s" + reg + " ";  // Store nel registro
    }
}
```

**Esempio:**
```
int x = 5;
→ "5 sa "  (push 5, store in registro 'a')
```

#### `visit(NodeAssign node)`
```java
public void visit(NodeAssign node) {
    char reg = node.getId().getAttributes().getRegistro();
    
    // 1. Genera codice per l'espressione
    node.getNodeExpr().accept(this);
    
    // 2. Store nel registro
    codiceDc += "s" + reg + " ";
}
```

**Esempio:**
```
x = 3 + 5;
→ "3 5 + sa "  (push 3, push 5, somma, store in 'a')
```

#### `visit(NodePrint node)`
```java
public void visit(NodePrint node) {
    char reg = node.getId().getAttributes().getRegistro();
    
    // Load dal registro e stampa
    codiceDc += "l" + reg + " p P ";
}
```

**Esempio:**
```
print x;
→ "la p P "  (load da 'a', stampa, pop)
```

#### `visit(NodeBinOp node)`
```java
public void visit(NodeBinOp node) {
    // 1. Genera codice per operando sinistro
    node.getLeft().accept(this);
    
    // 2. Genera codice per operando destro
    node.getRight().accept(this);
    
    // 3. Genera operatore
    String op = switch (node.getOp()) {
        case PLUS -> "+";
        case MINUS -> "-";
        case TIMES -> "*";
        case DIVIDE -> "/";
    };
    codiceDc += op + " ";
}
```

**Esempio:**
```
3 + 5 * 2
→ "3 5 2 * + "  (notazione RPN)
```

**Nota sulle divisioni:** Per le divisioni tra interi, genera codice per convertire in float:
```java
if (node.getOp() == LangOperation.DIVIDE) {
    // Controlla se entrambi gli operandi sono INT
    // Se sì, converti in float:
    codiceDc += "0 k ";  // Imposta precisione 0
}
```

#### `visit(NodeDeref node)`
```java
public void visit(NodeDeref node) {
    char reg = node.getId().getAttributes().getRegistro();
    codiceDc += "l" + reg + " ";  // Load dal registro
}
```

#### `visit(NodeConst node)`
```java
public void visit(NodeConst node) {
    codiceDc += node.getValore() + " ";  // Push della costante
}
```

#### `visit(NodeId node)`
```java
public void visit(NodeId node) {
    // Non genera codice
}
```

### Classe: `Registri.java` (package `visitor.codegen`)

**Scopo:** Gestisce l'allocazione dei registri per le variabili.

```java
public class Registri {
    private static final char[] registri = 
        {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
    private static int usedRegisters = 0;
    
    public static Character newRegister() {
        if (usedRegisters < registri.length) {
            return registri[usedRegisters++];
        }
        return null;  // Registri esauriti
    }
    
    public static void reset() {
        usedRegisters = 0;
    }
}
```

**Limitazione:** Massimo 10 variabili (registri da 'a' a 'j').

### Esempio completo di generazione codice:

**Codice sorgente:**
```acdc
int a = 5 + 3;
float b = 0.5;
a = a + b;
print a;
print b;
```

**AST:**
```
NodeProgram
├── NodeDecl(a, INT, NodeBinOp(+, 5, 3))
├── NodeDecl(b, FLOAT, NodeConst(0.5))
├── NodeAssign(a, NodeBinOp(+, NodeDeref(a), NodeDeref(b)))
├── NodePrint(a)
└── NodePrint(b)
```

**Codice dc generato:**
```
5 3 + sa          // a = 5 + 3
0.5 sb            // b = 0.5
la lb + sa        // a = a + b
la p P            // print a
lb p P            // print b
```

---

## 8️⃣ VISITOR (Pattern Visitor)

### 📦 Package: `visitor`

### Interfaccia: `IVisitor.java`

**Scopo:** Definisce l'interfaccia per il pattern Visitor, permettendo di separare gli algoritmi (type checking, code generation) dalla struttura dell'AST.

```java
public interface IVisitor {
    void visit(NodeProgram node);
    void visit(NodeDecl node);
    void visit(NodeAssign node);
    void visit(NodePrint node);
    void visit(NodeBinOp node);
    void visit(NodeDeref node);
    void visit(NodeConst node);
    void visit(NodeId node);
}
```

**Vantaggi del pattern Visitor:**
- Separazione delle responsabilità
- Facile aggiungere nuove operazioni (nuovi visitor) senza modificare i nodi
- Type checking e code generation sono completamente separati

---

## 🔄 Flusso Completo di Compilazione

### Esempio pratico: `int x = 5; print x;`

#### 1️⃣ **Scanner** (Analisi Lessicale)
```
Input: "int x = 5; print x;"

Token generati:
1. Token(1, TYINT, "int")
2. Token(1, ID, "x")
3. Token(1, ASSIGN, "=")
4. Token(1, INT, "5")
5. Token(1, SEMI, ";")
6. Token(1, PRINT, "print")
7. Token(1, ID, "x")
8. Token(1, SEMI, ";")
9. Token(1, EOF, "")
```

#### 2️⃣ **Parser** (Analisi Sintattica)
```
Parsing:
parsePrg()
  └─ parseDSs()
       ├─ parseDcl()  // int x = 5;
       │    ├─ parseTy() → INT
       │    ├─ match(ID) → "x"
       │    └─ parseDclP()
       │         ├─ match(ASSIGN)
       │         ├─ parseExp()
       │         │    └─ parseVal() → NodeConst("5", INT)
       │         └─ match(SEMI)
       │
       └─ parseDSs()
            ├─ parseStm()  // print x;
            │    ├─ match(PRINT)
            │    ├─ match(ID) → "x"
            │    └─ match(SEMI)
            │
            └─ parseDSs() → ϵ (EOF)

AST generato:
NodeProgram
├── NodeDecl(NodeId("x"), INT, NodeConst("5", INT))
└── NodePrint(NodeId("x"))
```

#### 3️⃣ **Type Checker** (Analisi Semantica)
```
TypeCheckingVisitor.visit(NodeProgram):
  │
  ├─ visit(NodeDecl)
  │    ├─ Controlla: "x" già dichiarato? NO
  │    ├─ SymbolTable.enter("x", Attributes(INT))
  │    ├─ visit(NodeConst("5", INT)) → IntType
  │    └─ Verifica: INT == INT? SÌ ✓
  │
  └─ visit(NodePrint)
       ├─ Controlla: "x" dichiarato? SÌ ✓
       └─ Attributi recuperati da SymbolTable

Risultato: OkType (tutto corretto)
```

#### 4️⃣ **Code Generator** (Generazione Codice)
```
CodeGeneratorVisitor.visit(NodeProgram):
  │
  ├─ visit(NodeDecl)
  │    ├─ Assegna registro: 'a'
  │    ├─ visit(NodeConst("5", INT))
  │    │    └─ codiceDc += "5 "
  │    └─ codiceDc += "sa "
  │
  └─ visit(NodePrint)
       └─ codiceDc += "la p P "

Codice generato: "5 sa la p P"
```

#### 5️⃣ **Esecuzione su dc**
```bash
$ echo "5 sa la p P" | dc
5
```

---

## 📊 Tabella Riassuntiva delle Classi

| Package | Classe | Scopo |
|---------|--------|-------|
| `scanner` | `Scanner` | Analisi lessicale, generazione token |
| `scanner` | `LexicalException` | Errori lessicali |
| `token` | `Token` | Rappresentazione di un token |
| `token` | `TokenType` | Enum dei tipi di token |
| `parser` | `Parser` | Analisi sintattica, costruzione AST |
| `parser` | `SyntacticException` | Errori sintattici |
| `ast` | `Node` | Interfaccia per tutti i nodi AST |
| `ast` | `NodeProgram` | Radice dell'AST |
| `ast` | `NodeDecl` | Dichiarazione di variabile |
| `ast` | `NodeAssign` | Assegnamento |
| `ast` | `NodePrint` | Comando print |
| `ast` | `NodeBinOp` | Operazione binaria |
| `ast` | `NodeConst` | Costante numerica |
| `ast` | `NodeDeref` | Dereferenziamento variabile |
| `ast` | `NodeId` | Identificatore |
| `ast` | `LangType` | Enum dei tipi (INT, FLOAT) |
| `ast` | `LangOperation` | Enum delle operazioni |
| `symbolTable` | `SymbolTable` | Tabella dei simboli |
| `symbolTable` | `Attributes` | Attributi di una variabile |
| `visitor` | `IVisitor` | Interfaccia pattern Visitor |
| `visitor` | `TypeCheckingVisitor` | Analisi semantica |
| `visitor` | `CodeGeneratorVisitor` | Generazione codice |
| `visitor.type` | `ResType` | Risultato type checking |
| `visitor.type` | `OkType` | Type checking ok |
| `visitor.type` | `ErrorType` | Errore semantico |
| `visitor.type` | `IntType` | Tipo INT |
| `visitor.type` | `FloatType` | Tipo FLOAT |
| `visitor.codegen` | `Registri` | Gestione registri |

---

## 🎯 Conclusioni

Il compilatore AcDc implementa tutte le fasi classiche di un compilatore:

1. **Analisi Lessicale** → Token
2. **Analisi Sintattica** → AST
3. **Analisi Semantica** → Verifica tipi
4. **Generazione Codice** → Codice target

Ogni fase è ben separata e comunicano attraverso strutture dati intermedie (Token, AST, SymbolTable). Il pattern Visitor permette di aggiungere nuove analisi senza modificare l'AST.

**Punti di forza:**
✅ Architettura modulare e pulita  
✅ Gestione errori dettagliata con numero di riga  
✅ Type checking robusto  
✅ Codice ben documentato  

**Limitazioni:**
⚠️ Massimo 10 variabili (registri limitati)  
⚠️ Nessun scope (tutte le variabili sono globali)  
⚠️ Nessun recupero dagli errori (panic mode non implementato)
