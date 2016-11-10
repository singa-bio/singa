package de.bioforscher.chemistry.parser.pdb;

import de.bioforscher.chemistry.physical.model.Structure;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 30/10/2016.
 */
public class PDBParserService {

    public static final String PDB_FETCH_URL = "https://files.rcsb.org/download/%s.pdb";

    public static Structure parseProteinById(String pdbId) throws IOException {
        return parsePDBFile(new URL(String.format(PDB_FETCH_URL, pdbId)).openStream());
    }

    public static Structure parsePDBFile(String filepath) throws IOException {
        return parsePDBFile(new File(filepath));
    }

    public static Structure parsePDBFile(File pdbFile) throws IOException {
        try (InputStream inputStream = Files.newInputStream(pdbFile.toPath())) {
            return parsePDBFile(inputStream);
        }
    }

    public static Structure parsePDBFile(InputStream inputStream) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return StructureAssembler.assembleStructure(bufferedReader.lines().collect(Collectors.toList()));
            }
        }
    }

}
