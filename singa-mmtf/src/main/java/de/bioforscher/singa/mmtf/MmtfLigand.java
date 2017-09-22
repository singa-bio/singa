package de.bioforscher.singa.mmtf;

import de.bioforscher.singa.chemistry.physical.interfaces.Ligand;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import org.rcsb.mmtf.api.StructureDataInterface;

/**
 * @author cl
 */
public class MmtfLigand extends MmtfLeafSubstructure<Ligand> implements Ligand {

    public MmtfLigand(StructureDataInterface data, LeafIdentifier leafIdentifier, int internalIndex, int atomStartIndex, int atomEndIndex) {
        super(data, leafIdentifier, internalIndex, atomStartIndex, atomEndIndex);
    }

    @Override
    public String flatToString() {
        return "Ligand ("+getThreeLetterCode()+") "+getIdentifier();
    }

    @Override
    public String toString() {
        return flatToString();
    }

}
