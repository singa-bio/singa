package bio.singa.structure.algorithms.superimposition.fit3d.statistics;

import bio.singa.core.utility.Resources;
import bio.singa.mathematics.vectors.RegularVector;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3D;
import bio.singa.structure.algorithms.superimposition.fit3d.Fit3DMatch;
import bio.singa.structure.model.oak.StructuralMotif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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
    private static final Logger logger = LoggerFactory.getLogger(FofanovEstimation.class);
    private static final String BINARY_NAME = "Rscript";
    private static final double START_RMSD = 0.0;
    private static final double SAMPLE_SIZE = 100000;

    private final double rmsdCutoff;
    private final AtomicInteger gs;
    private final AtomicInteger ns;
    private final double modelCorrectnessCutoff;

    private int referenceSize = DEFAULT_REFERENCE_SIZE;
    private Path temporaryDirectoryPath;
    private Path scriptPath;
    private Path rmsdValuesPath;
    private Path pvaluesPath;
    private Vector pvalues;
    private List<Fit3DMatch> matches;

    public FofanovEstimation(double rmsdCutoff) {
        this(rmsdCutoff, DEFAULT_REFERENCE_SIZE, rmsdCutoff);
    }

    public FofanovEstimation(double rmsdCutoff, int referenceSize, double modelCorrectnessCutoff) {
        checkRequirements();
        this.rmsdCutoff = rmsdCutoff;
        this.referenceSize = referenceSize;
        this.modelCorrectnessCutoff = modelCorrectnessCutoff;
        gs = new AtomicInteger(0);
        ns = new AtomicInteger(0);
    }

    /**
     * This calculates the cutoff epsilon up to which the RMSD distribution should be sampled for a desired model
     * correctness.
     * <pre>
     * epsilon = model_correctness * sqrt(n_atoms(query))
     * </pre>
     *
     * @param queryMotif The {@link StructuralMotif} used as query for the {@link Fit3D} alignment.
     * @param modelCorrectnessCutoff The desired model correctness.
     * @return The epsilon up to which RMSD distribution should be sampled to guarantee model correctness (the RMSD
     * cutoff that should be used for {@link Fit3D}).
     */
    public static double determineEpsilon(StructuralMotif queryMotif, double modelCorrectnessCutoff) {
        int numberOfAtoms = queryMotif.getAllAtoms().size();
        return determineEpsilon(numberOfAtoms, modelCorrectnessCutoff);
    }

    /**
     * This calculates the cutoff epsilon up to which the RMSD distribution should be sampled for a desired model
     * correctness.
     * <pre>
     * epsilon = model_correctness * sqrt(n_atoms(query))
     * </pre>
     *
     * @param numberOfAtoms The number of atoms used to represent the query for the {@link Fit3D} alignment.
     * @param modelCorrectnessCutoff The desired model correctness.
     * @return The epsilon up to which RMSD distribution should be sampled to guarantee model correctness (the RMSD
     * cutoff that should be used for {@link Fit3D}).
     */
    public static double determineEpsilon(int numberOfAtoms, double modelCorrectnessCutoff) {
        return modelCorrectnessCutoff * Math.sqrt(numberOfAtoms);
    }

    public double getModelCorrectnessCutoff() {
        return modelCorrectnessCutoff;
    }

    private void checkRequirements() {
        try {
            Runtime.getRuntime().exec(BINARY_NAME);
        } catch (IOException e) {
            throw new UnsupportedOperationException("required binary Rscript must be installed to use the Fofanov statistical model");
        }
    }


    public void incrementNs() {
        ns.incrementAndGet();
    }

    public void incrementGs() {
        gs.incrementAndGet();
    }

    @Override
    public void calculatePvalues(List<Fit3DMatch> matches) throws IOException, InterruptedException {
        this.matches = matches;
        createTemporaryDirectory();
        writeRmsdValues();
        runScript();
        for (int i = 0; i < matches.size(); i++) {
            Fit3DMatch match = matches.get(i);
            if (match.getRmsd() > modelCorrectnessCutoff) {
                match.setPvalue(Double.NaN);
                continue;
            }
            match.setPvalue(pvalues.getElement(i));
        }
    }

    private void writeRmsdValues() throws IOException {
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("0.0000");
        String formattedRmsdValues = matches.stream()
                .map(Fit3DMatch::getRmsd)
                .map(df::format)
                .collect(Collectors.joining("\n", "rmsd\n", ""));
        Files.write(rmsdValuesPath,
                formattedRmsdValues.getBytes());
        logger.info("rmsd values written to {}", rmsdValuesPath);
    }

    private void runScript() throws IOException, InterruptedException {
        logger.info("computing p-values by calling external R script");
        pvaluesPath = temporaryDirectoryPath.resolve("pvalues.csv");
        ProcessBuilder processBuilder = new ProcessBuilder(BINARY_NAME,
                scriptPath.toString(),
                String.valueOf(referenceSize),
                String.valueOf(ns.get()),
                String.valueOf(gs.get()),
                String.valueOf(START_RMSD),
                String.valueOf(rmsdCutoff),
                String.valueOf(modelCorrectnessCutoff),
                String.valueOf(SAMPLE_SIZE),
                rmsdValuesPath.toString(),
                pvaluesPath.toString());
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

        double[] pvalues = Files.readAllLines(pvaluesPath).stream()
                .map(Double::valueOf)
                .mapToDouble(Double::doubleValue).toArray();

        this.pvalues = new RegularVector(pvalues);
    }

    private void createTemporaryDirectory() throws IOException {
        temporaryDirectoryPath = Files.createTempDirectory("fit3d_");
        rmsdValuesPath = temporaryDirectoryPath.resolve("rmsd.csv");
        InputStream resourceAsStream = Resources.getResourceAsStream("bio/singa/structure/algorithms/superimposition/fit3d/statistics/fofanov.R");
        scriptPath = temporaryDirectoryPath.resolve("fofanov.R");
        Files.copy(resourceAsStream, scriptPath);
        logger.debug("script fofanov.R copied to {}", scriptPath);
    }
}
