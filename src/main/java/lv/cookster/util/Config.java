package lv.cookster.util;

import lv.cookster.rest.CategoryService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;

/**
 * Created by Rihards on 02.05.2015.
 */
@ApplicationPath("/rest")
public class Config extends ResourceConfig {

    public Config() {
        super(CategoryService.class);
        register(RolesAllowedDynamicFeature.class);
    }

}
