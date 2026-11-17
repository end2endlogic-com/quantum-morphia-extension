package com.end2endlogic.quantum.extension;


import com.end2endlogic.quantum.extension.runtime.MapperConfig;
import com.mongodb.client.MongoClient;
import dev.morphia.MorphiaDatastore;
import dev.morphia.config.ManualMorphiaConfig;
import dev.morphia.config.MorphiaConfig;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Recorder
public class MorphiaRecorder {

    private static final org.jboss.logging.Logger LOG = org.jboss.logging.Logger.getLogger(MorphiaRecorder.class);

    private final RuntimeValue<QuarkusMorphiaConfig> quarkusMorphiaConfigValue;

    public MorphiaRecorder(RuntimeValue<QuarkusMorphiaConfig> quarkusMorphiaConfig) {
        this.quarkusMorphiaConfigValue = quarkusMorphiaConfig;
    }

    /**
     * Whether a class belongs to a configured package, with the meaning Morphia gives the entry when it
     * scans: {@code a.b} is that package only, {@code a.b.*} is that package and everything below it.
     */
    static boolean inPackage(String configuredPackage, String className) {
        String entry = configuredPackage.trim();
        int lastDot = className.lastIndexOf('.');
        String classPackage = lastDot < 0 ? "" : className.substring(0, lastDot);
        if (entry.endsWith(".*")) {
            String base = entry.substring(0, entry.length() - 2);
            return base.isEmpty() || classPackage.equals(base) || classPackage.startsWith(base + ".");
        }
        return classPackage.equals(entry);
    }

    public Supplier<MorphiaDatastore> datastoreSupplier(
        Supplier<MongoClient> mongoClientSupplier,
        List<String> entities,
        String clientName
    ) {

        return () -> {
            QuarkusMorphiaConfig quarkusMorphiaConfig = quarkusMorphiaConfigValue.getValue();
            ClassLoader contextClassLoader = Thread.currentThread()
                    .getContextClassLoader();
            MongoClient mongoClient = mongoClientSupplier.get();
            //MorphiaConfig morphiaConfig = quarkusMorphiaConfig.getMapperConfig(clientName).toMorphiaConfig();
            MapperConfig mapperConfig = quarkusMorphiaConfig.mapperConfigs().get(clientName);
            if (mapperConfig == null) {
                mapperConfig = quarkusMorphiaConfig.defaultMapperConfig();
            }
            MorphiaConfig morphiaConfig =  ManualMorphiaConfig.configure()
                    .collectionNaming(mapperConfig.collectionNaming().convert())
                    .dateStorage(mapperConfig.dateStorage())
                    .discriminator(mapperConfig.discriminator().convert())
                    .discriminatorKey(mapperConfig.discriminatorKey())
                    .enablePolymorphicQueries(mapperConfig.enablePolymorphicQueries())
                    .ignoreFinals(mapperConfig.ignoreFinals())
                    .propertyNaming(mapperConfig.propertyNaming().convert())
                    .storeEmpties(mapperConfig.storeEmpties())
                    .applyCaps(mapperConfig.createCaps())
                    .database(mapperConfig.database())
                    .applyIndexes(mapperConfig.createIndexes())
                    .storeNulls(mapperConfig.storeNulls());
                    if (mapperConfig.packages() != null && !mapperConfig.packages().isEmpty()){
                        morphiaConfig = morphiaConfig.packages(mapperConfig.packages());
                    }

            // The entity types come from the build-time index. They are handed to Morphia as classes so
            // that it maps exactly these and does not scan the classpath again for every configured
            // package. Caps and indexes are applied once by the datastore, after all types are mapped.
            List<String> packages = morphiaConfig.packages();
            Set<Class<?>> entityTypes = new LinkedHashSet<>();
            try {
                for (String mapPackage : packages) {
                    boolean matched = false;
                    for (String type : entities) {
                        if (inPackage(mapPackage, type)) {
                            entityTypes.add(contextClassLoader.loadClass(type));
                            matched = true;
                        }
                    }
                    if (!matched) {
                        // Entities are found through the build-time index only. A package whose module is not
                        // indexed (no beans.xml, Jandex index or quarkus.index-dependency entry) lands here.
                        LOG.warnf("quarkus.morphia.packages entry '%s' matched no indexed entity class; "
                                + "if it holds entities, the module that provides them is not indexed", mapPackage);
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            MorphiaDatastore datastore = new MorphiaDatastore(mongoClient, morphiaConfig, entityTypes);
                       /* if (morphiaConfig.applyCaps()) {

                        } */

            return datastore;
        };


       /* return () -> {
            MapperConfig config = morphiaConfig.getMapperConfig(clientName);
            Datastore datastore = Morphia.createDatastore(
                mongoClientSupplier.get(),
                config.database,
                config.toMapperOptions()
            );
            ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();
            config.packages.ifPresent(packages -> {
                try {
                    for (String mapPackage : packages) {
                        Pattern pattern = Pattern.compile(
                            mapPackage.endsWith(".*")
                                ? mapPackage
                                : mapPackage + ".[A-Z]+"
                        );
                        for (String type : entities) {
                            if (pattern.matcher(type).lookingAt()) {
                                datastore
                                    .getMapper()
                                    .map(contextClassLoader.loadClass(type));
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                if (config.createValidators) {
                    datastore.enableDocumentValidation();
                }
                if (config.createCaps) {
                    datastore.ensureCaps();
                }
                if (config.createIndexes) {
                    datastore.ensureIndexes();
                }
            });
            return datastore;
        };

             */
    }
}
