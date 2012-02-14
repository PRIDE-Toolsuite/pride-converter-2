package uk.ac.ebi.pride.tools.converter.dao;

import uk.ac.ebi.pride.tools.converter.dao.impl.*;
import uk.ac.ebi.pride.tools.converter.dao_msgf_impl.MsgfDao;
import uk.ac.ebi.pride.tools.converter.utils.InvalidFormatException;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: rcote
 * Date: 13/01/11
 * Time: 17:19
 */
public class DAOFactory {

    public enum DAO_FORMAT {
        PRIDE("pride", "PRIDE", false, "help.ui.dao.pride"),
        MASCOT("mascot", "Mascot", false, "help.ui.dao.mascot"),
        SEQUEST("sequest", "Sequest", false, null),
        OMSSA("omssa", "OMSSA", false, null),
        MS_LIMS("mslims", "ms_lims", false, null),
        X_TANDEM("xtandem", "X!Tandem", false, "help.ui.dao.xtandem"),
        MGF("mgf", "MGF", true, "help.ui.dao.mgf"),
        MZIDENTML("mzidentml", "mzIdentML", false, "help.ui.dao.mzidentml"),
        DTA("dta", "DTA", true, "help.ui.dao.dta"),
        PKL("pkl", "PKL", true, "help.ui.dao.pkl"),
        MS2("ms2", "MS2", true, "help.ui.dao.ms2"),
        MZML("mzml", "mzML", true, "help.ui.dao.mzml"),
        MZXML("mzxml", "mzXML", true, "help.ui.dao.mzxml"),
        MZDATA("mzdata", "mzData", true, "help.ui.dao.mzdata"),
        MSGF("msgf", "MSGF", true, "help.ui.dao.msgf");

        private String commandLineName;
        private String niceName;
        private boolean spectrumOnly;
        private String helpResource;

        private DAO_FORMAT(String commandLineName, String niceName, boolean spectrumOnly, String helpResource) {
            this.commandLineName = commandLineName;
            this.niceName = niceName;
            this.spectrumOnly = spectrumOnly;
            this.helpResource = helpResource;
        }

        public static DAO_FORMAT getDAOForSearchengineOption(String searchEngineOption) {
            searchEngineOption = searchEngineOption.toLowerCase();

            for (DAO_FORMAT format : values()) {
                if (format.getCommandLineName().equalsIgnoreCase(searchEngineOption))
                    return format;
            }

            return null;
        }

        public String getNiceName() {
            return niceName;
        }

        public String getCommandLineName() {
            return commandLineName;
        }

        public boolean isSpectrumOnly() {
            return spectrumOnly;
        }

        public String getHelpResource() {
            return helpResource;
        }
    }

    public enum GEL_COORDINATE_HANDLER_TYPE {
        DEFAULT_TAB
    }

    private static DAOFactory instance = new DAOFactory();

    public static DAOFactory getInstance() {
        return instance;
    }

    public DAO getDAO(String sourcePath, DAO_FORMAT format) throws InvalidFormatException {
        return getDAO(sourcePath, format, null);
    }

    public DAO getDAO(String sourcePath, DAO_FORMAT format, Properties config) throws InvalidFormatException {

        DAO dao = null;
        switch (format) {
            case MASCOT:
                dao = new MascotDAO(new File(sourcePath));
                break;
            case MGF:
                dao = new MgfDAO(new File(sourcePath));
                break;
            case DTA:
                dao = new DtaDAO(new File(sourcePath));
                break;
            case PKL:
                dao = new PklDAO(new File(sourcePath));
                break;
            case MS2:
                dao = new Ms2DAO(new File(sourcePath));
                break;
            case X_TANDEM:
                dao = new XTandemDAO(new File(sourcePath));
                break;
            case MZML:
                dao = new MzmlDAO(new File(sourcePath));
                break;
            case MZIDENTML:
                dao = new MzIdentmlDAO(new File(sourcePath));
                break;
            case MZXML:
                dao = new MzXmlDAO(new File(sourcePath));
                break;
            case MZDATA:
                dao = new MzDataDAO(new File(sourcePath));
                break;
            case MSGF:
                dao = new MsgfDao(new File(sourcePath));
                break;
            case PRIDE:
                //todo
                throw new UnsupportedOperationException("No DAO defined for " + format);
            default:
                throw new UnsupportedOperationException("No DAO defined for " + format);
        }

        if (config != null) {
            dao.setConfiguration(config);
        }
        return dao;

    }

    public Collection<DAOProperty> getSupportedProperties(DAO_FORMAT format) {

        switch (format) {
            case MASCOT:
                return MascotDAO.getSupportedProperties();
            case MGF:
                return MgfDAO.getSupportedProperties();
            case DTA:
                return DtaDAO.getSupportedProperties();
            case PKL:
                return PklDAO.getSupportedProperties();
            case MS2:
                return Ms2DAO.getSupportedProperties();
            case X_TANDEM:
                return XTandemDAO.getSupportedProperties();
            case MZML:
                return MzmlDAO.getSupportedProperties();
            case MZIDENTML:
                return MzIdentmlDAO.getSupportedProperties();
            case MZXML:
                return MzXmlDAO.getSupportedProperties();
            case MZDATA:
                return MzDataDAO.getSupportedProperties();
            case MSGF:
                return MsgfDao.getSupportedProperties();
            case PRIDE:
                //todo
                return Collections.emptyList();
            default:
                throw new UnsupportedOperationException("No DAO defined for " + format);
        }

    }

}
