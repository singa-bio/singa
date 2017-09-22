package de.bioforscher.singa.mmtf;

import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;

/**
 * @author fk
 */
public class MmtfPlayground {

    public static void main(String[] args) throws IOException {

        // Get the message pack byte array from the web by PDB code
        MmtfStructure mmtfData = ReaderUtils.getDataFromUrl("2N5E");

        // Decode message pack byte array as flat arrays holding the structural data
        StructureDataInterface dataInterface = new GenericDecoder(mmtfData);

//        MmtfChain chain = new MmtfChain(dataInterface, "A");
//        List<LeafSubstructure<?>> leafSubstructures = chain.getAllLeafSubstructures();
//        for (LeafSubstructure<?> leafSubstructure : leafSubstructures) {
//            for (Atom atom : leafSubstructure.getAllAtoms()) {
//                System.out.println(leafSubstructure.getThreeLetterCode()+" - "+atom.getAtomName()+" : "+atom.getPosition());
//                atom.getElement();
//            }
//        }


    }
}