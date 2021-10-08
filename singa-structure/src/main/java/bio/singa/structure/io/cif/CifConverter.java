package bio.singa.structure.io.cif;

import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.cif.*;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.LeafSkeleton;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.rcsb.cif.model.FloatColumn;
import org.rcsb.cif.model.IntColumn;
import org.rcsb.cif.model.StrColumn;
import org.rcsb.cif.schema.mm.*;

import java.util.HashMap;
import java.util.Map;


public class CifConverter {

    private boolean createPdbReference;

    private MmCifFile mmCifFile;
    private Map<String, LeafSkeleton> compoundMap;
    private Map<Integer, String> entityNameMap;
    private Map<CifLeafIdentifier, PdbLeafIdentifier> pdbReferenceMap;

    private CifStructure structure;
    private String pdbId;

    public CifConverter(MmCifFile mmCifFile) {
        this.mmCifFile = mmCifFile;
        createPdbReference = false;
        compoundMap = new HashMap<>();
        entityNameMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
    }

    public CifConverter(MmCifFile mmCifFile, boolean createPdbReference) {
        this.mmCifFile = mmCifFile;
        this.createPdbReference = createPdbReference;
        compoundMap = new HashMap<>();
        entityNameMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
    }

    public static CifStructure convert(MmCifFile cifFile) {
        CifConverter cifConverter = new CifConverter(cifFile);
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
                resolution = data.getReflns().getDResolutionHigh().get(0);
            } else if (method.equals("")) {
                resolution = data.getEm3dReconstruction().getResolution().get(0);
            } else {
                resolution = Double.NaN;
            }
            structure.setResolution(resolution);
        }

        // title
        Struct structColumn = data.getStruct();
        if (structColumn.isDefined()) {
            structure.setTitle(structColumn.getTitle().get(0));
        }
    }

    private CifStructure convert() {
        MmCifBlock data = mmCifFile.getFirstBlock();
        extractMetaData(data);
        extractCompoundInformation(data);
        extractEntityNames(data);
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

        for (int row = 0; row < atomSite.getRowCount(); row++) {

            // todo could possibly be more efficient by checking if the identifier changed compared to previous id
            String chainIdentifier = chainColumn.get(row);
            int cifSerial = leafSerialColumn.get(row);
            int entityIdentifier = Integer.parseInt(entityIdColumn.get(row));
            int modelIdentifier = modelColumn.get(row);

            CifLeafIdentifier cifLeafIdentifier = new CifLeafIdentifier(pdbId, entityIdentifier, modelIdentifier, chainIdentifier, cifSerial);
            String threeLetterCode = threeLetterCodeColumn.get(row);
            String leafIsHetAtomString = groupPdbColumn.get(row);

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

            leafSubstructure.addAtom(cifAtom);
        }
    }

    private void extractEntityNames(MmCifBlock data) {
        Entity entityColumn = data.getEntity();
        StrColumn entityId = entityColumn.getId();
        StrColumn entityNameColumn = entityColumn.getPdbxDescription();
        for (int row = 0; row < entityColumn.getRowCount(); row++) {
            entityNameMap.put(Integer.parseInt(entityId.get(row)), entityNameColumn.get(row));
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


    /**
     * From monomer table build index of what each three letter code is.
     *
     * @param data
     */
    private void extractCompoundInformation(MmCifBlock data) {
        ChemComp chemComp = data.getChemComp();
        StrColumn idColumn = chemComp.getId();
        StrColumn typeColumn = chemComp.getType();
        StrColumn nameColumn = chemComp.getName();
        compoundMap = new HashMap<>();
        for (int row = 0; row < chemComp.getRowCount(); row++) {
            String id = idColumn.get(row);
            LeafSkeleton leafSkeleton = new LeafSkeleton(id);
            leafSkeleton.setName(nameColumn.get(row));
            String type = typeColumn.get(row);
            if (LeafSkeleton.isNucleotide(type)) {
                if (StructuralFamilies.Nucleotides.isNucleotide(id)) {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.NUCLEOTIDE);
                    leafSkeleton.setStructuralFamily(StructuralFamilies.Nucleotides.getOrUnknown(leafSkeleton.getThreeLetterCode()));
                } else {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.MODIFIED_NUCLEOTIDE);
                    leafSkeleton.setStructuralFamily(StructuralFamilies.Nucleotides.getOrUnknown(leafSkeleton.getParent()));
                }
            } else if (LeafSkeleton.isAminoAcid(type)) {
                if (StructuralFamilies.AminoAcids.isAminoAcid(id)) {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.AMINO_ACID);
                    leafSkeleton.setStructuralFamily(StructuralFamilies.AminoAcids.getOrUnknown(leafSkeleton.getThreeLetterCode()));
                } else {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.MODIFIED_AMINO_ACID);
                    leafSkeleton.setStructuralFamily(StructuralFamilies.AminoAcids.getOrUnknown(leafSkeleton.getParent()));
                }
            } else {
                leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.LIGAND);
                leafSkeleton.setStructuralFamily(new StructuralFamily("?", id));
            }
            compoundMap.put(id, leafSkeleton);
        }
    }


    private CifLeafSubstructure appendLeafSubstructure(CifChain chain, CifLeafIdentifier cifLeafIdentifier, String threeLetterCode, String leafIsHetAtomString) {
        CifLeafSubstructure leafSubstructure = CifLeafSubstructureFactory.createLeafSubstructure(compoundMap.get(threeLetterCode), cifLeafIdentifier);
        leafSubstructure.setAnnotatedAsHetAtom(leafIsHetAtomString.equals("HETATM"));
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
        CifEntity cifEntity = new CifEntity(entityIdentifier);
        cifEntity.setName(entityNameMap.get(entityIdentifier));
        structure.addEntity(cifEntity);
        return cifEntity;
    }

}
