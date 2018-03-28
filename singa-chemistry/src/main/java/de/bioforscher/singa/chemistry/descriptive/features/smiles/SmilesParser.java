package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.chemistry.descriptive.molecules.model.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.model.MoleculeGraph;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.structure.elements.Element;
import de.bioforscher.singa.structure.elements.ElementProvider;
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
    private final Queue<Character> symbols;
    /**
     * Collections of symbols that are handled as contextual connected
     */
    private final List<String> tokens;
    /**
     * The resulting MoleculeGraph
     */
    private final MoleculeGraph molecule;
    /**
     * Maps the consecutive connections between atoms (as a pair of identifiers) to the bond type
     */
    private final HashMap<Pair<Integer>, MoleculeBondType> connectors;
    /**
     * Maps the ring closure identifier to atom identifier that is to be connected
     */
    private final HashMap<Integer, Integer> ringClosures;
    /**
     * Holds the atom identifer, where hydrogen need to be added
     */
    private final List<Integer> hydrogens;
    /**
     * Remembers at which index a branch was opened
     */
    private final Deque<Integer> branches;
    /**
     * The symbol that is currently handled
     */
    private Character currentSymbol;
    /**
     * The token that is currently handled
     */
    private String currentToken;
    /**
     * The identifier of the last atom, that has been added to the graph
     */
    private int currentIdentifer = Integer.MIN_VALUE;
    /**
     * The current number of neutrons
     */
    private int currentMassNumber;
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
     * A flag that is set true when a branch is closed, so the last referenced branch is used to connect the next atom
     */
    private boolean firstAtomInBranch = false;

    /**
     * When two branches are following immediately after each other the same chainIdentifier reference is used again
     */
    private boolean sameChainReference;

    /**
     * Initializes the smiles Parser
     */
    public SmilesParser() {
        symbols = new LinkedList<>();
        tokens = new ArrayList<>();
        molecule = new MoleculeGraph();
        connectors = new HashMap<>();
        ringClosures = new HashMap<>();
        branches = new ArrayDeque<>();
        hydrogens = new ArrayList<>();
        currentToken = "";
        currentBondType = null;
    }

    /**
     * Parses a SMILES String and returns a {@link MoleculeGraph} that contains all the atoms and their connections.
     *
     * @param smilesString The SMILES String to parse
     * @return The MoleculeGraph generated from the smiles String.
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
        } else if (currentSymbol == '*') {
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

        if (currentSymbol == '(') {
            addToTokens();
            poll();
            openBranch();
            parseBond();

            int length = 0;
            while (parseSmiles()) {
                length++;
            }

            if (currentSymbol == ')') {
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

        switch (currentSymbol) {
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

        if (currentSymbol == '%') {
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
        switch (currentSymbol) {
            case 'B': {
                if (symbols.peek() == 'r') {
                    // Brom
                    dispose();
                    if (addLater) {
                        currentToken += "Br";
                        currentElement = ElementProvider.BROMINE;
                    } else {
                        tokens.add("Br");
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
                if (symbols.peek() == 'l') {
                    // Chlor
                    dispose();
                    if (addLater) {
                        currentToken += "Cl";
                        currentElement = ElementProvider.CHLORINE;
                    } else {
                        tokens.add("Cl");
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
            currentElement = ElementProvider.getElementBySymbol(String.valueOf(currentSymbol))
                    .orElseThrow(() -> new IllegalArgumentException("The symbol " + currentSymbol + " represents no valid element."));
        } else {
            addToTokens();
            addAtomToGraph(currentSymbol);
        }
    }

    private boolean parseAromaticSymbol(boolean addLater) {
        if (isEmpty()) {
            return false;
        }

        // AromaticSymbol ::= 'b' | 'c' | 'n' | 'o' | 'p' | 's'
        switch (currentSymbol) {
            case 'b':
            case 'c':
            case 'n':
            case 'o':
            case 'p':
            case 's': {
                handleAtom(addLater);
                currentBondType = MoleculeBondType.AROMATIC_BOND;
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
        if (currentSymbol == '[') {
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
                        if (currentSymbol == '*') {
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

            if (currentSymbol == ']') {
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
            isotopeCount += currentSymbol;
            poll();
            if (isDecimal()) {
                // parse second decimal
                isotopeCount += currentSymbol;
                poll();
                if (isDecimal()) {
                    // parse third decimal
                    isotopeCount += currentSymbol;
                    poll();
                }
            }
            currentToken += isotopeCount;
            currentMassNumber = Integer.valueOf(isotopeCount);
            return true;
        }
        return false;
    }

    private boolean parseAromaticSeleniumAndArsenic() {
        if (isEmpty()) {
            return false;
        }

        if (currentSymbol == 's') {
            if (symbols.peek() == 'e') {
                // parse selenium
                dispose();
                currentElement = ElementProvider.SELENIUM;
                currentBondType = MoleculeBondType.AROMATIC_BOND;
                poll();
                return true;
            }
            return false;
        } else if (currentSymbol == 'a') {
            if (symbols.peek() == 's') {
                // parse arsenic
                dispose();
                currentElement = ElementProvider.ARSENIC;
                currentBondType = MoleculeBondType.AROMATIC_BOND;
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
            element += currentSymbol;
            poll();
            if (isLowerCaseWordCharacter()) {
                addToCurrentToken();
                element += currentSymbol;
                poll();
            }
            currentElement = ElementProvider.getElementBySymbol(String.valueOf(element))
                    .orElseThrow(() -> new IllegalArgumentException("The symbol " + currentSymbol + " represents no valid element."));
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
        if (currentSymbol == '@') {
            addToCurrentToken();
            poll();
            if (currentSymbol == '@') {
                addToCurrentToken();
                poll();
                return true;
            } else if (currentSymbol == 'T') {
                if (symbols.peek() == 'H') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                } else if (symbols.peek() == 'B') {
                    addThisAndNext();
                    poll();
                    if (currentSymbol == '1') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (currentSymbol == '2') {
                        addToCurrentToken();
                        poll();
                        if (currentSymbol == '0') {
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
            } else if (currentSymbol == 'A') {
                if (symbols.peek() == 'L') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (currentSymbol == 'S') {
                if (symbols.peek() == 'P') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '3')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (currentSymbol == 'O') {
                if (symbols.peek() == 'H') {
                    addThisAndNext();
                    poll();
                    if (currentSymbol == '1') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (currentSymbol == '2') {
                        addToCurrentToken();
                        poll();
                        if (isDecimal()) {
                            addToCurrentToken();
                            poll();
                        }
                        return true;
                    } else if (currentSymbol == '3') {
                        addToCurrentToken();
                        poll();
                        if (currentSymbol == '0') {
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
        if (currentSymbol == 'H') {
            addToCurrentToken();
            poll();
            if (isDecimal()) {
                addToCurrentToken();
                connectHydrogens(Integer.valueOf(String.valueOf(currentSymbol)));
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
        if (currentSymbol == '+') {
            chargeToken += currentSymbol;
            addToCurrentToken();
            poll();
            if (currentSymbol == '+') {
                chargeToken += currentSymbol;
                addToCurrentToken();
                poll();
            } else {
                chargeToken += parseChargeNumber();
            }
            setCharge(chargeToken);
            return true;
        } else if (currentSymbol == '-') {
            chargeToken += currentSymbol;
            addToCurrentToken();
            poll();
            if (currentSymbol == '-') {
                chargeToken += currentSymbol;
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
        if (currentSymbol == '0') {
            chargeToken += currentSymbol;
            addToCurrentToken();
            poll();
        } else if (currentSymbol == '1') {
            chargeToken += currentSymbol;
            addToCurrentToken();
            poll();
            if (isInRage('0', '5')) {
                chargeToken += currentSymbol;
                addToCurrentToken();
                poll();
            }
        } else if (isInRage('2', '9')) {
            chargeToken += currentSymbol;
            addToCurrentToken();
            poll();
        }
        return chargeToken;
    }

    private void setCharge(String chargeToken) {
        switch (chargeToken) {
            case "+":
                currentCharge = 1;
                break;
            case "++":
                currentCharge = 2;
                break;
            case "-":
                currentCharge = -1;
                break;
            case "--":
                currentCharge = -2;
                break;
            default:
                currentCharge = Integer.valueOf(chargeToken);
                break;
        }
    }

    private boolean parseClass() {
        if (isEmpty()) {
            return false;
        }
        // Class    ::= ':' [0-9]+
        if (currentSymbol == ':') {
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
        currentBondType = MoleculeBondType.getBondForSMILESSymbol(currentSymbol);
    }

    private void connectConsecutiveAtoms() {
        if (molecule.getNodes().size() > 1) {
            if (firstAtomInBranch) {
                if (sameChainReference) {
                    connectors.put(new Pair<>(branches.peekLast(), currentIdentifer), currentBondType == null ? MoleculeBondType.SINGLE_BOND : currentBondType);
                    sameChainReference = false;
                } else {
                    connectors.put(new Pair<>(branches.pollLast(), currentIdentifer), currentBondType == null ? MoleculeBondType.SINGLE_BOND : currentBondType);
                }
                currentBondType = null;
                firstAtomInBranch = false;
            } else {
                connectors.put(new Pair<>(currentIdentifer - 1, currentIdentifer), currentBondType == null ? MoleculeBondType.SINGLE_BOND : currentBondType);
                currentBondType = null;
            }
        }
    }

    private void connectHydrogens(int hydrogenCount) {
        for (int count = 0; count < hydrogenCount; count++) {
            hydrogens.add(currentIdentifer + 1);
        }
    }

    private void addRingClosure() {
        int closureIdentifier = Integer.valueOf(String.valueOf(currentSymbol));
        if (ringClosures.containsKey(closureIdentifier)) {
            connectors.put(new Pair<>(ringClosures.get(closureIdentifier), currentIdentifer), MoleculeBondType.SINGLE_BOND);
            ringClosures.remove(closureIdentifier);
        } else {
            ringClosures.put(closureIdentifier, currentIdentifer);
        }
    }

    private boolean isEmpty() {
        return currentSymbol == null;
    }

    private boolean isInRage(Character rangeStart, Character rangeEnd) {
        return currentSymbol >= rangeStart && currentSymbol <= rangeEnd;
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
        currentToken += currentSymbol;
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
        logger.trace("read token {}", currentSymbol);
        tokens.add(String.valueOf(currentSymbol));
    }

    private void addAndClearCurrentToken() {
        logger.trace("read token {}", currentToken);
        tokens.add(currentToken);
        currentToken = "";
    }

    /**
     * Polls the next character from the symbols and sets the current symbol
     */
    private void poll() {
        currentSymbol = symbols.poll();
    }

    /**
     * Disposes the current character.
     */
    private void dispose() {
        poll();
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
        currentIdentifer = molecule.addNextAtom(atom);
    }

    private void addAtom() {
        if (currentMassNumber != 0) {
            currentIdentifer = molecule.addNextAtom(currentElement, currentCharge, currentMassNumber);
            currentMassNumber = 0;
        } else {
            currentIdentifer = molecule.addNextAtom(currentElement, currentCharge);
        }
        currentElement = null;
        currentCharge = 0;
    }

    private void openBranch() {
        if (!sameChainReference) {
            branches.add(currentIdentifer);
        }

    }

    private void closeBranch() {
        if (symbols.peek() == '(') {
            sameChainReference = true;
        }
        firstAtomInBranch = true;
    }

}
