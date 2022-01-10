package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.rcsb.cif.model.FloatColumn;
import org.rcsb.cif.model.IntColumn;
import org.rcsb.cif.model.StrColumn;
import org.rcsb.cif.schema.mm.*;

import java.util.*;


public class CifConverter {

    private boolean createPdbReference;

    private final MmCifFile mmcifFile;
    private Map<Integer, CifEntity> entityMap;
    private Map<CifLeafIdentifier, PdbLeafIdentifier> pdbReferenceMap;

    private LeafSkeletonFactory leafSkeletonFactory;

    private CifStructure structure;
    private String pdbId;
    private boolean isMutated;

    public CifConverter(MmCifFile mmcifFile) {
        this(mmcifFile, false);
    }

    public CifConverter(MmCifFile mmcifFile, LeafSkeletonFactory leafSkeletonFactory) {
        this(mmcifFile);
        this.leafSkeletonFactory = leafSkeletonFactory;
    }

    public CifConverter(MmCifFile mmcifFile, boolean createPdbReference) {
        this.mmcifFile = mmcifFile;
        this.createPdbReference = createPdbReference;
        entityMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
    }

    public static CifStructure convert(MmCifFile cifFile, LeafSkeletonFactory leafSkeletonFactory) {
        CifConverter cifConverter = new CifConverter(cifFile, leafSkeletonFactory);
        return cifConverter.convert();
    }

    private void extractMetaData(MmCifBlock data) {
        // structure id
        pdbId = data.getEntry().getId().get(0);
        structure = new CifStructure(pdbId);

        // resolution
        StrColumn methodColumn = data.getExptl().getMethod();
        if (methodColumn.isDefined()) {
            String method = methodColumn.get(0);
            double resolution;
            if (method.equals("X-RAY DIFFRACTION")) {
                if (data.getReflns().isDefined()) {
                    resolution = data.getReflns().getDResolutionHigh().get(0);
                } else {
                    resolution = Double.NaN;
                }
            } else if (method.equals("")) {
                if (data.getEm3dReconstruction().isDefined()) {
                    resolution = data.getEm3dReconstruction().getResolution().get(0);
                } else {
                    resolution = Double.NaN;
                }
            } else {
                resolution = Double.NaN;
            }
            structure.setResolution(resolution);
        }
        PdbxStructAssemblyGen assemblyGen = data.getPdbxStructAssemblyGen();
        if (assemblyGen.isDefined()) {
            HashMap<String, List<String>> assemblies = new HashMap<>();
            StrColumn assemblyIdColumn = assemblyGen.getAssemblyId();
            StrColumn asymIdListColumn = assemblyGen.getAsymIdList();

            for (int row = 0; row < assemblyGen.getRowCount(); row++) {
                String assemblyId = assemblyIdColumn.get(row);
                List<String> assemblyChains = Arrays.asList(asymIdListColumn.get(row).split(","));
                assemblies.put(assemblyId, assemblyChains);
            }
            structure.setBiologicalAssemblies(assemblies);
        }

        // title
        Struct structColumn = data.getStruct();
        if (structColumn.isDefined()) {
            structure.setTitle(structColumn.getTitle().get(0));
        }

        // determine mutations
        // _struct_ref_seq_dif
        StructRefSeqDif structRefSeqDif = data.getStructRefSeqDif();
        if (!structRefSeqDif.isDefined()) {
            return;
        }
        StrColumn difDetails = structRefSeqDif.getDetails();
        for (int row = 0; row < structRefSeqDif.getRowCount(); row++) {
            if (difDetails.get(row).contains("mutation")) {
                structure.setMutated(true);
            }
        }
    }

    private CifStructure convert() {
        MmCifBlock data = mmcifFile.getFirstBlock();
        extractMetaData(data);
        extractEntityInformation(data);
        extractAtomInformation(data);
        if (createPdbReference) {
            extractPolymerReferenceInformation(data);
            extractNonPolymerReferenceInformation(data);
        }
        return structure;
    }

