package de.bioforscher.chemistry.physical;

import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.matrices.LabeledSymmetricMatrix;
import de.bioforscher.mathematics.vectors.Vector3D;
import de.bioforscher.mathematics.vectors.VectorUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 09.06.2016.
 */
public class SubStructure extends AbstractGraph<Atom, Bond, Vector3D> implements StructuralEntity<SubStructure> {

    public int identifier;

    private List<SubStructure> neighbours;

    public SubStructure() {
        neighbours = new ArrayList<>();
    }

    public SubStructure(Atom atom) {
        this();
        this.identifier = atom.getIdentifier();
        this.addNode(atom);
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public Vector3D getPosition() {
        return VectorUtilities.getCentroid(this.getNodes().stream()
                .map(Atom::getPosition)
                .collect(Collectors.toList()))
                .as(Vector3D.class);
    }

    @Override
    public void addNeighbour(SubStructure node) {
        this.neighbours.add(node);
    }

    @Override
    public List<SubStructure> getNeighbours() {
        return this.neighbours;
    }

    @Override
    public int getDegree() {
        return 0;
    }

    public void connectByDistance() {
        // calculate pairwise distances
        LabeledSymmetricMatrix<Atom> distances = StructureUtilities.calculateDistanceMatrix(getNodes().stream()
                .collect(Collectors.toList()));
        // connect nodes that are below a certain distance
        for (int rowIndex = 0; rowIndex < distances.getElements().length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < distances.getElements()[rowIndex].length; columnIndex++) {
                if (rowIndex != columnIndex && distances.getElement(rowIndex, columnIndex) < 1.8) {
                    connectWithEdge(getNextEdgeIdentifier(),
                            getNode(distances.getRowLabel(rowIndex).getIdentifier()),
                            getNode(distances.getColumnLabel(columnIndex).getIdentifier()),
                            new Bond(BondType.COVALENT_BOND));
                }
            }
        }

    }
}
