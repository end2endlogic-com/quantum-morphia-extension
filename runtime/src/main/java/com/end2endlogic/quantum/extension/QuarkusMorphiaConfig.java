package com.end2endlogic.quantum.extension;

import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import com.end2endlogic.quantum.extension.runtime.MapperConfig;
import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

import java.util.HashMap;
import java.util.Map;

@ConfigMapping(prefix="quarkus.morphia")
@ConfigRoot(phase = RUN_TIME)
public interface QuarkusMorphiaConfig {

    /**
     * The default Mapper configuration.
     */
    @ConfigItem(name = ConfigItem.PARENT)
    public MapperConfig defaultMapperConfig = new MapperConfig();

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
    @ConfigItem(name = ConfigItem.PARENT)
    public Map<String, MapperConfig> mapperConfigs = new HashMap<>() {};

   /* public MapperConfig getMapperConfig(String clientName) {
        return MongoClientBeanUtil.isDefault(clientName)
            ? defaultMapperConfig
            : mapperConfigs.get(clientName);
    } */
}
