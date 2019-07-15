package bio.singa.mathematics.graphs.trees;

import java.util.*;

/**
 * @author cl
 */
public class NewickParser {

    private static final char DEFAULT_SEPARATOR = ',';

    private final char separator;

    private String newickString;

    private List<String> token;
    private String currentToken;

    private Deque<BinaryTreeNode<String>> ancestors;
    private BinaryTreeNode<String> tree;

    public NewickParser(String newickString) {
        this(newickString, DEFAULT_SEPARATOR);
    }

    public NewickParser(String newickString, char separator) {
        this.newickString = newickString;
        this.separator = separator;
        token = new ArrayList<>();
        ancestors = new ArrayDeque<>();
        tree = new BinaryTreeNode<>();
    }

    public static BinaryTreeNode<String> parseNewick(String newickString) {
        NewickParser parser = new NewickParser(newickString);
        return parser.parse();
    }

    public static BinaryTreeNode<String> parseNewick(String newickString, char separator) {
        NewickParser parser = new NewickParser(newickString, separator);
        return parser.parse();
    }

    private BinaryTreeNode<String> parse() {
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
                BinaryTreeNode<String> subtree = new BinaryTreeNode<>();
                tree.append(subtree);
                ancestors.push(tree);
                tree = subtree;
            } else if (String.valueOf(separator).equals(t)) {
                BinaryTreeNode<String> subtree = new BinaryTreeNode<>();
                ancestors.getFirst().append(subtree);
                tree = subtree;
            } else if (")".equals(t)) {
                tree = ancestors.pop();
            } else {
                tree.setData(t);
            }
        }
    }

    private void nameIntermediateNodes() {
        tree.inOrderTraversal(node -> node.setData(node.toNewickString(Objects::toString, String.valueOf(separator))));
    }

}
