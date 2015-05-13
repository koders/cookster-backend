package lv.cookster.util;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

/**
 * Created by Rihards on 02.05.2015.
 */
@ApplicationPath("/rest")
public class Config extends ResourceConfig {

    public Config() {
        packages("lv.cookster");
//        register(MultiPartConfigProvider.class);
        register(RolesAllowedDynamicFeature.class);
        register(MultiPartFeature.class);

        // Enable Tracing support.
        property(ServerProperties.TRACING, "ALL");
    }

}
