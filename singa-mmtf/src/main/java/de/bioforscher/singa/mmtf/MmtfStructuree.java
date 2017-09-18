package de.bioforscher.singa.mmtf;

import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.GenericDecoder;
import org.rcsb.mmtf.decoder.ReaderUtils;

import java.io.IOException;

/**
 * @author fk
 */
public class MmtfStructuree {


    public static void main(String[] args) throws IOException {

        // Get the message pack byte array from the web by PDB code
        MmtfStructure mmtfData = ReaderUtils.getDataFromUrl("4CUP");

// Decode message pack byte array as flat arrays holding the structural data
        StructureDataInterface dataInterface = new GenericDecoder(mmtfData);
    }
}