package uk.ac.ebi.pride.tools.converter.dao_omssa_txt.parsers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.ac.ebi.pride.tools.converter.dao_omssa_txt.model.OmssaPTM;
import uk.ac.ebi.pride.tools.converter.report.model.PTM;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: rcote
 * Date: 24/10/12
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class OmssaPTMFileParser {

    private HashMap<String, OmssaPTM> omssaPTMs = new HashMap<String, OmssaPTM>();

    public OmssaPTMFileParser(File ptmXmlFile) {

        try {
            //get the factory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setValidating(false);
            dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setAttribute("http://xml.org/sax/features/validation", false);

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            NodeList nodes, modNodes, residueNodes, tempNodes;
            String modName = "", modUnimodAc, modPsiModName;
            Vector<String> modResidues;

            Integer modNumber = -1;
            Double modMonoMass = 0.0;


            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(ptmXmlFile);

            //get the root elememt
            Element docEle = dom.getDocumentElement();

            nodes = docEle.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {

                if (nodes.item(i).getNodeName().equalsIgnoreCase("MSModSpec")) {

                    modNodes = nodes.item(i).getChildNodes();
                    modNumber = -1;
                    modName = "";
                    modUnimodAc = null;
                    modPsiModName = null;
                    modMonoMass = 0.0;
                    modResidues = new Vector<String>();

                    for (int j = 0; j < modNodes.getLength(); j++) {

                        if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_mod")) {

                            tempNodes = modNodes.item(j).getChildNodes();

                            for (int m = 0; m < tempNodes.getLength(); m++) {
                                if (tempNodes.item(m).getNodeName().equalsIgnoreCase("MSMod")) {
                                    modNumber = new Integer(tempNodes.item(m).getTextContent());
                                }
                            }
                        } else if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_name")) {
                            modName = modNodes.item(j).getTextContent();
                        } else if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_monomass")) {
                            modMonoMass = new Double(modNodes.item(j).getTextContent());
                        } else if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_unimod")) {
                            modUnimodAc = modNodes.item(j).getTextContent();
                        } else if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_psi-ms")) {
                            modPsiModName = modNodes.item(j).getTextContent();
                        } else if (modNodes.item(j).getNodeName().equalsIgnoreCase("MSModSpec_residues")) {
                            residueNodes = modNodes.item(j).getChildNodes();

                            modResidues = new Vector<String>();

                            for (int m = 0; m < residueNodes.getLength(); m++) {
                                if (residueNodes.item(m).getNodeName().equalsIgnoreCase("MSModSpec_residues_E")) {
                                    modResidues.add(residueNodes.item(m).getTextContent());
                                }
                            }
                        }
                    }

                    if (modMonoMass == 0.0) {
                        modMonoMass = null;
                    }

                    omssaPTMs.put(modName, new OmssaPTM(modNumber, modName, modMonoMass, modResidues, modUnimodAc, modPsiModName));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse OMSSA PTM XML file: " + e.getMessage(), e);
        }
    }

    public HashMap<String, OmssaPTM> getOmssaPTMs() {
        return omssaPTMs;
    }

    public static Map<String, PTM> createFixedPTMs(Map<Character, Double> fixedPtms) {

        Map<String, PTM> retval = new HashMap<String, PTM>();

        if (fixedPtms != null && !fixedPtms.isEmpty()) {
            for (Map.Entry<Character, Double> entry : fixedPtms.entrySet()) {

                //create PTM
                PTM reportPTM = new PTM();
                reportPTM.setFixedModification(true);
                reportPTM.setSearchEnginePTMLabel(entry.getValue().toString() + "@" + entry.getKey().toString());
                reportPTM.getModMonoDelta().add(entry.getValue().toString());
                reportPTM.setResidues(entry.getKey().toString());

                retval.put(entry.getKey().toString(), reportPTM);
            }
        }

        return retval;

    }
}
