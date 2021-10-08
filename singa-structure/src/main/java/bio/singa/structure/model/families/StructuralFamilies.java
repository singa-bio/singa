package bio.singa.structure.model.families;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;

import java.util.*;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.*;

public class StructuralFamilies {

    public static class Nucleotides {

        public static final Map<String, StructuralFamily> MAP = new HashMap<>();

        public static final StructuralFamily ADENOSINE = addNucleotide("A", "A");
        public static final StructuralFamily DESOXYADENOSINE = addNucleotide("A", "DA");
        public static final StructuralFamily GUANOSINE = addNucleotide("G", "G");
        public static final StructuralFamily DESOXYGUANOSINE = addNucleotide("G", "DG");
        public static final StructuralFamily THYMIDINE = addNucleotide("T", "T");
        public static final StructuralFamily DESOXYTHYMIDINE = addNucleotide("T", "DT");
        public static final StructuralFamily URIDINE = addNucleotide("U", "U");
        public static final StructuralFamily DESOXYURIDINE = addNucleotide("U", "DU");
        public static final StructuralFamily CYTIDINE = addNucleotide("C", "C");
        public static final StructuralFamily DESOXYCYTIDINE = addNucleotide("C", "DC");
        public static final StructuralFamily UNKNOWN = addNucleotide("X", "UNK");

        public static Optional<StructuralFamily> get(String threeLetterCode) {
            return Optional.ofNullable(MAP.get(threeLetterCode));
        }

        public static StructuralFamily getOrUnknown(String threeLetterCode) {
            return Optional.ofNullable(MAP.get(threeLetterCode)).orElse(UNKNOWN);
        }

        public static boolean isNucleotide(StructuralFamily structuralFamily) {
            return isNucleotide(structuralFamily.getThreeLetterCode());
        }

        public static boolean isNucleotide(String threeLetterCode) {
            return MAP.containsKey(threeLetterCode);
        }

        private static StructuralFamily addNucleotide(String oneLetterCode, String threeLetterCode) {
            return addNucleotide(new StructuralFamily(oneLetterCode, threeLetterCode));
        }

        private static StructuralFamily addNucleotide(StructuralFamily nucleotide) {
            Nucleotides.MAP.put(nucleotide.getThreeLetterCode(), nucleotide);
            return nucleotide;
        }

        private  Nucleotides() {

        }

        public static final Set<StructuralFamily> ALL = new HashSet<>(Nucleotides.MAP.values());
    }

    public static class AminoAcids {

        public static final Map<String, StructuralFamily> MAP = new HashMap<>();

        public static final StructuralFamily ALANINE = addAminoAcid("A", "ALA");
        public static final StructuralFamily ARGININE = addAminoAcid("R", "ARG");
        public static final StructuralFamily ASPARAGINE = addAminoAcid("N", "ASN");
        public static final StructuralFamily ASPARTIC_ACID = addAminoAcid("D", "ASP");
        public static final StructuralFamily CYSTEINE = addAminoAcid("C", "CYS");
        public static final StructuralFamily GLUTAMINE = addAminoAcid("Q", "GLN");
        public static final StructuralFamily GLUTAMIC_ACID = addAminoAcid("E", "GLU");
        public static final StructuralFamily GLYCINE = addAminoAcid("G", "GLY");
        public static final StructuralFamily HISTIDINE = addAminoAcid("H", "HIS");
        public static final StructuralFamily ISOLEUCINE = addAminoAcid("I", "ILE");
        public static final StructuralFamily LEUCINE = addAminoAcid("L", "LEU");
        public static final StructuralFamily LYSINE = addAminoAcid("K", "LYS");
        public static final StructuralFamily METHIONINE = addAminoAcid("M", "MET");
        public static final StructuralFamily PHENYLALANINE = addAminoAcid("F", "PHE");
        public static final StructuralFamily PROLINE = addAminoAcid("P", "PRO");
        public static final StructuralFamily SERINE = addAminoAcid("S", "SER");
        public static final StructuralFamily THREONINE = addAminoAcid("T", "THR");
        public static final StructuralFamily TRYPTOPHAN = addAminoAcid("W", "TRP");
        public static final StructuralFamily TYROSINE = addAminoAcid("Y", "TYR");
        public static final StructuralFamily VALINE = addAminoAcid("V", "VAL");
        public static final StructuralFamily UNKNOWN = addAminoAcid("X", "UNK");
        public static final StructuralFamily GAP = addAminoAcid("-", "GAP");

