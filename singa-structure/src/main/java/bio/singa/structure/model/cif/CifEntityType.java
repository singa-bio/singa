package bio.singa.structure.model.cif;

import java.util.Optional;

/**
 * https://mmcif.wwpdb.org/dictionaries/mmcif_pdbx_v50.dic/Items/_entity.type.html
 */
public enum CifEntityType {

    BRANCHED("branched"),
    MACROLIDE("macrolide"),
    POLYMER("polymer"),
    NON_POLYMER("non-polymer"),
    WATER("water");

    private final String typeString;

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
