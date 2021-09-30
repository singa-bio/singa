package bio.singa.structure.model.cif;

import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.LeafSkeleton;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.CifFile;
import org.rcsb.cif.model.IntColumn;
import org.rcsb.cif.model.StrColumn;
import org.rcsb.cif.schema.StandardSchemata;
import org.rcsb.cif.schema.mm.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CifConverter {

    private MmCifFile mmCifFile;
    private Map<String, LeafSkeleton> compoundMap;
    private Map<CifLeafIdentifier, CifPreloadedLeafSubstructure> leafMap;
    private Map<PdbLeafIdentifier, CifLeafIdentifier> pdbReferenceMap;

    public CifConverter(MmCifFile mmCifFile) {
        this.mmCifFile = mmCifFile;
        compoundMap = new HashMap<>();
        leafMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {

        // structure with different leaf enumeration between mmcif and pdb between
        String pdbId = "1v9i";
        CifFile cifFile = CifIO.readFromURL(new URL("https://files.rcsb.org/download/" + pdbId + ".cif"));

        // fine-grained options are available in the CifOptions class

        // access can be generic or using a specified schema - currently supports MMCIF and CIF_CORE
        // you can even use a custom dictionary
        MmCifFile mmCifFile = cifFile.as(StandardSchemata.MMCIF);

        CifConverter cifConverter = new CifConverter(mmCifFile);
        cifConverter.convert(pdbId);



    }

    private void convert(String pdbId) {
        // get first block of CIF
        MmCifBlock data = mmCifFile.getFirstBlock();
        // from monomer table build index of what each three letter code is
        // TODO get meta data from mmcif file (id, title, resolution , etc)
        // _chem_comp.id
        //_chem_comp.type
        //_chem_comp.mon_nstd_flag
        //_chem_comp.name
        //_chem_comp.pdbx_synonyms
        //_chem_comp.formula
        //_chem_comp.formula_weight
        //ALA 'L-peptide linking' y ALANINE         ? 'C3 H7 N O2'     89.093
        //ARG 'L-peptide linking' y ARGININE        ? 'C6 H15 N4 O2 1' 175.209
        //ASN 'L-peptide linking' y ASPARAGINE      ? 'C4 H8 N2 O3'    132.118
        //ASP 'L-peptide linking' y 'ASPARTIC ACID' ? 'C4 H7 N O4'     133.103
        //GLN 'L-peptide linking' y GLUTAMINE       ? 'C5 H10 N2 O3'   146.144
        //GLU 'L-peptide linking' y 'GLUTAMIC ACID' ? 'C5 H9 N O4'     147.129
        //GLY 'peptide linking'   y GLYCINE         ? 'C2 H5 N O2'     75.067
        //HIS 'L-peptide linking' y HISTIDINE       ? 'C6 H10 N3 O2 1' 156.162
        //LEU 'L-peptide linking' y LEUCINE         ? 'C6 H13 N O2'    131.173
        //LYS 'L-peptide linking' y LYSINE          ? 'C6 H15 N2 O2 1' 147.195
        //MET 'L-peptide linking' y METHIONINE      ? 'C5 H11 N O2 S'  149.211

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
                if (StructuralFamilies.Nucleotides.isNucleotide(type)) {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.NUCLEOTIDE);
                } else {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.MODIFIED_NUCLEOTIDE);
                }
            } else if (LeafSkeleton.isAminoAcid(type)) {
                if (StructuralFamilies.AminoAcids.isAminoAcid(type)) {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.AMINO_ACID);
                } else {
                    leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.MODIFIED_AMINO_ACID);
                }
            } else {
                leafSkeleton.setAssignedFamily(LeafSkeleton.AssignedFamily.LIGAND);
            }
            compoundMap.put(id, leafSkeleton);
        }

        // process poly
        // _pdbx_poly_seq_scheme.*
        // contains "asym_id" = "mmcif chain" equivalent pdb world "pdb_strand_id"
        // contains "seq_id" = "mmcif serial" equivalent pdb world "auth_seq_num"
        // there are no insertion codes, pdb insertion codes are "pdb_ins_code"
        // entity id if new for mmcif "entity_id", information on different / same antities that occur in structures
        leafMap = new HashMap<>();
        pdbReferenceMap = new HashMap<>();
        {
            PdbxPolySeqScheme pdbxPolySeqScheme = data.getPdbxPolySeqScheme();
            StrColumn asymId = pdbxPolySeqScheme.getAsymId();
            StrColumn pdbStrandId = pdbxPolySeqScheme.getPdbStrandId();
            IntColumn seqId = pdbxPolySeqScheme.getSeqId();
            StrColumn authSeqNum = pdbxPolySeqScheme.getAuthSeqNum();
            StrColumn pdbInsCode = pdbxPolySeqScheme.getPdbInsCode();
            StrColumn entityId = pdbxPolySeqScheme.getEntityId();
            StrColumn monId = pdbxPolySeqScheme.getMonId();
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
                pdbReferenceMap.put(new PdbLeafIdentifier(pdbId, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, pdbChain, pdbSerial, pdbInsertionCode), cifLeafIdentifier);

                // "three letter code"
                String monomerId = monId.get(row);
                compoundMap.get(monomerId);

//                cifMap.put(cifLeafIdentifier, );
            }
        }

        // ligands
        // _pdbx_nonpoly_scheme.* ligands
        // _pdbx_nonpoly_scheme.asym_id         B
        //_pdbx_nonpoly_scheme.entity_id       2
        //_pdbx_nonpoly_scheme.mon_id          ZN
        //_pdbx_nonpoly_scheme.ndb_seq_num     1
        //_pdbx_nonpoly_scheme.pdb_seq_num     262
        //_pdbx_nonpoly_scheme.auth_seq_num    262
        //_pdbx_nonpoly_scheme.pdb_mon_id      ZN
        //_pdbx_nonpoly_scheme.auth_mon_id     ZN
        //_pdbx_nonpoly_scheme.pdb_strand_id   C
        //_pdbx_nonpoly_scheme.pdb_ins_code    .
        {
            PdbxNonpolyScheme pdbxPolySeqScheme = data.getPdbxNonpolyScheme();
            StrColumn asymId = pdbxPolySeqScheme.getAsymId();
            StrColumn pdbStrandId = pdbxPolySeqScheme.getPdbStrandId();
            StrColumn ndbSeqNum = pdbxPolySeqScheme.getNdbSeqNum();
            StrColumn authSeqNum = pdbxPolySeqScheme.getAuthSeqNum();
            StrColumn pdbInsCode = pdbxPolySeqScheme.getPdbInsCode();
            StrColumn entityId = pdbxPolySeqScheme.getEntityId();
            StrColumn monId = pdbxPolySeqScheme.getMonId();

            for (int row = 0; row < pdbxPolySeqScheme.getRowCount(); row++) {

                String cifChain = asymId.get(row);
                int cifSerial = Integer.parseInt(ndbSeqNum.get(row));
                int entityIdentifier = Integer.parseInt(entityId.get(row));
//                cifMap.add(new CifLeafIdentifier(pdbId, entityIdentifier, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, cifChain, cifSerial));

                String pdbChain = pdbStrandId.get(row);
                int pdbSerial = Integer.parseInt(authSeqNum.get(row));
                char pdbInsertionCode;
                if (pdbInsCode.get(row) != null && !pdbInsCode.get(row).isEmpty()) {
                    pdbInsertionCode = pdbInsCode.get(row).charAt(0);
                } else {
                    pdbInsertionCode = PdbLeafIdentifier.DEFAULT_INSERTION_CODE;
                }
//                pdbMapping.add(new PdbLeafIdentifier(pdbId, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, pdbChain, pdbSerial, pdbInsertionCode));

                // "three letter code"
                String monomerId = monId.get(row);
            }


        }
        // 1: create index of leaves


        // _pdbx_nmr_ensemble.conformers_submitted_total_number             10
        // eg 2N5E
        // contains number of models to create
        // if not nmr no need for extra models





        // lazy load atom data on request
        // keep mmcif data object in
    }

}