        private static final String RESIDUE_PROTOTYPES_BASE_DIR = "bio/singa/structure/leaves/prototypes/";

        public static boolean isAminoAcid(StructuralFamily structuralFamily) {
            return isAminoAcid(structuralFamily.getThreeLetterCode());
        }

        public static boolean isAminoAcid(String threeLetterCode) {
            return MAP.containsKey(threeLetterCode);
        }

        public static AminoAcid getPrototype(StructuralFamily structuralFamily) {
            return StructureParser.local()
                    .inputStream(Resources.getResourceAsStream(RESIDUE_PROTOTYPES_BASE_DIR +structuralFamily.getThreeLetterCode() + ".pdb"))
                    .settings(StructureParserOptions.Setting.OMIT_HYDROGENS)
                    .parse()
                    .getAllAminoAcids()
                    .get(0);
        }

        public static Optional<StructuralFamily> get(String threeLetterCode) {
            return Optional.ofNullable(MAP.get(threeLetterCode));
        }

        public static StructuralFamily getOrUnknown(String threeLetterCode) {
            return Optional.ofNullable(MAP.get(threeLetterCode)).orElse(UNKNOWN);
        }

        private static StructuralFamily addAminoAcid(String oneLetterCode, String threeLetterCode) {
            return addAminoAcid(new StructuralFamily(oneLetterCode, threeLetterCode));
        }

        private static StructuralFamily addAminoAcid(StructuralFamily aminoAcid) {
            AminoAcids.MAP.put(aminoAcid.getThreeLetterCode(), aminoAcid);
            return aminoAcid;
        }

        public static final Set<StructuralFamily> ALL = new HashSet<>(AminoAcids.MAP.values());

        private AminoAcids() {

        }
    }

    public static class Matchers {

        public static final Map<StructuralFamily, Set<StructuralFamily>> MAP = new HashMap<>();

        public static final StructuralFamily GUTTERIDGE_IMIDAZOLE = addMatcherFamily("i", "IMI", Set.of(HISTIDINE));
        public static final StructuralFamily GUTTERIDGE_AMINE = addMatcherFamily("n", "AMN", Set.of(LYSINE));
        public static final StructuralFamily GUTTERIDGE_CARBOXYLATE = addMatcherFamily("n", "CAB", Set.of(ASPARTIC_ACID, GLUTAMIC_ACID));
        public static final StructuralFamily GUTTERIDGE_AMIDE = addMatcherFamily("d", "AMD", Set.of(ASPARAGINE, GLUTAMINE));
        public static final StructuralFamily GUTTERIDGE_HYDROXYL = addMatcherFamily("h", "HYD", Set.of(SERINE, THREONINE, TYROSINE));
        public static final StructuralFamily GUTTERIDGE_THIOL = addMatcherFamily("t", "THI", Set.of(CYSTEINE));
        public static final StructuralFamily GUTTERIDGE_GUANIDIUM = addMatcherFamily("g", "GND", Set.of(ARGININE));
        public static final StructuralFamily GUTTERIDGE_OTHERS = addMatcherFamily("o", "OTH", Set.of(ALANINE, GLYCINE, ISOLEUCINE, LEUCINE, METHIONINE, PHENYLALANINE, PROLINE, TRYPTOPHAN, VALINE));

        /**
         * The following types are grouped according to
         * <pre>
         *      Gutteridge, A. and Thornton, J. M.:
         *      Understanding nature's catalytic toolkit Trends in biochemical sciences, Elsevier, 2005, 30, 622-629.
         * </pre>
         */
        public static final Set<StructuralFamily> ALL_GUTTERIDGE = Set.of(GUTTERIDGE_IMIDAZOLE, GUTTERIDGE_AMINE, GUTTERIDGE_CARBOXYLATE, GUTTERIDGE_AMIDE, GUTTERIDGE_HYDROXYL, GUTTERIDGE_THIOL, GUTTERIDGE_GUANIDIUM, GUTTERIDGE_OTHERS);

