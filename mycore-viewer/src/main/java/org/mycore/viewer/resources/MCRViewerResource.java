package org.mycore.viewer.resources;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.util.MCRServletContentHelper;
import org.mycore.common.xml.MCRLayoutService;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.jersey.MCRJerseyUtil;
import org.mycore.services.i18n.MCRTranslation;
import org.mycore.viewer.configuration.MCRIviewACLProvider;
import org.mycore.viewer.configuration.MCRIviewDefaultACLProvider;
import org.mycore.viewer.configuration.MCRViewerConfiguration;
import org.mycore.viewer.configuration.MCRViewerConfigurationStrategy;
import org.mycore.viewer.configuration.MCRViewerDefaultConfigurationStrategy;
import org.xml.sax.SAXException;

/**
 * Base resource for the mycore image viewer.
 * 
 * @author Matthias Eichner
 * @author Sebastian Hofmann
 */
@Path("/viewer")
public class MCRViewerResource {

    private static final MCRIviewACLProvider IVIEW_ACL_PROVDER = MCRConfiguration.instance()
        .<MCRIviewACLProvider> getInstanceOf("MCR.Viewer.MCRIviewACLProvider",
            MCRIviewDefaultACLProvider.class.getName());

    private static final String JSON_CONFIG_ELEMENT_NAME = "json";

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("{derivate}{path: (/[^?#]*)?}")
    public void show(@Context HttpServletRequest request, @Context HttpServletResponse response,
        @Context ServletContext context, @Context ServletConfig config) throws Exception {
        MCRContent content = getContent(request, response);
        boolean serveContent = MCRServletContentHelper.isServeContent(request);
        MCRServletContentHelper.serveContent(content, request, response, context,
            MCRServletContentHelper.buildConfig(config), serveContent);
    }

    /**
     * Builds the jdom configuration response document.
     * 
     * @param config the mycore configuration object
     * @return jdom configuration object
     */
    private static Document buildResponseDocument(MCRViewerConfiguration config)
        throws JDOMException, IOException, SAXException, JAXBException {
        String configJson = config.toJSON();
        Element startIviewClientElement = new Element("IViewConfig");
        Element configElement = new Element(JSON_CONFIG_ELEMENT_NAME);
        startIviewClientElement.addContent(configElement);
        startIviewClientElement.addContent(config.toXML().asXML().getRootElement().detach());
        configElement.addContent(configJson);
        Document startIviewClientDocument = new Document(startIviewClientElement);
        return startIviewClientDocument;
    }

    protected MCRContent getContent(final HttpServletRequest req, final HttpServletResponse resp)
        throws JDOMException, IOException, SAXException, JAXBException, TransformerException {
        // get derivate id from request object
        String derivate = MCRViewerConfiguration.getDerivate(req);
        if (derivate == null) {
            MCRJerseyUtil.throwException(Status.BAD_REQUEST, "Could not locate derivate identifer in path.");
        }
        // get mycore object id
        final MCRObjectID derivateID = MCRObjectID.getInstance(derivate);
        if (!MCRMetadataManager.exists(derivateID)) {
            String errorMessage = MCRTranslation.translate("component.viewer.MCRIViewClientServlet.object.not.found",
                derivateID);
            MCRJerseyUtil.throwException(Status.NOT_FOUND, errorMessage);
        }
        // check permission
        if (IVIEW_ACL_PROVDER != null && !IVIEW_ACL_PROVDER.checkAccess(req.getSession(), derivateID)) {
            String errorMessage = MCRTranslation.translate("component.viewer.MCRIViewClientServlet.noRights",
                derivateID);
            MCRJerseyUtil.throwException(Status.UNAUTHORIZED, errorMessage);
        }
        // build configuration object
        MCRViewerConfigurationStrategy configurationStrategy = MCRConfiguration.instance()
            .getInstanceOf("MCR.Viewer.configuration.strategy", new MCRViewerDefaultConfigurationStrategy());
        MCRJDOMContent source = new MCRJDOMContent(buildResponseDocument(configurationStrategy.get(req)));
        return MCRLayoutService.instance().getTransformedContent(req, resp, source);
    }

}
