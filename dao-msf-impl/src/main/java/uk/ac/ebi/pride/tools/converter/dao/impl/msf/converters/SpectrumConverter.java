/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.pride.tools.converter.dao.impl.msf.converters;

import com.compomics.thermo_msf_parser.msf.Peak;
import com.compomics.thermo_msf_parser.msf.SpectrumLowMem;
import com.compomics.thermo_msf_parser.msf.SpectrumLowMemController;
import com.compomics.thermo_msf_parser.msf.ScanEventLowMemController;
import com.compomics.thermo_msf_parser.msf.enums.ActivationType;
import java.sql.Connection;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ebi.pride.jaxb.model.*;
import uk.ac.ebi.pride.tools.converter.dao.DAOCvParams;
import uk.ac.ebi.pride.tools.converter.dao.impl.AbstractDAOImpl;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.MsfDao;
import uk.ac.ebi.pride.tools.converter.dao.impl.msf.terms.MsfCvTermReference;

/**
 *
 * @author toorn101
 */
public class SpectrumConverter {
    
    private static SpectrumLowMemController spectra = new SpectrumLowMemController();
    private static Vector<Peak> peaks;
    private static ScanEventLowMemController scanEvents = new ScanEventLowMemController();
    /**
     * Make the conversion of a spectrum found in the msf parser and the PRIDE
     * converter spectrum
     *
     * @param msfSpectrum
     * @return
     * @throws an exception when there is a problem with the connection
     */
    public static Spectrum convert(SpectrumLowMem msfSpectrum, Connection msfFileConnection) throws Exception {
        Spectrum prideConverterSpectrum = new Spectrum();
        prideConverterSpectrum.setId(msfSpectrum.getSpectrumId());
        spectra.createSpectrumXMLForSpectrum(msfSpectrum);
        // TreeMap is sorted by keys, usable for getting the range. Assumption is, no two m/z in the list are the same.
        TreeMap<Double, Double> mzint = new TreeMap<Double, Double>();
        // Read peak by peak, fill treemap
        peaks = spectra.getMSMSPeaks(msfSpectrum.getSpectrumXML());
        
        for (Peak peak : peaks) {
            mzint.put(peak.getX(), peak.getY());
        }

        Double lowMz = mzint.firstKey();
        Double highMz = mzint.lastKey();

        // create the byte arrays
        byte[] massesBytes = AbstractDAOImpl.doubleCollectionToByteArray(mzint.keySet());
        byte[] intenBytes = AbstractDAOImpl.doubleCollectionToByteArray(mzint.values());

        // create the intensity array
        Data intenData = new Data();
        intenData.setEndian("little");
        intenData.setLength(intenBytes.length);
        intenData.setPrecision("64"); // doubles are 64 bit in java
        intenData.setValue(intenBytes);

        IntenArrayBinary intenArrayBin = new IntenArrayBinary();
        intenArrayBin.setData(intenData);

        // create the mass data array
        Data massData = new Data();
        massData.setEndian("little");
        massData.setLength(massesBytes.length);
        massData.setPrecision("64");
        massData.setValue(massesBytes);

        MzArrayBinary massArrayBinary = new MzArrayBinary();
        massArrayBinary.setData(massData);

        // store the mz and intensities in the spectrum
        prideConverterSpectrum.setIntenArrayBinary(intenArrayBin);
        prideConverterSpectrum.setMzArrayBinary(massArrayBinary);

        // add the spectrum description
        prideConverterSpectrum.setSpectrumDesc(compileSpectrumDescription(msfSpectrum, lowMz, highMz, msfFileConnection));

        return prideConverterSpectrum;
    }

    /**
     * Generates the SpectrumDesc object for the passed spectrum. <br> A charge
     * state is only reported at the peptide level
     *
     * @param msfSpectrum
     * @param lowMz predetermined lower mz value
     * @param highMz predetermined upper mz value
     * @return The SpectrumDesc object for the given spectrum
     */
    public static SpectrumDesc compileSpectrumDescription(com.compomics.thermo_msf_parser.msf.SpectrumLowMem msfSpectrum, Double lowMz, Double highMz, Connection msfFileConnection) {
        // initialize the spectrum description
        SpectrumDesc description = new SpectrumDesc();

        // create the spectrumSettings/spectrumInstrument (mzRangeStop, mzRangeStart, msLevel)
        SpectrumSettings settings = new SpectrumSettings();
        SpectrumInstrument instrument = new SpectrumInstrument();
        int msLevel = scanEvents.getScanEventForSpectrum(msfSpectrum,msfFileConnection).getMSLevel();
        instrument.setMsLevel(msLevel);

        // Use pre-determined range
        instrument.setMzRangeStart(lowMz.floatValue());
        instrument.setMzRangeStop(highMz.floatValue());


        // set the spectrum settings
        settings.setSpectrumInstrument(instrument);
        description.setSpectrumSettings(settings);


        // Precursors

        PrecursorList precursorList = new PrecursorList();

        // currently, there's only one precursor supported
        precursorList.setCount(1);

        Precursor precursor = new Precursor();
        precursor.setMsLevel(msLevel - 1);

        Spectrum spec = new Spectrum();
        spec.setId(0);
        precursor.setSpectrum(spec);

        Param ionSelection = new Param();

        Double precursorIntensity = 0.0;
        try {
            precursorIntensity = spectra.getFragmentedMsPeak(msfSpectrum.getSpectrumXML()).getY();
        } catch (Exception ex) {
            Logger.getLogger(MsfDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double precursorMz =
                msfSpectrum.getMz();
        String retentionTime = new Double(msfSpectrum.getRetentionTime()).toString();

        if (precursorIntensity != 0) {
            ionSelection.getCvParam().add(MsfCvTermReference.PSI_INTENSITY.getJaxbParam(precursorIntensity.toString()));
        }

        if (precursorMz != 0) {
            ionSelection.getCvParam().add(MsfCvTermReference.PSI_MZ_RATIO.getJaxbParam(precursorMz.toString()));
        }

        if (!retentionTime.isEmpty()) {
            ionSelection.getCvParam().add(DAOCvParams.RETENTION_TIME.getJaxbParam(retentionTime));
        }

        ionSelection.getCvParam().add(MsfCvTermReference.PSI_CHARGE_STATE.getJaxbParam(msfSpectrum.getCharge() + ""));
        
        // save the ionselection
        precursor.setIonSelection(ionSelection);

        // Activation method
        uk.ac.ebi.pride.jaxb.model.Param activation = new uk.ac.ebi.pride.jaxb.model.Param();
        for (ActivationType activationType : scanEvents.getScanEventForSpectrum(msfSpectrum, msfFileConnection).getActivationTypeSet()) {
            activation.getCvParam().add(MsfCvTermReference.valueOf("ACTIVATION_" + activationType.toString().toUpperCase()).getJaxbParam(""));
        }
        
        precursor.setActivation(activation);

        precursorList.getPrecursor().add(precursor);
        description.setPrecursorList(precursorList);

        return description;
    }
}
