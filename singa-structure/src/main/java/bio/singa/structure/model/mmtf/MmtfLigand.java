package bio.singa.structure.model.mmtf;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Ligand;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

import java.util.HashSet;

/**
 * The implementation of {@link Ligand}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfLigand extends MmtfLeafSubstructure implements Ligand {

    /**
     * Creates a new {@link MmtfLigand}.
     *
     * @param data The original data.
     * @param family The leaf family.
     * @param leafIdentifier The leaf identifier.
     * @param internalGroupIndex The index of this leaf in the data array.
     * @param atomStartIndex The index of the first atom that belong to this leaf.
     * @param atomEndIndex The index of the last atom that belong to this leaf.
     */
    MmtfLigand(StructureDataInterface data, byte[] bytes, StructuralFamily family, PdbLeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, bytes, family, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    /**
     * A copy constructor that passes all attributes of the given {@link MmtfLeafSubstructure} to a new instance.
     *
     * @param mmtfLeafSubstructure The {@link MmtfLeafSubstructure} to copy.
     */
    private MmtfLigand(MmtfLigand mmtfLeafSubstructure) {
        super(mmtfLeafSubstructure);
        family = mmtfLeafSubstructure.family;
    }

    @Override
    public boolean isAnnotatedAsHeteroAtom() {
        return false;
    }

    @Override
    public Ligand getCopy() {
        return new MmtfLigand(this);
    }
}