        public static final StructuralFamily FUNCTIONAL_AROMATIC = addMatcherFamily("a", "ARO", Set.of(PHENYLALANINE, TYROSINE, TRYPTOPHAN));
        public static final StructuralFamily FUNCTIONAL_NEGATIVE = addMatcherFamily("e", "NEG", Set.of(ASPARTIC_ACID, GLUTAMIC_ACID));
        public static final StructuralFamily FUNCTIONAL_POSITIVE = addMatcherFamily("p", "POS", Set.of(LYSINE, ARGININE, HISTIDINE));
        public static final StructuralFamily FUNCTIONAL_POLAR = addMatcherFamily("p", "POL", Set.of(PROLINE, ASPARAGINE, GLUTAMINE, CYSTEINE, THREONINE, SERINE));
        public static final StructuralFamily FUNCTIONAL_UNPOLAR = addMatcherFamily("u", "UPO", Set.of(GLYCINE, ALANINE, VALINE, LEUCINE, METHIONINE, ISOLEUCINE));

        /**
         * The following types are grouped according to functional chemical groups:
         * <pre>
         *      aromatic (a)             F,Y,W
         *      negatively charged (n)   D,E
         *      positively charged (p)   K,R,H
         *      polar, uncharged (o)     P,N,Q,C,T,S
         *      nonpolar, aliphatic (i)  G,A,V,L,M,I
         * </pre>
         */
        public static final Set<StructuralFamily> ALL_FUNCTIONAL = Set.of(FUNCTIONAL_AROMATIC, FUNCTIONAL_NEGATIVE, FUNCTIONAL_POSITIVE, FUNCTIONAL_POLAR, FUNCTIONAL_UNPOLAR);

        public static final StructuralFamily PHYSICOCHEMICAL_AROMATIC = addMatcherFamily("a", "ARO", Set.of(TRYPTOPHAN, TYROSINE, PHENYLALANINE));
        public static final StructuralFamily PHYSICOCHEMICAL_NEGATIVE = addMatcherFamily("n", "NEG", Set.of(ASPARTIC_ACID, GLUTAMIC_ACID));
        public static final StructuralFamily PHYSICOCHEMICAL_POSITIVE = addMatcherFamily("p", "POS", Set.of(LYSINE, ARGININE, HISTIDINE));
        public static final StructuralFamily PHYSICOCHEMICAL_POLAR = addMatcherFamily("u", "POL", Set.of(GLUTAMINE, METHIONINE, CYSTEINE, PROLINE, ASPARAGINE, THREONINE));
        public static final StructuralFamily PHYSICOCHEMICAL_TINY = addMatcherFamily("t", "TNY", Set.of(ALANINE, GLYCINE, SERINE));
        public static final StructuralFamily PHYSICOCHEMICAL_ALIPHATIC = addMatcherFamily("p", "ALI", Set.of(LEUCINE, ISOLEUCINE, VALINE));

        /**
         * The following types are grouped according to the definition of Livingstone and Barton 1993.
         * <pre>
         *      aromatic (a)             Y,W,F
         *      negatively charged (n)   D,E
         *      positively charged (p)   K,R,H
         *      polar, uncharged (u)     Q,M,C,P,N,T
         *      tiny (t)                 A,G,S
         *      aliphatic (p)            L,I,V
         * </pre>
         */
        public static final Set<StructuralFamily> ALL_PHYSICOCHEMICAL = Set.of(PHYSICOCHEMICAL_AROMATIC, PHYSICOCHEMICAL_NEGATIVE, PHYSICOCHEMICAL_POSITIVE, PHYSICOCHEMICAL_POLAR, PHYSICOCHEMICAL_TINY, PHYSICOCHEMICAL_ALIPHATIC);

        public static final Set<StructuralFamily> ALL = new HashSet<>(AminoAcids.MAP.values());

        private static StructuralFamily addMatcherFamily(String oneLetterCode, String threeLetterCode, Set<StructuralFamily> matchedFamilies) {
            return addMatcherFamily(new StructuralFamily(oneLetterCode, threeLetterCode), matchedFamilies);
        }

        private static StructuralFamily addMatcherFamily(StructuralFamily structuralFamily, Set<StructuralFamily> matchedFamilies) {
            MAP.put(structuralFamily, matchedFamilies);
            return structuralFamily;
        }

        public static Set<StructuralFamily> getMatcherEntities(StructuralFamily structuralFamily) {
            return MAP.get(structuralFamily);
        }

        public static boolean isMatcher(StructuralFamily structuralFamily) {
            return MAP.containsKey(structuralFamily);
        }

    }


}