    private void extractAtomInformation(MmCifBlock data) {
        AtomSite atomSite = data.getAtomSite();
        // model
        IntColumn modelColumn = atomSite.getPdbxPDBModelNum();
        // entity id
        StrColumn entityIdColumn = atomSite.getLabelEntityId();
        // cif chain
        StrColumn chainColumn = atomSite.getLabelAsymId();
        // leaf serial
        IntColumn leafSerialColumn = atomSite.getLabelSeqId();
        // hetatom
        StrColumn groupPdbColumn = atomSite.getGroupPDB();
        // three letter code
        StrColumn threeLetterCodeColumn = atomSite.getLabelCompId();
        // alternative conformations
        StrColumn alternativePositionColumn = atomSite.getLabelAltId();

        // atom serial
        IntColumn atomSerialColumn = atomSite.getId();
        // element
        StrColumn elementColumn = atomSite.getTypeSymbol();
        // atom name
        StrColumn atomNameColumn = atomSite.getLabelAtomId();
        // x coordinate
        FloatColumn xCoordinateColumn = atomSite.getCartnX();
        // y coordinate
        FloatColumn yCoordinateColumn = atomSite.getCartnY();
        // z coordinate
        FloatColumn zCoordinateColumn = atomSite.getCartnZ();
        // b factor
        FloatColumn bFactorColumn = atomSite.getBIsoOrEquiv();

        Set<CifLeafSubstructure> leavesWithAlternativeConfirmations = new HashSet<>();

        for (int row = 0; row < atomSite.getRowCount(); row++) {

            // todo could possibly be more efficient by checking if the identifier changed compared to previous id
            String chainIdentifier = chainColumn.get(row);
            int cifSerial = leafSerialColumn.get(row);
            int entityIdentifier = Integer.parseInt(entityIdColumn.get(row));
            int modelIdentifier = modelColumn.get(row);

            CifLeafIdentifier cifLeafIdentifier = new CifLeafIdentifier(pdbId, entityIdentifier, modelIdentifier, chainIdentifier, cifSerial);
            String threeLetterCode = threeLetterCodeColumn.get(row);
            String leafIsHetAtomString = groupPdbColumn.get(row);
            String alternativeConformation = alternativePositionColumn.get(row);

            // lazily initialize structures
            CifModel model = structure.getModel(modelIdentifier)
                    .orElseGet(() -> appendModel(modelIdentifier));

            CifEntity entity = structure.getEntity(entityIdentifier)
                    .orElseGet(() -> appendEntity(entityIdentifier));

            CifChain chain = model.getChain(chainIdentifier)
                    .orElseGet(() -> appendChain(entity, model, chainIdentifier));

            CifLeafSubstructure leafSubstructure = chain.getLeafSubstructure(cifLeafIdentifier)
                    .orElseGet(() -> appendLeafSubstructure(chain, cifLeafIdentifier, threeLetterCode, leafIsHetAtomString));

            CifAtom cifAtom = new CifAtom(atomSerialColumn.get(row));
            cifAtom.setAtomName(atomNameColumn.get(row));
            cifAtom.setBFactor(bFactorColumn.get(row));
            cifAtom.setElement(ElementProvider.getElementBySymbol(elementColumn.get(row)).orElse(ElementProvider.UNKOWN));
            cifAtom.setPosition(new Vector3D(xCoordinateColumn.get(row), yCoordinateColumn.get(row), zCoordinateColumn.get(row)));

            leafSubstructure.addAtom(alternativeConformation, cifAtom);

            if (!alternativeConformation.equals(CifConformation.DEFAULT_CONFORMATION_IDENTIFIER)) {
                leavesWithAlternativeConfirmations.add(leafSubstructure);
            }
        }

        // postprocess leaves with alternative conformations
        for (CifLeafSubstructure leaf : leavesWithAlternativeConfirmations) {
            leaf.postProcessConformations();
        }

    }

    private void extractEntityInformation(MmCifBlock data) {
        Entity entityColumn = data.getEntity();
        StrColumn entityId = entityColumn.getId();
        StrColumn entityNameColumn = entityColumn.getPdbxDescription();
        StrColumn entityTypeColumn = entityColumn.getType();
        for (int row = 0; row < entityColumn.getRowCount(); row++) {
            int entityIdentifier = Integer.parseInt(entityId.get(row));
            CifEntity entity = new CifEntity(entityIdentifier);
            String typeString = entityTypeColumn.get(row);
            CifEntityType type = CifEntityType.getTypeForString(typeString)
                    .orElseThrow(() -> new IllegalArgumentException("unable to determine entity type " + typeString));
            entity.setCifEntityType(type);
            entity.setName(entityNameColumn.get(row));
            entityMap.put(entityIdentifier, entity);
        }
    }

