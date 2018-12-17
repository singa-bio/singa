package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;

import static bio.singa.features.model.Evidence.SourceType.*;

/**
 * @author cl
 */
public class DefaultFeatureSources {

    public static final Evidence BINESH2015 = new Evidence(LITERATURE, "Binesh 2015", "Binesh, A. R., and R. Kamali. \"Molecular dynamics insights into human aquaporin 2 water channel.\" Biophysical chemistry 207 (2015): 107-113.");
    public static final Evidence EHRLICH2004 = new Evidence(LITERATURE, "Ehrlich 2004", "Ehrlich, Marcelo, et al. \"Endocytosis by random initiation and stabilization of clathrin-coated pits.\" Cell 118.5 (2004): 591-605.");
    public static final Evidence MERRIFIELD2005 = new Evidence(LITERATURE, "Merrifield 2005", "Merrifield, Christien J., David Perrais, and David Zenisek. \"Coupling between clathrin-coated-pit invagination, cortactin recruitment, and membrane scission observed in live cells.\" Cell 121.4 (2005): 593-606.");
    public static final Evidence JHA2015 = new Evidence(LITERATURE, "Jha 2015", "Jha, Rupam, and Thomas Surrey. \"Regulation of processive motion and microtubule localization of cytoplasmic dynein.\" (2015): 48-57.");
    public static final Evidence LANG2000 = new Evidence(LITERATURE, "Lang 2000", "Lang, Thorsten, et al. \"Role of actin cortex in the subplasmalemmal transport of secretory granules in PC-12 cells.\" Biophysical journal 78.6 (2000): 2863-2877.");
    public static final Evidence DONOVAN2015 = new Evidence(LITERATURE, "Donovan 2015", "Donovan, Kirk W., and Anthony Bretscher. \"Tracking individual secretory vesicles during exocytosis reveals an ordered and regulated process.\" J Cell Biol (2015): jcb-201501118.");

    private DefaultFeatureSources() {

    }

}
