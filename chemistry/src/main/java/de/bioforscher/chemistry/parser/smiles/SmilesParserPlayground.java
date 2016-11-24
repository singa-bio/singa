package de.bioforscher.chemistry.parser.smiles;

import de.bioforscher.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.util.GraphFactory;
import javafx.application.Application;

import java.util.*;

import static de.bioforscher.chemistry.descriptive.molecules.MoleculeBondType.*;

/**
 * Created by Christoph on 17/11/2016.
 */
public class SmilesParserPlayground {

    public static void main(String[] args) {
        SmilesParserPlayground playground = new SmilesParserPlayground();
        // String smilesString = "CCBBr[10*]CC[H]";
        // String smilesString = "Nc1ncnc2n(cnc12)[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        String smilesString = "[H]C(=O)[C@H](O)[C@@H](O)[C@H](O)[C@H](O)CO";
        System.out.println(smilesString);
        playground.parse(smilesString);

        GraphDisplayApplication.graph = playground.graph;
        Application.launch(GraphDisplayApplication.class);

    }

    private Queue<Character> queue;
    private Character currentSymbol;

    private List<String> tokens;
    private String currentToken;

    private MoleculeGraph graph;

    private int currentNeutronCount;
    private int currentCharge;

    private Optional<MoleculeBondType> lastBondType;
    // maps the consecutive connections between atoms (as a pair of identifiers) to the bond type
    private HashMap<Pair<Integer>, MoleculeBondType> connectors;
    // maps the ring closures  to the connecting atoms;
    private HashMap<Integer, Pair<Integer>> ringClosures;

    private int currentIdentifer = Integer.MIN_VALUE;
    private int lastBranchConnection;
    private boolean firstAtomInBranch;

    public SmilesParserPlayground() {
        this.queue = new LinkedList<>();
        this.tokens = new ArrayList<>();
        this.graph = new MoleculeGraph();
        this.connectors = new HashMap<>();
        this.ringClosures = new HashMap<>();
        this.currentToken = "";
        this.lastBondType = Optional.empty();
    }

