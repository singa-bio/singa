package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.physical.model.*;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by leberech on 16/12/16.
 */
public class StructuralMotif extends BranchSubstructure<StructuralMotif> {

    public StructuralMotif(int identifier) {
        super(identifier);
    }

    public StructuralMotif(StructuralMotif branchSubstructure) {
        super(branchSubstructure);
    }

    public static StructuralMotif fromLeafs(int identifier, Structure structure, List<LeafIdentifier> leafIdentifiers) {
        StructuralMotif motif = new StructuralMotif(identifier);
        leafIdentifiers.forEach(leafIdentifer -> {
            Substructure subStructure = structure.getAllChains().stream()
                    .filter(StructureFilter.isInChain(leafIdentifer.getChainIdentifer()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .getSubstructure(leafIdentifer.getLeafIdentifer())
                    .orElseThrow(NoSuchElementException::new);
            motif.addSubstructure(subStructure);
        });
        return motif;
    }

    /**
     * Returns the size of the structural motif (the number of contained
     * {@link de.bioforscher.chemistry.physical.leafes.LeafSubstructure}s.
     *
     * @return The size of the motif.
     */
    public int size() {
        return getLeafSubstructures().size();
    }

    /**
     * FIXME: here we have to find a nice solution to generify definition od exchanges
     *
     * @param leafIdentifier
     * @param exchangeableType
     */
    public void addExchangableType(LeafIdentifier leafIdentifier, StructuralFamily exchangeableType) {
        getSubstructure(leafIdentifier.getLeafIdentifer())
                .map(Exchangeable.class::cast)
                .orElseThrow(NoSuchElementException::new)
                .addExchangeableType(exchangeableType);
    }

    @Override
    public StructuralMotif getCopy() {
        return new StructuralMotif(this);
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new NotImplementedException();
    }
}
