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
            return PdbLeafIdentifier.fromString(leafIdentifierString.substring(4));
        } else if (leafIdentifierString.startsWith("CIF")) {
            return CifLeafIdentifier.fromString(leafIdentifierString.substring(4));
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

    static CifStructureStep cif() {
        return new CifIdentifierBuilder();
    }

    interface CifStructureStep {

        CifEntityStep structure(String structureIdentifier);

    }

    interface CifEntityStep {

        CifModelStep entity(int entityIdentifier);

    }

    interface CifModelStep {

        CifChainStep model(int modelIdentifier);

    }

    interface CifChainStep {

        CifSerialStep chain(String chainIdentifier);

    }

    interface CifSerialStep {

        CifLeafIdentifier serial(int serial);

    }

    class CifIdentifierBuilder implements CifStructureStep, CifEntityStep, CifModelStep, CifChainStep, CifSerialStep {

        private String structureIdentifier;
        private int entityIdentifier;
        private int modelIdentifier;
        private String chainIdentifier;

        public CifEntityStep structure(String structureIdentifier) {
            this.structureIdentifier = structureIdentifier;
            return this;
        }

        public CifModelStep entity(int entityIdentifier) {
            this.entityIdentifier = entityIdentifier;
            return this;
        }

        public CifChainStep model(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            return this;
        }

        public CifSerialStep chain(String chainIdentifier) {
            this.chainIdentifier = chainIdentifier;
            return this;
        }

        public CifLeafIdentifier serial(int serialIdentifier) {
            return new CifLeafIdentifier(structureIdentifier, entityIdentifier, modelIdentifier, chainIdentifier, serialIdentifier);
        }

    }

}
