package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.chemistry.physical.model.StructurePredicates;
import de.bioforscher.chemistry.physical.model.Substructure;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;

import java.util.List;

/**
 * Created by leberech on 16/12/16.
 */
public class StructuralMotif extends BranchSubstructure<StructuralMotif> {

    public static StructuralMotif fromLeafsInStructure(int identifier, Structure structure, List<LeafIdentifier> leafIdentifiers) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafIdentifiers.forEach(leafIdentifer -> {
            Substructure subStructure = structure.getAllChains().stream()
                    .filter(StructurePredicates.isInChain(leafIdentifer.getChainIdentifer()))
                    .findFirst().orElseThrow(IllegalArgumentException::new)
                    .getSubStructure(leafIdentifer.getLeafIdentifer());
            motif.addSubstructure(subStructure);
        });
        return motif;
    }

    public StructuralMotif(int identifier) {
        super(identifier);
    }

    public StructuralMotif(StructuralMotif branchSubstructure) {
        super(branchSubstructure);
    }

    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }


}
