package bio.singa.structure.model.cif;

import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.io.ccd.LeafSkeletonFactory;
import bio.singa.structure.io.general.StructureParserOptions;
import bio.singa.structure.model.interfaces.LeafIdentifier;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.rcsb.cif.model.FloatColumn;
import org.rcsb.cif.model.IntColumn;
import org.rcsb.cif.model.StrColumn;
import org.rcsb.cif.schema.mm.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class CifConverter {

    // TODO could be enum
    public static final String COVALENT_CONNECTION_TYPE = "covale";

    private boolean coalesceLigands;

    private final MmCifFile mmcifFile;
    private final Map<Integer, CifEntity> entityMap;
    private final Map<PdbLeafIdentifier, CifLeafIdentifier> pdbReferenceMap;

    private final Map<String, String> chainInformation;

    /**
     * Chains of branched entities that are connected to protein polymer structures, making them modifications and part
     * of the polymer chain.
     */
    private final Set<String> connectedBranches;

    private LeafSkeletonFactory leafSkeletonFactory;

    private CifStructure structure;
    private String pdbId;

    private final AtomicInteger modificationCounter = new AtomicInteger(0);

    public CifConverter(MmCifFile mmcifFile, LeafSkeletonFactory leafSkeletonFactory) {
        this(mmcifFile);
        this.leafSkeletonFactory = leafSkeletonFactory;
    }

    public CifConverter(MmCifFile mmcifFile) {
        this.mmcifFile = mmcifFile;
        entityMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
        connectedBranches = new HashSet<>();
        chainInformation = new HashMap<>();
    }

    public static CifStructure convert(MmCifFile cifFile, LeafSkeletonFactory leafSkeletonFactory) {
        CifConverter cifConverter = new CifConverter(cifFile, leafSkeletonFactory);
        return cifConverter.convert();
    }

    public static CifStructure convert(MmCifFile cifFile, LeafSkeletonFactory leafSkeletonFactory, StructureParserOptions options) {
        CifConverter cifConverter = new CifConverter(cifFile, leafSkeletonFactory);
        cifConverter.coalesceLigands = options.isCoalesceLigands();
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
        extractChainInformation(data);
        extractAtomInformation(data);
        extractConnectionInformation(data);
        extractCloseContactInformation(data);
        postProcessBranchedEntities();

        return structure;
    }

    private void extractChainInformation(MmCifBlock data) {
        // also consider bird information
        PdbxMolecule pdbxMolecule = data.getPdbxMolecule();
        if (!pdbxMolecule.isDefined()) {
            return;
        }
        StrColumn prdId = pdbxMolecule.getPrdId();
        StrColumn asymId = pdbxMolecule.getAsymId();

        for (int row = 0; row < pdbxMolecule.getRowCount(); row++) {
            String id = prdId.get(row);
            String chain = asymId.get(row);
            chainInformation.put(chain, id);
        }

    }

    private void extractAtomInformation(MmCifBlock data) {
        AtomSite atomSite = data.getAtomSite();
        // model
        IntColumn pdbxPDBModelNum = atomSite.getPdbxPDBModelNum();
        // hetatom or atom
        StrColumn groupPdbColumn = atomSite.getGroupPDB();

        // cif entity id
        StrColumn labelEntityId = atomSite.getLabelEntityId();
        // cif chain
        StrColumn labelAsymId = atomSite.getLabelAsymId();
        // cif leaf serial (preferred)
        IntColumn labelSeqId = atomSite.getLabelSeqId();


        // auth chain
        StrColumn authAsymId = atomSite.getAuthAsymId();
        // auth leaf serial (to distinguish branched monomer leaves)
        IntColumn authSeqId = atomSite.getAuthSeqId();
        // insertionCode
        StrColumn pdbxPDBInsCode = atomSite.getPdbxPDBInsCode();

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

            // TODO could possibly be more efficient by checking if the identifier changed compared to previous id
            int modelIdentifier = pdbxPDBModelNum.get(row);
            int cifEntityIdentifier = Integer.parseInt(labelEntityId.get(row));
            String cifChainIdentifier = labelAsymId.get(row);
            int cifSerial = labelSeqId.get(row);

            String pdbChainIdentifier = authAsymId.get(row);
            int pdbSerial = authSeqId.get(row);
            String insertionCode = pdbxPDBInsCode.get(row);

            // in case of branched entities use author id to distinguish monomers explicitly
            if (cifSerial == 0) {
                CifEntityType entityType = entityMap.get(cifEntityIdentifier).getCifEntityType();
                if (entityType.equals(CifEntityType.BRANCHED)) {
                    cifSerial = pdbSerial;
                } else if (!coalesceLigands && (entityType.equals(CifEntityType.WATER) || entityType.equals(CifEntityType.NON_POLYMER))) {
                    cifSerial = pdbSerial;
                }
            }

            CifLeafIdentifier cifLeafIdentifier = new CifLeafIdentifier(pdbId, cifEntityIdentifier, modelIdentifier, cifChainIdentifier, cifSerial);
            PdbLeafIdentifier pdbLeafIdentifier;
            if (insertionCode != null && !insertionCode.isEmpty()) {
                pdbLeafIdentifier = new PdbLeafIdentifier(pdbId, modelIdentifier, pdbChainIdentifier, pdbSerial, insertionCode.charAt(0));
            } else {
                pdbLeafIdentifier = new PdbLeafIdentifier(pdbId, modelIdentifier, pdbChainIdentifier, pdbSerial);
            }

            String threeLetterCode = threeLetterCodeColumn.get(row);
            String leafIsHetAtomString = groupPdbColumn.get(row);
            String alternativeConformation = alternativePositionColumn.get(row);

            // lazily initialize structures
            CifModel model = structure.getModel(modelIdentifier)
                    .orElseGet(() -> appendModel(modelIdentifier));

            CifEntity entity = structure.getEntity(cifEntityIdentifier)
                    .orElseGet(() -> appendEntity(cifEntityIdentifier));

            CifChain chain = model.getChain(cifChainIdentifier)
                    .orElseGet(() -> appendChain(entity, model, cifChainIdentifier));

            CifLeafSubstructure leafSubstructure = chain.getLeafSubstructure(cifLeafIdentifier)
                    .orElseGet(() -> appendLeafSubstructure(entity, chain, cifLeafIdentifier, pdbLeafIdentifier, threeLetterCode, leafIsHetAtomString));

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

        // try to get common entity names
        EntityNameCom entityNameCom = data.getEntityNameCom();
        if (!entityNameCom.isDefined()) {
            return;
        }
        StrColumn comEntityId = entityNameCom.getEntityId();
        StrColumn comName = entityNameCom.getName();
        for (int row = 0; row < entityNameCom.getRowCount(); row++) {
            int entityIdentifier = Integer.parseInt(comEntityId.get(row));
            String name = comName.get(row);
            entityMap.get(entityIdentifier).setName(name);
        }


    }

    private void extractConnectionInformation(MmCifBlock data) {
        StructConn structConn = data.getStructConn();
        // connection type e.g. disulf, covale, ...
        StrColumn connTypeId = structConn.getConnTypeId();
        // name for PTMs
        StrColumn pdbxRole = structConn.getPdbxRole();
        // chain first
        StrColumn ptnr1LabelAsymId = structConn.getPtnr1LabelAsymId();
        // leaf serial first
        IntColumn ptnr1LabelSeqId = structConn.getPtnr1LabelSeqId();
        // auth leaf serial first
        IntColumn ptnr1AuthSeqId = structConn.getPtnr1AuthSeqId();
        // atom name first
        StrColumn ptnr1LabelAtomId = structConn.getPtnr1LabelAtomId();
        // chain second
        StrColumn ptnr2LabelAsymId = structConn.getPtnr2LabelAsymId();
        // leaf serial second
        IntColumn ptnr2LabelSeqId = structConn.getPtnr2LabelSeqId();
        // auth leaf serial first
        IntColumn ptnr2AuthSeqId = structConn.getPtnr2AuthSeqId();
        // atom name first
        StrColumn ptnr2LabelAtomId = structConn.getPtnr2LabelAtomId();

        for (int row = 0; row < structConn.getRowCount(); row++) {
            String connectionType = connTypeId.get(row);
            // only for covalent connections
            // TODO could be expanded to disulfide bonds etc.
            if (!connectionType.equals(COVALENT_CONNECTION_TYPE)) {
                continue;
            }
            String descriptor = "";
            if (pdbxRole.isDefined()) {
                descriptor = pdbxRole.get(row);
            }
            String firstChainId = ptnr1LabelAsymId.get(row);
            int firstSerial = ptnr1LabelSeqId.get(row);
            // in branched case there is some inconsistency with the sequence id
            if (firstSerial == 0 && structure.getFirstModel().getChain(firstChainId).get().getType().equals(CifEntityType.BRANCHED)) {
                firstSerial = ptnr1AuthSeqId.get(row);
            }
            String firstAtomName = ptnr1LabelAtomId.get(row);
            String secondChainId = ptnr2LabelAsymId.get(row);
            int secondSerial = ptnr2LabelSeqId.get(row);
            // in branched case there is some inconsistency with the sequence id
            if (secondSerial == 0 && structure.getFirstModel().getChain(secondChainId).get().getType().equals(CifEntityType.BRANCHED)) {
                secondSerial = ptnr2AuthSeqId.get(row);
            }
            String secondAtomName = ptnr2LabelAtomId.get(row);


            for (CifModel model : structure.getAllModels()) {
                // create leaf ids
                CifLeafIdentifier firstLeafIdentifier = LeafIdentifier.cif()
                        .model(model.getModelIdentifier())
                        .chain(firstChainId)
                        .serial(firstSerial);
                CifLeafIdentifier secondLeafIdentifier = LeafIdentifier.cif()
                        .model(model.getModelIdentifier())
                        .chain(secondChainId)
                        .serial(secondSerial);
                // get leafs
                Optional<CifLeafSubstructure> optionalFirstLeaf = model.getLeafSubstructure(firstLeafIdentifier);
                Optional<CifLeafSubstructure> optionalSecondLeaf = model.getLeafSubstructure(secondLeafIdentifier);
                // either leaf not present
                if (!optionalFirstLeaf.isPresent() || !optionalSecondLeaf.isPresent()) {
                    continue;
                }
                CifLeafSubstructure firstLeaf = optionalFirstLeaf.get();
                CifLeafSubstructure secondLeaf = optionalSecondLeaf.get();
                Optional<CifAtom> firstAtom = firstLeaf.getAtomByName(firstAtomName);
                Optional<CifAtom> secondAtom = secondLeaf.getAtomByName(secondAtomName);
                // either atom not present
                if (!firstAtom.isPresent() || !secondAtom.isPresent()) {
                    continue;
                }
                // assign connection
                firstLeaf.connect(firstAtom.get().getAtomName(), secondAtom.get().getAtomName(), secondLeaf);
                if (descriptor.isEmpty()) {
                    descriptor = "modificaiton " + modificationCounter.getAndIncrement();
                }
                setModification(descriptor, firstLeaf, secondLeaf);
            }
        }
    }

    private void extractCloseContactInformation(MmCifBlock data) {
        PdbxValidateCloseContact pdbxValidateCloseContact = data.getPdbxValidateCloseContact();
        if (!pdbxValidateCloseContact.isDefined()) {
            return;
        }
        // model
        IntColumn pdbModelNum = pdbxValidateCloseContact.getPDBModelNum();

        // first chain
        StrColumn authAsymId1 = pdbxValidateCloseContact.getAuthAsymId1();
        // first serial
        StrColumn authSeqId1 = pdbxValidateCloseContact.getAuthSeqId1();
        // first insertionCode
        StrColumn pdbInsCode1 = pdbxValidateCloseContact.getPDBInsCode1();
        // first atom name
        StrColumn authAtomId1 = pdbxValidateCloseContact.getAuthAtomId1();

        // second chain
        StrColumn authAsymId2 = pdbxValidateCloseContact.getAuthAsymId2();
        // second serial
        StrColumn authSeqId2 = pdbxValidateCloseContact.getAuthSeqId2();
        // second insertionCode
        StrColumn pdbInsCode2 = pdbxValidateCloseContact.getPDBInsCode2();
        // second atom name
        StrColumn authAtomId2 = pdbxValidateCloseContact.getAuthAtomId2();

        for (int row = 0; row < pdbxValidateCloseContact.getRowCount(); row++) {
            int modelId = pdbModelNum.get(row);
            String firstChain = authAsymId1.get(row);
            int firstSerial = Integer.parseInt(authSeqId1.get(row));
            String firstAtomName = authAtomId1.get(row);
            char firstInsertionCode;
            if (!pdbInsCode1.isDefined()) {
                firstInsertionCode = pdbInsCode1.get(row).charAt(0);
            } else {
                firstInsertionCode = PdbLeafIdentifier.DEFAULT_INSERTION_CODE;
            }

            PdbLeafIdentifier firstPdbLeafIdentifier = LeafIdentifier.pdb()
                    .structure(pdbId)
                    .model(PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER)
                    .chain(firstChain)
                    .serial(firstSerial)
                    .insertionCode(firstInsertionCode);

            String secondChain = authAsymId2.get(row);
            int secondSerial = Integer.parseInt(authSeqId2.get(row));
            String secondAtomName = authAtomId2.get(row);
            char secondInsertionCode;
            if (!pdbInsCode2.isDefined()) {
                secondInsertionCode = pdbInsCode2.get(row).charAt(0);
            } else {
                secondInsertionCode = PdbLeafIdentifier.DEFAULT_INSERTION_CODE;
            }

            PdbLeafIdentifier secondPdbLeafIdentifier = LeafIdentifier.pdb()
                    .structure(pdbId)
                    .model(PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER)
                    .chain(secondChain)
                    .serial(secondSerial)
                    .insertionCode(secondInsertionCode);

            CifLeafIdentifier firstCifLeafIdentifier = pdbReferenceMap.get(firstPdbLeafIdentifier);
            if (firstCifLeafIdentifier == null) {
                continue;
            }
            CifLeafIdentifier secondCifLeafIdentifier = pdbReferenceMap.get(secondPdbLeafIdentifier);
            if (secondCifLeafIdentifier == null) {
                continue;
            }

            Optional<CifModel> optionalModel = structure.getModel(modelId);
            if (!optionalModel.isPresent()) {
                continue;
            }
            CifModel model = optionalModel.get();

            // get leafs
            Optional<CifLeafSubstructure> optionalFirstLeaf = model.getLeafSubstructure(firstCifLeafIdentifier);
            Optional<CifLeafSubstructure> optionalSecondLeaf = model.getLeafSubstructure(secondCifLeafIdentifier);
            // either leaf not present
            if (!optionalFirstLeaf.isPresent() || !optionalSecondLeaf.isPresent()) {
                continue;
            }
            CifLeafSubstructure firstLeaf = optionalFirstLeaf.get();
            CifLeafSubstructure secondLeaf = optionalSecondLeaf.get();
            Optional<CifAtom> firstAtom = firstLeaf.getAtomByName(firstAtomName);
            Optional<CifAtom> secondAtom = secondLeaf.getAtomByName(secondAtomName);
            // either atom not present
            if (!firstAtom.isPresent() || !secondAtom.isPresent()) {
                continue;
            }
            // assign connection
            firstLeaf.connect(firstAtom.get().getAtomName(), secondAtom.get().getAtomName(), secondLeaf);
            String descriptor = "unknown modification " + modificationCounter.getAndIncrement();
            setModification(descriptor, firstLeaf, secondLeaf);

        }

    }

    private void setModification(String descriptor, CifLeafSubstructure firstLeaf, CifLeafSubstructure secondLeaf) {
        if (firstLeaf instanceof CifAminoAcid) {
            String chainIdentifier = secondLeaf.getIdentifier().getChainIdentifier();
            connectedBranches.add(chainIdentifier);
            ((CifAminoAcid) firstLeaf).getModifications().put(descriptor, chainIdentifier);
        } else if (secondLeaf instanceof CifAminoAcid) {
            String chainIdentifier = firstLeaf.getIdentifier().getChainIdentifier();
            connectedBranches.add(chainIdentifier);
            ((CifAminoAcid) secondLeaf).getModifications().put(descriptor, chainIdentifier);
        }
    }

    private void postProcessBranchedEntities() {
        for (String connectedBranch : connectedBranches) {
            for (CifModel model : structure.getAllModels()) {
                Optional<CifChain> optionalChain = model.getChain(connectedBranch);
                if (!optionalChain.isPresent()) {
                    continue;
                }
                CifChain chain = optionalChain.get();
                for (CifLeafSubstructure substructure : chain.getAllLeafSubstructures()) {
                    substructure.setPartOfPolymer(true);
                }
            }
        }
    }

    private CifLeafSubstructure appendLeafSubstructure(CifEntity cifEntity, CifChain chain, CifLeafIdentifier cifLeafIdentifier, PdbLeafIdentifier pdbLeafIdentifier, String threeLetterCode, String leafIsHetAtomString) {
        CifLeafSubstructure leafSubstructure = CifLeafSubstructureFactory.createLeafSubstructure(leafSkeletonFactory.getLeafSkeleton(threeLetterCode), cifLeafIdentifier);
        leafSubstructure.setAnnotatedAsHeteroAtom(leafIsHetAtomString.equals("HETATM"));
        leafSubstructure.setPartOfPolymer(cifEntity.getCifEntityType().equals(CifEntityType.POLYMER));
        chain.addLeafSubstructure(leafSubstructure);
        pdbReferenceMap.put(pdbLeafIdentifier, cifLeafIdentifier);
        return leafSubstructure;
    }

    private CifChain appendChain(CifEntity entity, CifModel model, String chainIdentifier) {
        CifChain cifChain = new CifChain(chainIdentifier);
        cifChain.setType(entity.getCifEntityType());
        cifChain.setAdditionalIdentifier(chainInformation.get(chainIdentifier));
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
