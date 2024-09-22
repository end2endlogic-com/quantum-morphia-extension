package com.end2endlogic.quantum.extension;



import com.end2endlogic.quantum.extension.runtime.MapperConfig;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

import java.util.HashMap;
import java.util.Map;

@ConfigMapping(prefix="quarkus.morphia")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface QuarkusMorphiaConfig {

    /**
     * The default Mapper configuration.
     */
    @WithParentName
    MapperConfig defaultMapperConfig();

    /**
     * Configures additional {@code @Mapper} configurations.
     * <p>
     * each cluster have a unique identifier witch must be identified to select the right connection.
     * example:
     *
     * <pre>
     * {@code
     * quarkus.morphia.cluster1.discriminatorKey = className
     * quarkus.morphia.cluster2.discriminatorKey = _type
     * }
     * </pre>
     *
     * And then use annotations above the instances of {@code @MongoClient} to indicate which instance we are going to use
     *
     * <pre>
     * {
     *     &#64;code
     *     &#64;Inject
     *     &#64;MongoClientName("cluster1")
     *     Datastore datastore;
     * }
     * </pre>
     */
    Map<String, MapperConfig> mapperConfigs();

   /* public MapperConfig getMapperConfig(String clientName) {
        return MongoClientBeanUtil.isDefault(clientName)
            ? defaultMapperConfig
            : mapperConfigs.get(clientName);
    } */
}
