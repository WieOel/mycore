/**
 * 
 */
package org.mycore.mods;

import java.text.MessageFormat;
import java.util.Collections;

import org.jdom.Element;
import org.jdom.Namespace;
import org.mycore.datamodel.metadata.MCRMetaElement;
import org.mycore.datamodel.metadata.MCRMetaXML;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * @author Thomas Scheffler
 *
 */
public class MCRMODSWrapper {

    public static final String MODS_OBJECT_TYPE = "mods";

    public static final Namespace MODS_NS = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");

    public static MCRObject wrapMODSDocument(Element modsDefinition, String projectID) {
        MCRObjectID objID = MCRObjectID.getInstance(MessageFormat.format("{0}_{1}_00000008", projectID, MODS_OBJECT_TYPE));
        MCRObject obj = new MCRObject();
        obj.setId(objID);
        obj.setSchema("datamodel-mods.xsd");
        MCRMetaXML modsContainer = new MCRMetaXML("modsContainer", null, 0);
        modsContainer.addContent(modsDefinition);
        MCRMetaElement defModsContainer = new MCRMetaElement(MCRMetaXML.class, "def.modsContainer", false, true, Collections.nCopies(1, modsContainer));
        obj.getMetadata().setMetadataElement(defModsContainer);
        return obj;
    }
}
