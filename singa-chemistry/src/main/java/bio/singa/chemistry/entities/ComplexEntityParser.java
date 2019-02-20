package bio.singa.chemistry.entities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author cl
 */
public class ComplexEntityParser {

    private final char separator = ':';

    private String newickString;

    private List<String> token;
    private String currentToken;

    private Deque<ComplexEntity> ancestors;
    private ComplexEntity tree;
    private List<ChemicalEntity> reference;

    public ComplexEntityParser(String newickString, List<ChemicalEntity> reference) {
        this.newickString = newickString;
        this.reference = reference;
        token = new ArrayList<>();
        ancestors = new ArrayDeque<>();
        tree = new ComplexEntity();

    }

    public static ComplexEntity parseNewick(String newickString, List<ChemicalEntity> reference) {
        ComplexEntityParser parser = new ComplexEntityParser(newickString, reference);
        return parser.parse();
    }

    private ComplexEntity parse() {
        tokenize();
        buildTree();
        nameIntermediateNodes();
        return tree;
    }

    private void tokenize() {
        currentToken = "";
        for (char c : newickString.toCharArray()) {
            if (c == '(' || c == ')' || c == separator) {
                addPreviousToken();
                token.add(String.valueOf(c));
            } else {
                currentToken += c;
            }
        }
    }

    private void addPreviousToken() {
        if (currentToken.length() > 0) {
            token.add(currentToken);
            currentToken = "";
        }
    }

    private void buildTree() {
        for (String t : token) {
            if ("(".equals(t)) {
                ComplexEntity subtree = new ComplexEntity();
                tree.appendEntity(subtree);
                ancestors.push(tree);
                tree = subtree;
            } else if (String.valueOf(separator).equals(t)) {
                ComplexEntity subtree = new ComplexEntity();
                ancestors.getFirst().appendEntity(subtree);
                tree = subtree;
            } else if (")".equals(t)) {
                tree = ancestors.pop();
            } else {
                tree.setData(getFromReference(t));
            }
        }
    }

    private ChemicalEntity getFromReference(String entity) {
        for (ChemicalEntity chemicalEntity : reference) {
            if (chemicalEntity.getIdentifier().getContent().equals(entity)) {
                return chemicalEntity;
            }
        }
        return null;
    }


    private void nameIntermediateNodes() {
        tree.inOrderTraversal(node -> {
            if (!node.isLeaf()) {
                ComplexEntity entity = (ComplexEntity) node;
                entity.setData(entity);
                entity.setIdentifier(entity.toNewickString(t -> t.getIdentifier().getContent(), ":"));
            }
        });
    }

}
