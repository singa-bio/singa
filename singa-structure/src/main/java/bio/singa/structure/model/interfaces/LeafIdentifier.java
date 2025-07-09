package bio.singa.structure.model.interfaces;

import bio.singa.structure.model.general.LabelLeafIdentifier;
import bio.singa.structure.model.general.AuthLeafIdentifier;

import java.util.Comparator;

public interface LeafIdentifier extends Comparable<LeafIdentifier> {

    String DEFAULT_PDB_IDENTIFIER = "0000";

    Comparator<LeafIdentifier> LEAF_IDENTIFIER_COMPARATOR = Comparator
            .comparing(LeafIdentifier::getModelIdentifier)
            .thenComparing(LeafIdentifier::getChainIdentifier)
            .thenComparing(LeafIdentifier::getSerial)
            .thenComparing(LeafIdentifier::getInsertionCode);

    static LeafIdentifier fromString(String leafIdentifierString) {
        if (leafIdentifierString.startsWith(AuthLeafIdentifier.AUTH_IDENTIFIER_PREFIX)) {
            return AuthLeafIdentifier.fromString(leafIdentifierString.substring(AuthLeafIdentifier.AUTH_IDENTIFIER_PREFIX.length() + 1));
        } else if (leafIdentifierString.startsWith(LabelLeafIdentifier.LABEL_IDENTIFIER_PREFIX)) {
            return LabelLeafIdentifier.fromString(leafIdentifierString.substring(LabelLeafIdentifier.LABEL_IDENTIFIER_PREFIX.length() + 1));
        } else {
            throw new IllegalArgumentException("Leaf identifiers have to start with " + AuthLeafIdentifier.AUTH_IDENTIFIER_PREFIX + " or " + LabelLeafIdentifier.LABEL_IDENTIFIER_PREFIX + " prefix.");
        }
    }

    String getStructureIdentifier();

    int getModelIdentifier();

    String getChainIdentifier();

    int getSerial();

    char getInsertionCode();

    boolean hasInsertionCode();

    static LabelModelStep label() {
        return new LabelIdentifierBuilder();
    }

    interface LabelStructureStep {

        LabelModelStep structure(String structureIdentifier);

    }

    interface LabelModelStep extends LabelStructureStep {

        LabelChainStep model(int modelIdentifier);

    }

    interface LabelChainStep {

        LabelSerialStep chain(String chainIdentifier);

    }

    interface LabelSerialStep {

        LabelLeafIdentifier serial(int serial);

    }

    class LabelIdentifierBuilder implements LabelStructureStep, LabelModelStep, LabelChainStep, LabelSerialStep {

        private String structureIdentifier = DEFAULT_PDB_IDENTIFIER;
        private int modelIdentifier;
        private String chainIdentifier;

        public LabelModelStep structure(String structureIdentifier) {
            this.structureIdentifier = structureIdentifier;
            return this;
        }

        public LabelChainStep model(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            return this;
        }

        public LabelSerialStep chain(String chainIdentifier) {
            this.chainIdentifier = chainIdentifier;
            return this;
        }

        public LabelLeafIdentifier serial(int serialIdentifier) {
            return new LabelLeafIdentifier(structureIdentifier, modelIdentifier, chainIdentifier, serialIdentifier);
        }

    }

    static AuthModelStep auth() {
        return new AuthIdentifierBuilder();
    }

    interface AuthStructureStep {

        AuthModelStep structure(String structureIdentifier);

    }

    interface AuthModelStep extends AuthStructureStep {

        AuthChainStep model(int modelIdentifier);

    }

    interface AuthChainStep {

        AuthSerialStep chain(String chainIdentifier);

    }

    interface AuthSerialStep {

        AuthInsertionCodeStep serial(int serial);

    }

    interface AuthInsertionCodeStep {

        AuthLeafIdentifier noInsertionCode();

        AuthLeafIdentifier insertionCode(char insertionCode);

    }

    class AuthIdentifierBuilder implements AuthStructureStep, AuthModelStep, AuthChainStep, AuthSerialStep, AuthInsertionCodeStep {

        private String structureIdentifier = DEFAULT_PDB_IDENTIFIER;
        private int modelIdentifier;
        private String chainIdentifier;
        private int serialIdentifier;

        public AuthModelStep structure(String structureIdentifier) {
            this.structureIdentifier = structureIdentifier;
            return this;
        }

        public AuthChainStep model(int modelIdentifier) {
            this.modelIdentifier = modelIdentifier;
            return this;
        }

        public AuthSerialStep chain(String chainIdentifier) {
            this.chainIdentifier = chainIdentifier;
            return this;
        }

        public AuthInsertionCodeStep serial(int serialIdentifier) {
            this.serialIdentifier = serialIdentifier;
            return this;
        }

        @Override
        public AuthLeafIdentifier noInsertionCode() {
            return insertionCode(AuthLeafIdentifier.DEFAULT_INSERTION_CODE);
        }

        @Override
        public AuthLeafIdentifier insertionCode(char insertionCode) {
            return new AuthLeafIdentifier(structureIdentifier, modelIdentifier, chainIdentifier, serialIdentifier, insertionCode);
        }
    }

}
