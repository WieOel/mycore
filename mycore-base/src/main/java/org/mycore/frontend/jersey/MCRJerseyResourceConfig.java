package org.mycore.frontend.jersey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.inject.MCRInjectorConfig;

import com.google.inject.Injector;

/**
 * Base entry point for mycore jersey configuration. By default the multipart feature
 * is added and all packages defined in MCR.Jersey.resource.packages are resolved.
 * This does also add the bridge between jersey and guice.
 * 
 * @author Matthias Eichner
 */
public class MCRJerseyResourceConfig extends ResourceConfig {

    private static Logger LOGGER = LogManager.getLogger();

    public MCRJerseyResourceConfig() {
        super();
        LOGGER.info("Loading jersey resource config...");
        // multi part
        this.register(MultiPartFeature.class);

        // register all packages from MCRConfiguratin
        String propertyString = MCRConfiguration.instance().getString("MCR.Jersey.resource.packages",
            "org.mycore.frontend.jersey.resources");
        this.packages(propertyString.split(","));
        LOGGER.info("Scanning jersey resource packages " + propertyString);

        // include mcr jersey feature
        this.packages("org.mycore.frontend.jersey.feature");

        // setup guice bridge
        LOGGER.info("Initialize hk2 - guice bridge...");
        register(new AbstractContainerLifecycleListener() {
            @Override
            public void onStartup(Container container) {
                setupGuiceBridge(container.getApplicationHandler().getServiceLocator());
            }
        });
    }

    /**
     * Adds the binding between guice and hk2. This binding is one directional.
     * You can add guice services into hk2 (jersey) resources. You cannot add
     * a hk2 service into guice.
     * <p>
     * <a href="https://hk2.java.net/guice-bridge/">about the bridge</a>
     * </p>
     * 
     * @param serviceLocator the service locator
     */
    protected void setupGuiceBridge(ServiceLocator serviceLocator) {
        Injector injector = MCRInjectorConfig.injector();
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }

}