    private void parse(String smilesString) {
        // SMILES   ::= Atom ( Chain | Branch )*

        for (char aChar : smilesString.toCharArray()) {
            this.queue.add(aChar);
        }

        this.currentSymbol = this.queue.poll();

        while (!this.queue.isEmpty()) {
            if (!parseSmiles()) {
                throw new IllegalArgumentException("The given string is no valid SMILES String (Exception was thrown" +
                        " after " + this.tokens + " have been parsed).");
            }
        }

        this.tokens.forEach(s -> System.out.print(s + " "));
        System.out.println();
        this.graph.getNodes().stream()
                .sorted(Comparator.comparing(MoleculeAtom::getIdentifier))
                .forEach(atom -> System.out.print(atom + " "));
        System.out.println();
        this.connectors.forEach((key, value) -> System.out.println(key + " - " + value));
        System.out.println();
        this.ringClosures.forEach((key, value) -> System.out.println(key + " - " + value));

        this.connectors.forEach( (connector,type) ->
                this.graph.addEdgeBetween(this.graph.getNode(connector.getFirst()), this.graph.getNode(connector.getSecond()), type));
        this.ringClosures.forEach( (identifier, connector) ->
                this.graph.addEdgeBetween(this.graph.getNode(connector.getFirst()), this.graph.getNode(connector.getSecond())));

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

        if (parseOrganicSymbol()) {
            connectConsecutiveAtoms();
            return true;
        } else if (parseAromaticSymbol(false)) {
            connectAromaticAtoms();
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
            createBranch();
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
                addToTokens();
                poll();
                closeBranch();
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
            case '\\': {
                addToTokens();
                setNextBond();
                poll();
                return true;
            }
            case '.': {
                // don't connect
                addToTokens();
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

    private boolean parseOrganicSymbol() {
        if (isEmpty()) {
            return false;
        }

        // OrganicSymbol ::= 'B' 'r'? | 'C' 'l'? | 'N' | 'O' | 'P' | 'S' | 'F' | 'I'
        switch (this.currentSymbol) {
            case 'B': {
                if (this.queue.peek() == 'r') {
                    // Brom
                    dispose();
                    addAtomToGraph("Br");

                } else {
                    // Bor
                    addToTokens();
                    addAtomToGraph(this.currentSymbol);
                }
                poll();
                return true;
            }
            case 'C': {
                if (this.queue.peek() == 'l') {
                    // Chlor
                    dispose();
                    this.tokens.add("Cl");
                    addAtomToGraph("Cl");
                } else {
                    // Carbon
                    addToTokens();
                    addAtomToGraph(this.currentSymbol);
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
                addToTokens();
                addAtomToGraph(this.currentSymbol);
                poll();
                return true;
            }
            default: {
                return false;
            }

        }
    }

    private boolean parseAromaticSymbol(boolean toToken) {
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
                if (toToken) {
                    addToCurrentToken();
                } else {
                    addToTokens();
                    addAtomToGraph(this.currentSymbol);
                }
                // todo take care of the aromatic bond
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
        if (isNonZeroDecimal()) {
            // parse first nonzero decimal
            addToCurrentToken();
            poll();
            if (isDecimal()) {
                // parse second decimal
                addToCurrentToken();
                poll();
                if (isDecimal()) {
                    // parse third decimal
                    addToCurrentToken();
                    poll();
                }
            }
            return true;
        }
        return false;

    }

    private boolean parseAromaticSeleniumAndArsenic() {
        if (isEmpty()) {
            return false;
        }

        if (this.currentSymbol == 's') {
            if (this.queue.peek() == 'e') {
                // parse selenium
                addThisAndNext();
                poll();
                return true;
            }
            return false;
        } else if (this.currentSymbol == 'a') {
            if (this.queue.peek() == 's') {
                // parse arsenic
                addThisAndNext();
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
            addAtomToGraph(element);
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
                if (this.queue.peek() == 'H') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                } else if (this.queue.peek() == 'B') {
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
                if (this.queue.peek() == 'L') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '2')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (this.currentSymbol == 'S') {
                if (this.queue.peek() == 'P') {
                    addThisAndNext();
                    poll();
                    if (isInRage('1', '3')) {
                        addToCurrentToken();
                        poll();
                        return true;
                    }
                }
            } else if (this.currentSymbol == 'O') {
                if (this.queue.peek() == 'H') {
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
            connectHydrogens(Integer.valueOf(String.valueOf(1)));
        }
        return false;
    }

    private boolean parseCharge() {
        if (isEmpty()) {
            return false;
        }

        // Charge   ::= '-' ( '-' | '0' | '1' [0-5]? | [2-9] )?
        //            | '+' ( '+' | '0' | '1' [0-5]? | [2-9] )?
        if (this.currentSymbol == '+') {
            addToCurrentToken();
            poll();
            if (this.currentSymbol == '+') {
                addToCurrentToken();
                poll();
            } else {
                parseChargeNumber();
            }
            return true;
        } else if (this.currentSymbol == '-') {
            addToCurrentToken();
            poll();
            if (this.currentSymbol == '-') {
                addToCurrentToken();
                poll();
            } else {
                parseChargeNumber();
            }
            return true;
        }
        return false;
    }

    private void parseChargeNumber() {
        if (this.currentSymbol == '0') {
            addToCurrentToken();
            poll();
        } else if (this.currentSymbol == '1') {
            addToCurrentToken();
            poll();
            if (isInRage('0', '5')) {
                addToCurrentToken();
                poll();
            }
        } else if (isInRage('2', '9')) {
            addToCurrentToken();
            poll();
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
        this.lastBondType = Optional.of(getBondForSMILESSymbol(this.currentSymbol));
    }

    private void connectConsecutiveAtoms() {
        if (this.graph.getNodes().size() > 1) {
            if (this.firstAtomInBranch) {
                this.connectors.put(new Pair<>(this.lastBranchConnection, this.currentIdentifer), this.lastBondType.orElse(SINGLE_BOND));
                this.lastBondType = Optional.empty();
                this.firstAtomInBranch = false;
            } else {
                this.connectors.put(new Pair<>(this.currentIdentifer - 1, this.currentIdentifer), this.lastBondType.orElse(SINGLE_BOND));
                this.lastBondType = Optional.empty();
            }
        }
    }

    private void connectAromaticAtoms() {
        this.connectors.put(new Pair<>(this.currentIdentifer - 1, this.currentIdentifer), AROMATIC_BOND);
    }

    private void connectHydrogens(int hydrogenCount) {
        for (int count = 0; count < hydrogenCount; count++) {
            int hydrogenIdentifier = this.graph.addNextAtom("H");
            this.connectors.put(new Pair<>(this.currentIdentifer, hydrogenIdentifier), SINGLE_BOND);
        }
    }

    private void addRingClosure() {
        int closureIdentifier = Integer.valueOf(String.valueOf(this.currentSymbol));
        Pair<Integer> closure = new Pair<>(this.currentIdentifer, Integer.MIN_VALUE);
        this.ringClosures.computeIfPresent(closureIdentifier,
                (key, value) -> value = new Pair<>(value.getFirst(), this.currentIdentifer));
        this.ringClosures.putIfAbsent(closureIdentifier, closure);
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
        this.tokens.add(String.valueOf(this.currentSymbol));
    }

    private void addAndClearCurrentToken() {
        this.tokens.add(this.currentToken);
        this.currentToken = "";
    }

    /**
     * Polls the next character from the queue and sets the current symbol
     */
    private void poll() {
        this.currentSymbol = this.queue.poll();
    }

    /**
     * Disposes the current character.
     */
    private void dispose() {
        this.poll();
    }

    private void addAtomToGraph(char atom) {
        addAtomToGraph(String.valueOf(atom));
    }

    private void addAtomToGraph(String atom) {
        this.currentIdentifer = this.graph.addNextAtom(atom);
    }

    private void createBranch() {
        this.lastBranchConnection = this.currentIdentifer;
        this.firstAtomInBranch = true;
    }

    private void closeBranch() {
        this.currentIdentifer = this.lastBranchConnection;
        this.firstAtomInBranch = true;
    }

}
