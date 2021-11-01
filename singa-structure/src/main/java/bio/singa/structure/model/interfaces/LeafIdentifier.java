package bio.singa.structure.model.interfaces;

import bio.singa.structure.model.cif.CifLeafIdentifier;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;

import java.util.Comparator;

public interface LeafIdentifier extends Comparable<LeafIdentifier> {

    Comparator<LeafIdentifier> LEAF_IDENTIFIER_COMPARATOR = Comparator
            .comparing(LeafIdentifier::getStructureIdentifier)
            .thenComparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifier)
            .thenComparing(LeafIdentifier::getSerial)
            .thenComparing(LeafIdentifier::getInsertionCode);

    static LeafIdentifier fromString(String leafIdentifierString) {
        if (leafIdentifierString.startsWith("PDB")) {
            return PdbLeafIdentifier.fromString(leafIdentifierString);
        } else if (leafIdentifierString.startsWith("CIF")) {
            return CifLeafIdentifier.fromString(leafIdentifierString);
        } else {
            throw new IllegalArgumentException("Leaf identifiers have to start with PDB of CIF prefix.");
        }
    }

    String getStructureIdentifier();

    int getModelIdentifier();

    String getChainIdentifier();

    int getSerial();

    char getInsertionCode();

    boolean hasInsertionCode();

}
