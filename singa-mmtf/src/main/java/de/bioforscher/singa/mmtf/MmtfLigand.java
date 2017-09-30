package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Ligand;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * The implementation of {@link Ligand}s for mmtf structures.
 *
 * @author cl
 */
public class MmtfLigand extends MmtfLeafSubstructure implements Ligand {

    MmtfLigand(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalGroupIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalGroupIndex, atomStartIndex, atomEndIndex);
    }

    private MmtfLigand(MmtfLeafSubstructure mmtfLeafSubstructure) {
        super(mmtfLeafSubstructure);
    }

    @Override
    public String flatToString() {
        return "Ligand ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

    @Override
    public Ligand getCopy() {
        return new MmtfLigand(this);
    }
}