    /**
     * process poly
     * "asym_id" = "mmcif chain" equivalent pdb world "pdb_strand_id"
     * "seq_id" = "mmcif serial" equivalent pdb world "auth_seq_num"
     * there are no insertion codes, pdb insertion codes are "pdb_ins_code"
     * entity id if new for mmcif "entity_id", information on different / same antities that occur in structures
     *
     * @param data
     */
    private void extractPolymerReferenceInformation(MmCifBlock data) {
        PdbxPolySeqScheme pdbxPolySeqScheme = data.getPdbxPolySeqScheme();
        StrColumn asymId = pdbxPolySeqScheme.getAsymId();
        StrColumn pdbStrandId = pdbxPolySeqScheme.getPdbStrandId();
        IntColumn seqId = pdbxPolySeqScheme.getSeqId();
        StrColumn authSeqNum = pdbxPolySeqScheme.getAuthSeqNum();
        StrColumn pdbInsCode = pdbxPolySeqScheme.getPdbInsCode();
        StrColumn entityId = pdbxPolySeqScheme.getEntityId();
        for (int row = 0; row < pdbxPolySeqScheme.getRowCount(); row++) {

            String cifChain = asymId.get(row);
            int cifSerial = seqId.get(row);
            int entityIdentifier = Integer.parseInt(entityId.get(row));
            CifLeafIdentifier cifLeafIdentifier = new CifLeafIdentifier(pdbId, entityIdentifier, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, cifChain, cifSerial);

            String pdbChain = pdbStrandId.get(row);
            int pdbSerial = Integer.parseInt(authSeqNum.get(row));
            char pdbInsertionCode;
            if (pdbInsCode.get(row) != null && !pdbInsCode.get(row).isEmpty()) {
                pdbInsertionCode = pdbInsCode.get(row).charAt(0);
            } else {
                pdbInsertionCode = PdbLeafIdentifier.DEFAULT_INSERTION_CODE;
            }
            pdbReferenceMap.put(cifLeafIdentifier, new PdbLeafIdentifier(pdbId, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, pdbChain, pdbSerial, pdbInsertionCode));
        }

    }

    private void extractNonPolymerReferenceInformation(MmCifBlock data) {
        PdbxNonpolyScheme pdbxNonpolySeqScheme = data.getPdbxNonpolyScheme();
        StrColumn asymId = pdbxNonpolySeqScheme.getAsymId();
        StrColumn pdbStrandId = pdbxNonpolySeqScheme.getPdbStrandId();
        StrColumn ndbSeqNum = pdbxNonpolySeqScheme.getNdbSeqNum();
        StrColumn authSeqNum = pdbxNonpolySeqScheme.getAuthSeqNum();
        StrColumn pdbInsCode = pdbxNonpolySeqScheme.getPdbInsCode();
        StrColumn entityId = pdbxNonpolySeqScheme.getEntityId();

        for (int row = 0; row < pdbxNonpolySeqScheme.getRowCount(); row++) {

            String cifChain = asymId.get(row);
            int cifSerial = Integer.parseInt(ndbSeqNum.get(row));
            int entityIdentifier = Integer.parseInt(entityId.get(row));
            CifLeafIdentifier cifLeafIdentifier = new CifLeafIdentifier(pdbId, entityIdentifier, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, cifChain, cifSerial);

            String pdbChain = pdbStrandId.get(row);
            int pdbSerial = Integer.parseInt(authSeqNum.get(row));
            char pdbInsertionCode;
            if (pdbInsCode.get(row) != null && !pdbInsCode.get(row).isEmpty()) {
                pdbInsertionCode = pdbInsCode.get(row).charAt(0);
            } else {
                pdbInsertionCode = PdbLeafIdentifier.DEFAULT_INSERTION_CODE;
            }
            pdbReferenceMap.put(cifLeafIdentifier, new PdbLeafIdentifier(pdbId, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, pdbChain, pdbSerial, pdbInsertionCode));

        }
    }

    private CifLeafSubstructure appendLeafSubstructure(CifChain chain, CifLeafIdentifier cifLeafIdentifier, String threeLetterCode, String leafIsHetAtomString) {
        CifLeafSubstructure leafSubstructure = CifLeafSubstructureFactory.createLeafSubstructure(leafSkeletonFactory.getLeafSkeleton(threeLetterCode), cifLeafIdentifier);
        leafSubstructure.setAnnotatedAsHeteroAtom(leafIsHetAtomString.equals("HETATM"));
        chain.addLeafSubstructure(leafSubstructure);
        return leafSubstructure;
    }

    private CifChain appendChain(CifEntity entity, CifModel model, String chainIdentifier) {
        CifChain cifChain = new CifChain(chainIdentifier);
        model.addChain(cifChain);
        entity.addChain(cifChain);
        return cifChain;
    }

    private CifModel appendModel(int modelIdentifier) {
        CifModel cifModel = new CifModel(modelIdentifier);
        structure.addModel(cifModel);
        return cifModel;
    }

    private CifEntity appendEntity(int entityIdentifier) {
        CifEntity cifEntity = entityMap.get(entityIdentifier);
        structure.addEntity(cifEntity);
        return cifEntity;
    }

}
