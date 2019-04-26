package bio.singa.simulation.trajectories;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author cl
 */
public class Recorders {

    private Recorders() {

    }

    public static void createDirectories(Path currentVariationSetPath) {
        if (!Files.exists(currentVariationSetPath)) {
            try {
                Files.createDirectories(currentVariationSetPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to create directory " + currentVariationSetPath + " for current simulation variation.", e);
            }
        }
    }

    public static Path createFile(Path path, String fileName) {
        Path file = path.resolve(fileName);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to create file " + file + " for current simulation variation.", e);
            }
        }
        return file;
    }

    public static  Path appendTimestampedFolder(Path parentPath) {
        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'").format(date);
        return parentPath.resolve(timeStamp);
    }


}
