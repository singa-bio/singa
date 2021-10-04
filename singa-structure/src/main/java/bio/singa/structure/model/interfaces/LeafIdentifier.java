package bio.singa.structure.model.interfaces;

import java.util.Comparator;

public interface LeafIdentifier extends Comparable<LeafIdentifier> {

    Comparator<LeafIdentifier> LEAF_IDENTIFIER_COMPARATOR = Comparator
            .comparing(LeafIdentifier::getStructureIdentifier)
            .thenComparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifier)
            .thenComparing(LeafIdentifier::getSerial)
            .thenComparing(LeafIdentifier::getInsertionCode);

    String getStructureIdentifier();

    int getModelIdentifier();

    String getChainIdentifier();

    int getSerial();

    char getInsertionCode();

    boolean hasInsertionCode();

}
