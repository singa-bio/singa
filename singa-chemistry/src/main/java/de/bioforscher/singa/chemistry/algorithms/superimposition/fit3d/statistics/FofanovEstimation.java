package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d.statistics;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.mathematics.vectors.RegularVector;
import de.bioforscher.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * An estimation method of the statistical significance of matches produced by the Fit3D algorithm. According to:
 * <pre>
 *     Fofanov, V. et al.
 *     A statistical model to correct systematic bias introduced by algorithmic thresholds in protein structural
 *     comparison algorithms.
 *     Bioinformatics and Biomedicine Workshops, 2008. BIBMW 2008. IEEE International Conference on, 2008, 1-8
 * </pre>
 *
 * @author fk
 */
public class FofanovEstimation implements StatisticalModel {

    /**
     * The number of chains in the BLASTe-80 nrpdb dataset according to VAST as of 2017-10-17.
     */
    public static final int DEFAULT_REFERENCE_SIZE = 39592;

    private static final String BINARY_NAME = "Rscript";
    private static final double START_RMSD = 0.0;
    private static final double SAMPLE_SIZE = 10000;
    private static Logger logger = LoggerFactory.getLogger(FofanovEstimation.class);

    private int referenceSize = DEFAULT_REFERENCE_SIZE;
    private double rmsdCutoff;

    private AtomicInteger gs;
    private AtomicInteger ns;

    private Path temporaryDirectoryPath;
    private Path scriptPath;
    private Path rmsdValuesPath;
    private Path pvaluesPath;
    private Vector pvalues;
    private TreeMap<Double, SubstructureSuperimposition> matches;
    private TreeMap<Double, SubstructureSuperimposition> pvalueMatches;

    public FofanovEstimation(double rmsdCutoff, int referenceSize) {
        this.rmsdCutoff = rmsdCutoff;
        this.referenceSize = referenceSize;
    }

    public FofanovEstimation(double rmsdCutoff) {
        checkRequirements();
        this.rmsdCutoff = rmsdCutoff;
        this.gs = new AtomicInteger(0);
        this.ns = new AtomicInteger(0);
    }

    public TreeMap<Double, SubstructureSuperimposition> getPvalueMatches() {
        if (this.pvalueMatches == null) {
            throw new UnsupportedOperationException("p-values are not available");
        }
        return this.pvalueMatches;
    }

    private void checkRequirements() {
        try {
            Runtime.getRuntime().exec(BINARY_NAME);
        } catch (IOException e) {
            throw new UnsupportedOperationException("required binary Rscript must be installed to use the Fofanov statistical model");
        }
    }


    public void incrementNs() {
        this.ns.incrementAndGet();
    }

    public void incrementGs() {
        this.gs.incrementAndGet();
    }

    @Override
    public void calculatePvalues(TreeMap<Double, SubstructureSuperimposition> matches) throws IOException, InterruptedException {
        this.matches = matches;
        this.pvalueMatches = new TreeMap<>();
        createTemporaryDirectory();
        writeRmsdValues();
        runScript();
        int counter = 0;
        for (Map.Entry<Double, SubstructureSuperimposition> entry : matches.entrySet()) {
            this.pvalueMatches.put(this.pvalues.getElement(counter), entry.getValue());
            counter++;
        }
    }

    private void writeRmsdValues() throws IOException {
        NumberFormat nf = NumberFormat.getInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.0000");
        String formattedRmsdValues = this.matches.keySet().stream()
                .map(df::format)
                .collect(Collectors.joining("\n", "rmsd\n", ""));
        Files.write(this.rmsdValuesPath,
                formattedRmsdValues.getBytes());
        logger.info("rmsd values written to {}", this.rmsdValuesPath);
    }

    private void runScript() throws IOException, InterruptedException {
        logger.info("computing p-values by calling external R script");
        this.pvaluesPath = this.temporaryDirectoryPath.resolve("pvalues.csv");
        ProcessBuilder processBuilder = new ProcessBuilder(BINARY_NAME,
                this.scriptPath.toString(),
                String.valueOf(this.referenceSize),
                String.valueOf(this.ns.get()),
                String.valueOf(this.gs.get()),
                String.valueOf(START_RMSD),
                String.valueOf(this.rmsdCutoff),
                String.valueOf(SAMPLE_SIZE),
                this.rmsdValuesPath.toString(),
                this.pvaluesPath.toString());
        Process process;
        if (logger.isDebugEnabled()) {
            process = processBuilder.inheritIO().start();
        } else {
            process = processBuilder.start();
        }
        int exitStatus = process.waitFor();
        if (exitStatus != 0) {
            logger.error("p-value calculation failed");
            throw new RuntimeException("p-value calculation ended with exit status: " + exitStatus);
        }

        logger.info("p-value calculation successful");

        double[] pvalues = Files.readAllLines(this.pvaluesPath).stream()
                .map(Double::valueOf)
                .mapToDouble(Double::doubleValue).toArray();

        this.pvalues = new RegularVector(pvalues);
    }

    private void createTemporaryDirectory() throws IOException {
        this.temporaryDirectoryPath = Files.createTempDirectory("fit3d_");
        this.rmsdValuesPath = this.temporaryDirectoryPath.resolve("rmsd.csv");
        InputStream resourceAsStream = Resources.getResourceAsStream("de/bioforscher/singa/chemistry/algorithms/superimposition/fit3d/statistics/fofanov.R");
        this.scriptPath = this.temporaryDirectoryPath.resolve("fofanov.R");
        Files.copy(resourceAsStream, this.scriptPath);
        logger.debug("script fofanov.R copied to {}", this.scriptPath);
    }
}
