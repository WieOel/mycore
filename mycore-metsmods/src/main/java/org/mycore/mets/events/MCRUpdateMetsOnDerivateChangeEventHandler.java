package org.mycore.mets.events;

import org.apache.log4j.Logger;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.events.MCREvent;
import org.mycore.common.events.MCREventHandlerBase;
import org.mycore.datamodel.ifs.MCRFile;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.mets.tools.MetsSave;

/**
 * EventHandler updates the mets.xml after a file is added to an existing
 * derivate.
 * 
 * @author shermann
 */
public class MCRUpdateMetsOnDerivateChangeEventHandler extends MCREventHandlerBase {
    private static final Logger LOGGER = Logger.getLogger(MCRUpdateMetsOnDerivateChangeEventHandler.class);

    @Override
    protected void handleFileCreated(MCREvent evt, MCRFile file) {
        String mets = MCRConfiguration.instance().getString(" MCR.Mets.Filename", "mets.xml");
        if (file.getName().equals(mets)) {
            return;
        }

        MCRDerivate owner = MCRMetadataManager.retrieveMCRDerivate(MCRObjectID.getInstance(file.getOwnerID()));
        if (!owner.receiveDirectoryFromIFS().hasChild(mets)) {
            return;
        }

        try {
            MetsSave.update(owner, file.getName());
        } catch (Exception e) {
            LOGGER.error("Error while updating mets file", e);
        }
    }
}
