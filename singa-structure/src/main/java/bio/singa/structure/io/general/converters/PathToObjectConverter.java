package bio.singa.structure.io.general.converters;

import bio.singa.structure.io.general.StructureParserException;
import org.rcsb.cif.CifIO;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author cl
 */
public class PathToObjectConverter implements ContentConverter<Path, Object> {

    private static final PathToObjectConverter instance = new PathToObjectConverter();


    private PathToObjectConverter() {

    }

    public static PathToObjectConverter get() {
        return instance;
    }

    @Override
    public Object convert(Path content) {
        String fileName = content.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".mmtf.gz")) {
            try {
                return Files.readAllBytes(content);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read mmtf " + content, e);
            }
        }

        if (fileName.endsWith(".ent.gz")) {
            try {
                return fetchLines(readPacked(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read packed pdb file" + content, e);
            }
        }

        if (fileName.endsWith(".pdb.gz")) {
            try {
                return fetchLines(readPacked(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read packed pdb file" + content, e);
            }
        }

        if (fileName.endsWith(".pdb")) {
            try {
                return fetchLines(Files.newInputStream(content));
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read pdb file" + content, e);
            }
        }

        if (fileName.endsWith(".bcif")) {
            try {
                return CifIO.readFromPath(content, CifStaticOptions.BCIF_PLAIN);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read bcif file" + content, e);
            }
        }

        if (fileName.endsWith(".bcif.gz")) {
            try {
                return CifIO.readFromPath(content, CifStaticOptions.BCIF_GZIPPED);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read bcif file" + content, e);
            }
        }

        if (fileName.endsWith(".cif")) {
            try {
                return CifIO.readFromPath(content, CifStaticOptions.CIF_PLAIN);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read bcif file" + content, e);
            }
        }

        if (fileName.endsWith(".cif.gz")) {
            try {
                return CifIO.readFromPath(content, CifStaticOptions.CIF_GZIPPED);
            } catch (IOException e) {
                throw new UncheckedIOException("unable to read bcif file" + content, e);
            }
        }

        throw new StructureParserException("Unable to determine file type of " + content);
    }

}
