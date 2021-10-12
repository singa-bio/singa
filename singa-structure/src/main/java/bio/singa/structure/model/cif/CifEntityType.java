package bio.singa.structure.model.cif;

import java.util.Optional;

public enum CifEntityType {

    POLYMER("polymer"),
    NON_POLYMER("non-polymer"),
    WATER("water");

    private String typeString;

    CifEntityType(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }

    public static Optional<CifEntityType> getTypeForString(String typeString) {
        for (CifEntityType type : values()) {
            if (type.getTypeString().equals(typeString)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
