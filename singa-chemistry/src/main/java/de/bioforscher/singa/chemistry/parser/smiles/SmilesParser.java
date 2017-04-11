package de.bioforscher.singa.chemistry.parser.smiles;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class SmilesParser {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SmilesParser.class);

    /**
     * The symbols of the SMILES String
     */
    private Queue<Character> symbols;

    /**
     * The symbol that is currently handled
     */
    private Character currentSymbol;

    /**
     * Collections of symbols that are handled as contextual connected
     */
    private List<String> tokens;

    /**
     * The token that is currently handled
     */
    private String currentToken;

    /**
     * The resulting MoleculeGraph
     */
    private MoleculeGraph molecule;

    /**
     * The identifier of the last atom, that has been added to the graph
     */
    private int currentIdentifer = Integer.MIN_VALUE;

    /**
     * The current number of neutrons
     */
    private int currentNeutronCount;

    /**
     * The current charge
     */
    private int currentCharge;

    /**
     * The current element
     */
    private Element currentElement;

    /**
     * The bond type to connect the next atom with
     */
    private MoleculeBondType currentBondType;

    /**
     * Maps the consecutive connections between atoms (as a pair of identifiers) to the bond type
     */
    private HashMap<Pair<Integer>, MoleculeBondType> connectors;

    /**
     * Maps the ring closure identifier to atom identifier that is to be connected
     */
    private HashMap<Integer, Integer> ringClosures;

    /**
     * Holds the atom identifer, where hydrogen need to be added
     */
    private List<Integer> hydrogens;

    /**
     * Remembers at which index a branch was opened
     */
    private Deque<Integer> branches;

    /**
     * A flag that is set true when a branch is closed, so the last referenced branch is used to connect the next atom
     */
    private boolean firstAtomInBranch = false;

    /**
     * When two branches are following immediately after each other the same chain reference is used again
     */
    private boolean sameChainReference;

    /**
     * Initializes the smiles Parser
     */
    public SmilesParser() {
        this.symbols = new LinkedList<>();
        this.tokens = new ArrayList<>();
        this.molecule = new MoleculeGraph();
        this.connectors = new HashMap<>();
        this.ringClosures = new HashMap<>();
        this.branches = new ArrayDeque<>();
        this.hydrogens = new ArrayList<>();
        this.currentToken = "";
        this.currentBondType = null;
    }

    /**
     * Parses a SMILES String and returns a {@link MoleculeGraph} that contains all the atoms and their connections.
     *
     * @param smilesString The SMILES String to parse
     */
    public static MoleculeGraph parse(String smilesString) {
        logger.info("parsing smiles string {} ", smilesString);
        SmilesParser parser = new SmilesParser();
        // extract symbols
        for (char aChar : smilesString.toCharArray()) {
            parser.symbols.add(aChar);
        }
        // poll the first symbol
        parser.currentSymbol = parser.symbols.poll();
        // try to parse the string
        while (!parser.symbols.isEmpty()) {
            if (!parser.parseSmiles()) {
                throw new IllegalArgumentException("The given string is no valid SMILES String (Exception was thrown" +
                        " after " + parser.tokens + " have been parsed).");
            }
        }

        // add hydrogens to connectors
        parser.hydrogens.forEach((identifier) -> {
            int hydrogenIdentifier = parser.molecule.addNextAtom("H");
            parser.connectors.put(new Pair<>(identifier, hydrogenIdentifier), MoleculeBondType.SINGLE_BOND);
        });

        // add bonds
        parser.connectors.forEach((connector, type) -> {
            if (type != MoleculeBondType.UNCONNECTED) {
                parser.molecule.addEdgeBetween(parser.molecule.getNode(connector.getFirst()),
                        parser.molecule.getNode(connector.getSecond()), type);
            }
        });

        // transform aromatic bonds to double bonds

        return parser.molecule;

    }

    private boolean parseSmiles() {
        // SMILES ::= Atom ( Chain | Branch )*
        if (isEmpty()) {
            return false;
        }

        if (!parseAtom()) {
            return false;
        }

        boolean parsable;
        do {
            parsable = parseBranch();
            parsable |= parseChain();
        } while (parsable);

        return true;
    }

    private boolean parseAtom() {
        if (isEmpty()) {
            return false;
        }

        if (parseOrganicSymbol(false)) {
            connectConsecutiveAtoms();
            return true;
        } else if (parseAromaticSymbol(false)) {
            connectConsecutiveAtoms();
            return true;
        } else if (parseAtomSpecification()) {
            connectConsecutiveAtoms();
            return true;
        } else if (this.currentSymbol == '*') {
            addToTokens();
            poll();
        }
        return false;
    }

    private boolean parseBranch() {
        // Branch ::= '(' Bond? SMILES+ ')'
        if (isEmpty()) {
            return false;
        }

        if (this.currentSymbol == '(') {
            addToTokens();
            poll();
            openBranch();
            parseBond();

            int length = 0;
            while (parseSmiles()) {
                length++;
            }

            if (this.currentSymbol == ')') {
                // ending with closed round brackets
                if (length < 1) {
                    return false;
                }
                closeBranch();
                addToTokens();
                poll();
                return true;
            }
        }
        return false;
    }

    private boolean parseChain() {
        if (isEmpty()) {
            return false;
        }

        boolean parsable;
        int length = 0;
        do {
            parseBond();
            parsable = parseAtom();
            parsable |= parseRingClosure();
            if (parsable) {
                length++;
            }
        } while (parsable);
        return length > 0;
    }

    private boolean parseBond() {
        if (isEmpty()) {
            return false;
        }

        switch (this.currentSymbol) {
            case '-':
            case '=':
            case '#':
            case '$':
            case ':':
            case '/':
            case '.':
            case '\\': {
                addToTokens();
                setNextBond();
                poll();
                return true;
            }
            default: {
                return false;
            }
        }

    }

    private boolean parseRingClosure() {
        if (isEmpty()) {
            return false;
        }

        if (this.currentSymbol == '%') {
            if (isNonZeroDecimal()) {
                addToCurrentToken();
                poll();
                if (isDecimal()) {
                    addToCurrentToken();
                    poll();
                    return true;
                }
            }
        } else if (isDecimal()) {
            addToTokens();
            addRingClosure();
            poll();
            return true;
        }
        return false;
    }

    private boolean parseOrganicSymbol(boolean addLater) {
        if (isEmpty()) {
            return false;
        }

        // OrganicSymbol ::= 'B' 'r'? | 'C' 'l'? | 'N' | 'O' | 'P' | 'S' | 'F' | 'I'
        switch (this.currentSymbol) {
            case 'B': {
                if (this.symbols.peek() == 'r') {
                    // Brom
                    dispose();
                    if (addLater) {
                        this.currentToken += "Br";
                        this.currentElement = ElementProvider.BROMINE;
                    } else {
                        this.tokens.add("Br");
                        addAtomToGraph("Br");
                    }
                } else {
                    // Bor
                    handleAtom(addLater);
                }
                poll();
                return true;
            }
            case 'C': {
                if (this.symbols.peek() == 'l') {
                    // Chlor
                    dispose();
                    if (addLater) {
                        this.currentToken += "Cl";
                        this.currentElement = ElementProvider.CHLORINE;
                    } else {
                        this.tokens.add("Cl");
                        addAtomToGraph("Cl");
                    }
                } else {
                    // Carbon
                    handleAtom(addLater);
                }
                poll();
                return true;
            }
            case 'N':
            case 'O':
            case 'P':
            case 'S':
            case 'F':
            case 'I': {
                handleAtom(addLater);
                poll();
                return true;
            }
            default: {
                return false;
            }

        }
    }

    private void handleAtom(boolean addLater) {
        if (addLater) {
            addToCurrentToken();
            this.currentElement = ElementProvider.getElementBySymbol(String.valueOf(this.currentSymbol))
                    .orElseThrow(() -> new IllegalArgumentException("The symbol " + this.currentSymbol + " represents no valid element."));
        } else {
            addToTokens();
            addAtomToGraph(this.currentSymbol);
        }
    }

    private boolean parseAromaticSymbol(boolean addLater) {
        if (isEmpty()) {
            return false;
        }

        // AromaticSymbol ::= 'b' | 'c' | 'n' | 'o' | 'p' | 's'
        switch (this.currentSymbol) {
            case 'b':
            case 'c':
            case 'n':
            case 'o':
            case 'p':
            case 's': {
                handleAtom(addLater);
                this.currentBondType = MoleculeBondType.AROMATIC_BOND;
                poll();
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private boolean parseAtomSpecification() {
        if (isEmpty()) {
            return false;
        }

        // AtomSpec ::= '[' Isotope? ( 'se' | 'as' | AromaticSymbol | ElementSymbol | WILDCARD ) ChiralClass? HCount? Charge? Class? ']'
        if (this.currentSymbol == '[') {
            addToCurrentToken();
            poll();
            // try to parse isotope
            parseIsotope();
            // one of the following has to match!
            // try to parse aromatic selene or arsenic
            if (!parseAromaticSeleniumAndArsenic()) {
                // if not, try to parse other aromatic elements
                if (!parseAromaticSymbol(true)) {
                    // if not, try to parse element
                    if (!parseElementSymbol()) {
                        // if not, try to parse wildcard
                        if (this.currentSymbol == '*') {
                            addToCurrentToken();
                            poll();
                        } else {
                            // if all of the previous fail the smiles is invalid
                            return false;
                        }
                    }
                }
            }

            parseChirality();
            parseHCount();
            parseCharge();
            parseClass();

            addAtom();

            if (this.currentSymbol == ']') {
                // ending with closed square brackets
                addToCurrentToken();
                addAndClearCurrentToken();
                poll();
                return true;
            }
        }
        return false;
    }

    private boolean parseIsotope() {
        if (isEmpty()) {
            return false;
        }

        // Isotope  ::= [1-9] [0-9]? [0-9]?
        String isotopeCount = "";
        if (isNonZeroDecimal()) {
            // parse first nonzero decimal
            isotopeCount += this.currentSymbol;
            poll();
            if (isDecimal()) {
                // parse second decimal
                isotopeCount += this.currentSymbol;
                poll();
                if (isDecimal()) {
                    // parse third decimal
                    isotopeCount += this.currentSymbol;
                    poll();
                }
            }
            this.currentToken += isotopeCount;
            this.currentNeutronCount = Integer.valueOf(isotopeCount);
            return true;
        }
        return false;

    }

    private boolean parseAromaticSeleniumAndArsenic() {
        if (isEmpty()) {
            return false;
        }

        if (this.currentSymbol == 's') {
            if (this.symbols.peek() == 'e') {
                // parse selenium
                dispose();
                this.currentElement = ElementProvider.SELENIUM;
                this.currentBondType = MoleculeBondType.AROMATIC_BOND;
                poll();
                return true;
            }
            return false;
        } else if (this.currentSymbol == 'a') {
            if (this.symbols.peek() == 's') {
                // parse arsenic
                dispose();
                this.currentElement = ElementProvider.ARSENIC;
                this.currentBondType = MoleculeBondType.AROMATIC_BOND;
                poll();
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean parseElementSymbol() {
        // ElementSymbol ::= [A-Z] [a-z]?
        if (isUpperCaseWordCharacter()) {
            String element = "";
            addToCurrentToken();
            element += this.currentSymbol;
            poll();
            if (isLowerCaseWordCharacter()) {
                addToCurrentToken();
                element += this.currentSymbol;
                poll();
            }
            this.currentElement = ElementProvider.getElementBySymbol(String.valueOf(element))
                    .orElseThrow(() -> new IllegalArgumentException("The symbol " + this.currentSymbol + " represents no valid element."));
            return true;
        }
        return false;
    }

    private boolean parseChirality() {
        if (isEmpty()) {
            return false;
        }

        // ChiralClass ::= ( '@' ( '@' | 'TH' [1-2] | 'AL' [1-2] | 'SP' [1-3] |
        // 'TB' ( '1' [0-9]? | '2' '0'? | [3-9] ) | 'OH' ( '1' [0-9]? | '2' [0-9]? | '3' '0'? | [4-9] ) )? )?
        if (this.currentSymbol == '@') {
            addToCurrentToken();
            poll();
            if (this.currentSymbol == '@') {
                addToCurrentToken();
                poll();
                return true;
            } else if (this.currentSymbol == 'T') {
                if (this.symbols.peek() == 'H') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                } else if (this.symbols.peek() == 'B') {
                    addThisAndNext();
                    poll();
                    if (this.currentSymbol == '1') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (this.currentSymbol == '2') {
                        addToCurrentToken();
                        poll();
                        if (this.currentSymbol == '0') {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (isInRage('3', '9')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (this.currentSymbol == 'A') {
                if (this.symbols.peek() == 'L') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (this.currentSymbol == 'S') {
                if (this.symbols.peek() == 'P') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '3')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (this.currentSymbol == 'O') {
                if (this.symbols.peek() == 'H') {
                    addThisAndNext();
                    poll();
                    if (this.currentSymbol == '1') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (this.currentSymbol == '2') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (this.currentSymbol == '3') {
                        addToCurrentToken();
                        poll();
                        if (this.currentSymbol == '0') {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (isInRage('4', '9')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean parseHCount() {
        if (isEmpty()) {
            return false;
        }

        // HCount   ::= 'H' [0-9]?
        if (this.currentSymbol == 'H') {
            addToCurrentToken();
            poll();
            if (isDecimal()) {
                addToCurrentToken();
                connectHydrogens(Integer.valueOf(String.valueOf(this.currentSymbol)));
                poll();
                return true;
            }
            connectHydrogens(1);
        }
        return false;
    }

    private boolean parseCharge() {
        if (isEmpty()) {
            return false;
        }
        String chargeToken = "";
        // Charge   ::= '-' ( '-' | '0' | '1' [0-5]? | [2-9] )?
        //            | '+' ( '+' | '0' | '1' [0-5]? | [2-9] )?
        if (this.currentSymbol == '+') {
            chargeToken += this.currentSymbol;
            addToCurrentToken();
            poll();
            if (this.currentSymbol == '+') {
                chargeToken += this.currentSymbol;
                addToCurrentToken();
                poll();
            } else {
                chargeToken += parseChargeNumber();
            }
            setCharge(chargeToken);
            return true;
        } else if (this.currentSymbol == '-') {
            chargeToken += this.currentSymbol;
            addToCurrentToken();
            poll();
            if (this.currentSymbol == '-') {
                chargeToken += this.currentSymbol;
                addToCurrentToken();
                poll();
            } else {
                chargeToken += parseChargeNumber();
            }
            setCharge(chargeToken);
            return true;
        }
        return false;
    }

    private String parseChargeNumber() {
        String chargeToken = "";
        if (this.currentSymbol == '0') {
            chargeToken += this.currentSymbol;
            addToCurrentToken();
            poll();
        } else if (this.currentSymbol == '1') {
            chargeToken += this.currentSymbol;
            addToCurrentToken();
            poll();
            if (isInRage('0', '5')) {
                chargeToken += this.currentSymbol;
                addToCurrentToken();
                poll();
            }
        } else if (isInRage('2', '9')) {
            chargeToken += this.currentSymbol;
            addToCurrentToken();
            poll();
        }
        return chargeToken;
    }

    private void setCharge(String chargeToken) {
        switch (chargeToken) {
            case "+":
                this.currentCharge = 1;
                break;
            case "++":
                this.currentCharge = 2;
                break;
            case "-":
                this.currentCharge = -1;
                break;
            case "--":
                this.currentCharge = -2;
                break;
            default:
                this.currentCharge = Integer.valueOf(chargeToken);
                break;
        }
    }

    private boolean parseClass() {
        if (isEmpty()) {
            return false;
        }
        // Class    ::= ':' [0-9]+
        if (this.currentSymbol == ':') {
            addToCurrentToken();
            poll();
            int length = 0;
            while (isDecimal()) {
                addToCurrentToken();
                poll();
                length++;
            }
            return length > 0;
        }
        return false;
    }

    private void setNextBond() {
        this.currentBondType = MoleculeBondType.getBondForSMILESSymbol(this.currentSymbol);
    }

    private void connectConsecutiveAtoms() {
        if (this.molecule.getNodes().size() > 1) {
            if (this.firstAtomInBranch) {
                if (this.sameChainReference) {
                    this.connectors.put(new Pair<>(this.branches.peekLast(), this.currentIdentifer), this.currentBondType == null ? MoleculeBondType.SINGLE_BOND : this.currentBondType);
                    this.sameChainReference = false;
                } else {
                    this.connectors.put(new Pair<>(this.branches.pollLast(), this.currentIdentifer), this.currentBondType == null ? MoleculeBondType.SINGLE_BOND : this.currentBondType);
                }
                this.currentBondType = null;
                this.firstAtomInBranch = false;
            } else {
                this.connectors.put(new Pair<>(this.currentIdentifer - 1, this.currentIdentifer), this.currentBondType == null ? MoleculeBondType.SINGLE_BOND : this.currentBondType);
                this.currentBondType = null;
            }
        }
    }

    private void connectHydrogens(int hydrogenCount) {
        for (int count = 0; count < hydrogenCount; count++) {
            this.hydrogens.add(this.currentIdentifer + 1);
        }
    }

    private void addRingClosure() {
        int closureIdentifier = Integer.valueOf(String.valueOf(this.currentSymbol));
        if (this.ringClosures.containsKey(closureIdentifier)) {
            this.connectors.put(new Pair<>(this.ringClosures.get(closureIdentifier), this.currentIdentifer), MoleculeBondType.SINGLE_BOND);
            this.ringClosures.remove(closureIdentifier);
        } else {
            this.ringClosures.put(closureIdentifier, this.currentIdentifer);
        }
    }

    private boolean isEmpty() {
        return this.currentSymbol == null;
    }

    private boolean isInRage(Character rangeStart, Character rangeEnd) {
        return this.currentSymbol >= rangeStart && this.currentSymbol <= rangeEnd;
    }

    /**
     * Checks if the current symbol is a decimal character, but not zero [1-9]
     *
     * @return {@code true} if the current symbol is a decimal character, but not zero.
     */
    private boolean isNonZeroDecimal() {
        return isInRage('1', '9');
    }

    /**
     * Checks if the current symbol is a decimal character [0-9]
     *
     * @return {@code true} if the current symbol is a decimal character.
     */
    private boolean isDecimal() {
        return isInRage('0', '9');
    }

    /**
     * Checks if the current symbol is a lowercase word character [a-z]
     *
     * @return {@code true} if the current symbol is a lowercase word character.
     */
    private boolean isLowerCaseWordCharacter() {
        return isInRage('a', 'z');
    }

    /**
     * Checks if the current symbol is a uppercase word character [A-Z]
     *
     * @return {@code true} if the current symbol is a uppercase word character.
     */
    private boolean isUpperCaseWordCharacter() {
        return isInRage('A', 'Z');
    }

    /**
     * Adds the current symbol to the current token.
     */
    private void addToCurrentToken() {
        this.currentToken += this.currentSymbol;
    }

    private void addThisAndNext() {
        addToCurrentToken();
        poll();
        addToCurrentToken();
    }

    /**
     * Adds the current symbol to the collected tokens.
     */
    private void addToTokens() {
        logger.trace("read token {}", this.currentSymbol);
        this.tokens.add(String.valueOf(this.currentSymbol));
    }

    private void addAndClearCurrentToken() {
        logger.trace("read token {}", this.currentToken);
        this.tokens.add(this.currentToken);
        this.currentToken = "";
    }

    /**
     * Polls the next character from the symbols and sets the current symbol
     */
    private void poll() {
        this.currentSymbol = this.symbols.poll();
    }

    /**
     * Disposes the current character.
     */
    private void dispose() {
        this.poll();
    }

    /**
     * Adds a atom to the molecule. This method creates a new atom with the specified symbol({@link Element#getSymbol()})
     *
     * @param atom The symbol of the element of the new atom.
     */
    private void addAtomToGraph(char atom) {
        addAtomToGraph(String.valueOf(atom));
    }

    /**
     * Adds a atom to the molecule. This method creates a new atom with the specified symbol({@link Element#getSymbol()})
     *
     * @param atom The symbol of the element of the new atom.
     */
    private void addAtomToGraph(String atom) {
        this.currentIdentifer = this.molecule.addNextAtom(atom);
    }

    private void addAtom() {
        if (this.currentNeutronCount != 0) {
            this.currentIdentifer = this.molecule.addNextAtom(this.currentElement, this.currentCharge, this.currentNeutronCount);
            this.currentNeutronCount = 0;
        } else {
            this.currentIdentifer = this.molecule.addNextAtom(this.currentElement, this.currentCharge);
        }
        this.currentElement = null;
        this.currentCharge = 0;
    }

    private void openBranch() {
        if (!this.sameChainReference) {
            this.branches.add(this.currentIdentifer);
        }

    }

    private void closeBranch() {
        if (this.symbols.peek() == '(') {
            this.sameChainReference = true;
        }
        this.firstAtomInBranch = true;
    }

}
