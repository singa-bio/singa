package de.bioforscher.singa.mmtf;

import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author fk
 */
public class MmtfStructuree implements StructureInterface {

    private StructureDataInterface data;

    @Override
    public String getPdbIdentifier() {
        return data.getStructureId();
    }

    @Override
    public String getTitle() {
        return data.getTitle();
    }

    @Override
    public ChainInterface getChain(String chainIdentifier) {
        return null; // new MmtfChain(data);
    }

    public static void main(String[] args) throws IOException {

        // Get the message pack byte array from the web by PDB code
        MmtfStructure mmtfData = ReaderUtils.getDataFromUrl("1pqs");

        // Decode message pack byte array as flat arrays holding the structural data
        StructureDataInterface dataInterface = new GenericDecoder(mmtfData);

        MmtfChain chain = new MmtfChain(dataInterface, "A");
        List<LeafSubstructureInterface> leafSubstructures = chain.getLeafSubstructures();
        for (LeafSubstructureInterface leafSubstructure : leafSubstructures) {
            for (AtomInterface atomInterface : leafSubstructure.getAtoms()) {
                System.out.println(leafSubstructure.getThreeLetterCode()+" - "+atomInterface.getAtomName()+" : "+atomInterface.getPosition());
            }
        }


    }
}