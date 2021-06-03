package bio.singa.structure.parser.pdb.rest.cluster;

import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.structure.parser.pdb.rest.PDBRestEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link PDBRestEndpoint} that uses the PDB REST API to obtain the sequence cluster for a given chain.
 *
 * @author fk
 */
public class PDBSequenceCluster extends PDBRestEndpoint {

    private static final Pattern LINE_PATTERN = Pattern.compile("<pdbChain name=\"([0-9A-Z.]+)\" rank=\"(\\d+)\" />");
    private static final Logger logger = LoggerFactory.getLogger(PDBSequenceCluster.class);
    private static final String SEQUENCE_CLUSTER_ENDPOINT = "http://www.rcsb.org/pdb/rest/sequenceCluster?";
    private final PDBIdentifier pdbIdentifier;
    private final String chainIdentifier;
    private final PDBSequenceClusterIdentity identity;
    private List<PDBSequenceClusterMember> clusterMembers;

    public PDBSequenceCluster(String pdbIdentifier, String chainIdentifier, PDBSequenceClusterIdentity identity) {
        this.pdbIdentifier = new PDBIdentifier(pdbIdentifier);
        this.chainIdentifier = chainIdentifier;
        this.identity = identity;
        clusterMembers = new ArrayList<>();
        logger.info("calling PDB REST to obtain sequence cluster {}/{}", pdbIdentifier, chainIdentifier);
    }

    public static PDBSequenceCluster of(String pdbIdentifier, String chainIdentifier, PDBSequenceClusterIdentity identity) {
        return new PDBSequenceCluster(pdbIdentifier, chainIdentifier, identity).parse();
    }

    public List<PDBSequenceClusterMember> getClusterMembers() {
        return clusterMembers;
    }

    @Override
    public PDBSequenceCluster parse() {

        setResource(getEndpoint());
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("cluster", String.valueOf(identity.getIdentity()));
        parameterMap.put("structureId", pdbIdentifier.getContent() + "." + chainIdentifier);
        fetchWithQuery(parameterMap);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getFetchResult()));
            // store already used ranks to avoid mining of homomeric structures
            Set<Integer> ranks = new TreeSet<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                Matcher matcher = LINE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String chainSpecification = matcher.group(1);
                    String pdbIdentifier = chainSpecification.split("\\.")[0].toLowerCase();
                    String chainIdentifier = chainSpecification.split("\\.")[1];
                    int rank = Integer.parseInt(matcher.group(2));
                    if (!ranks.contains(rank)) {
                        clusterMembers.add(new PDBSequenceClusterMember(rank, new PDBIdentifier(pdbIdentifier), chainIdentifier));
                    } else {
                        logger.debug("ignored duplicate homomeric entry {}_{}", pdbIdentifier, chainIdentifier);
                    }
                    ranks.add(rank);
                }
            }
            if (clusterMembers.isEmpty()) {
                throw new IllegalArgumentException("failed to obtain PDB sequence clusters for " + pdbIdentifier + "/" + chainIdentifier);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to parse results for chain " + pdbIdentifier + "." + chainIdentifier, e);
        }
        return this;
    }

    @Override
    protected String getEndpoint() {
        return SEQUENCE_CLUSTER_ENDPOINT;
    }

    /**
     * Returns the representative for this cluster as an array containing the PDB-ID and the chain-ID.
     *
     * @return The representative chain of this cluster.
     */
    public String[] getRepresentative() {
        if (!clusterMembers.isEmpty()) {
            PDBSequenceClusterMember representativeMember = clusterMembers.get(0);
            return new String[]{representativeMember.getPdbIdentifier().getContent(), representativeMember.getChainIdentifier()};
        }
        throw new UnsupportedOperationException("Cannot retrieve representative member if no members are defined.");
    }

    public enum PDBSequenceClusterIdentity {
        IDENTITY_100, IDENTITY_95, IDENTITY_90, IDENTITY_70, IDENTITY_50, IDENTITY_40, IDENTITY_30;

        public int getIdentity() {
            return Integer.parseInt(name().split("_")[1]);
        }
    }
}
