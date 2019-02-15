package bio.singa.chemistry.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static bio.singa.chemistry.entities.ComplexModification.Operation.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class ComplexEntityTest {

    @Test
    @DisplayName("complex entity - create two part complex")
    void createTwoPartComplex() {
        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();

        ComplexEntity complexEntity = ComplexEntity.from(a, b);
        assertEquals(a, complexEntity.getLeft().getData());
        assertEquals(b, complexEntity.getRight().getData());
    }

    @Test
    @DisplayName("complex entity - create three part complex")
    void crateThreePartComplex() {
        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();

        ComplexEntity abc = ComplexEntity.from(a, b, c);
        assertNotNull(abc.find(a));
        assertNotNull(abc.find(b));
        assertNotNull(abc.find(c));
    }

    @Test
    @DisplayName("complex entity - parse from string")
    void createFromString() {
        ChemicalEntity a = SmallMolecule.create("A").build();
        ChemicalEntity b = SmallMolecule.create("B").build();
        ChemicalEntity c = SmallMolecule.create("C").build();
        List<ChemicalEntity> reference = new ArrayList<>();
        reference.add(a);
        reference.add(b);
        reference.add(c);

        // TODO make a copy of newick parser and modify for complex entities

//        BinaryTreeNode<String> template = NewickParser.parseNewick("((A:B):(A:B))", ':');
//
//        LeafFirstBinaryTreeIterator<String> iterator = new LeafFirstBinaryTreeIterator<>(template);
//
//        ComplexEntity tree = new ComplexEntity();
//
//        while (iterator.hasNext()) {
//            BinaryTreeNode<String> templateNode = iterator.next();
//            if (templateNode.isLeaf()) {
//                tree.append(get(reference, templateNode.getData()));
//            } else {
//                if (tree.hasLeft() && tree.hasRight()) {
//                    ComplexEntity temp = ComplexEntity.from(tree.getLeft().getData(), tree.getRight().getData());
//                    if (iterator.hasNext()) {
//                        tree = new ComplexEntity();
//                        tree.append((ChemicalEntity) temp);
//                    }
//                }
//            }
//
//        }
//
//        tree.print();

    }

//    private ChemicalEntity get(List<ChemicalEntity> reference, String identifier) {
//        for (ChemicalEntity chemicalEntity : reference) {
//            if (chemicalEntity.getIdentifier().getContent().equals(identifier)) {
//                return chemicalEntity;
//            }
//        }
//        return null;
//    }

    @Test
    @DisplayName("complex entity - add part modification")
    void addPart() {
        // create test tree
        ChemicalEntity akap = new Protein.Builder("AKAP").build();
        ChemicalEntity pkac = new Protein.Builder("PKAC").build();
        ChemicalEntity pkar = new Protein.Builder("PKAR").build();
        ChemicalEntity camp = SmallMolecule.create("CAMP").build();
        // create complexes
        ComplexEntity pka = ComplexEntity.from(pkac, pkar);
        ComplexEntity complex = ComplexEntity.from(akap, pka);
        // apply modification
        ComplexModification modification = new ComplexModification(ADD, camp, pkar);
        ComplexEntity campComplex = complex.apply(modification);
        // assert
        assertNotNull(campComplex.find(ComplexEntity.from(pkar, camp)));
    }

    @Test
    @DisplayName("complex entity - replace part modification")
    void replacePart() {
        // create test tree
        ChemicalEntity akap = new Protein.Builder("AKAP").build();
        ChemicalEntity pkac = new Protein.Builder("PKAC").build();
        ChemicalEntity pkar = new Protein.Builder("PKAR").build();
        // create complexes
        ComplexEntity pka = ComplexEntity.from(pkac, pkar);
        ComplexEntity complex = ComplexEntity.from(akap, pka);
        // apply modification
        ComplexModification modification = new ComplexModification(REPLACE, pkar, pka);
        ComplexEntity reducedComplex = complex.apply(modification);
        // assert removal
        assertNull(reducedComplex.find(pka));
        assertNotNull(reducedComplex.find(akap));
        assertNotNull(reducedComplex.find(pkar));
    }

    @Test
    @DisplayName("complex entity - remove part modification")
    void removePart() {
        // create test tree
        ChemicalEntity akap = new Protein.Builder("AKAP").build();
        ChemicalEntity pkac = new Protein.Builder("PKAC").build();
        ChemicalEntity pkar = new Protein.Builder("PKAR").build();
        // create complexes
        ComplexEntity pka = ComplexEntity.from(pkac, pkar);
        ComplexEntity complex = ComplexEntity.from(akap, pka);
        // apply modification
        ComplexModification modification = new ComplexModification(REMOVE, pkar, pka);
        ComplexEntity reducedComplex = complex.apply(modification);
        // assert removal
        assertNull(reducedComplex.find(pkar));
        assertNotNull(reducedComplex.find(pkac));
    }

}