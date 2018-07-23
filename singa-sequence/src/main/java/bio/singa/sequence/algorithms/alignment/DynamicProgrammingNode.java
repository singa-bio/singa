package bio.singa.sequence.algorithms.alignment;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.AbstractNode;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;

/**
 * @author cl
 */
public class DynamicProgrammingNode extends AbstractNode<DynamicProgrammingNode, Vector2D, Integer> {

    private double score;

    public DynamicProgrammingNode(Integer identifier) {
        super(identifier, Vectors.generateRandom2DVector(new Rectangle(200, 200)));
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public DynamicProgrammingNode getCopy() {
        return null;
    }

}
