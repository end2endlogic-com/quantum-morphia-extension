package com.end2endlogic.quantum.extension;


import com.end2endlogic.quantum.extension.runtime.MapperConfig;
import com.mongodb.client.MongoClient;
import dev.morphia.MorphiaDatastore;
import dev.morphia.config.ManualMorphiaConfig;
import dev.morphia.config.MorphiaConfig;
import io.quarkus.runtime.annotations.Recorder;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Recorder
public class MorphiaRecorder {

    public Supplier<MorphiaDatastore> datastoreSupplier(
        Supplier<MongoClient> mongoClientSupplier,
        QuarkusMorphiaConfig quarkusMorphiaConfig,
        List<String> entities,
        String clientName
    ) {

        return () -> {
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
                    if (mapperConfig.packages() != null || !mapperConfig.packages().isEmpty()){
                        morphiaConfig = morphiaConfig.packages(mapperConfig.packages());
                    }

            MorphiaDatastore datastore = new MorphiaDatastore(mongoClient, morphiaConfig);
            List<String> packages = morphiaConfig.packages();

                        try {
                            for (String mapPackage : packages) {
                                Pattern pattern = Pattern.compile(
                                        mapPackage.endsWith(".*")
                                                ? mapPackage
                                                : mapPackage + ".[A-Z]+"
                                );
                                for (String type : entities) {
                                    if (pattern.matcher(type).lookingAt()) {
                                        Class c =contextClassLoader.loadClass(type);
                                        datastore.getMapper()
                                                .mapEntity(c);
                                        if (morphiaConfig.applyIndexes()) {
                                            datastore.ensureIndexes(c);
                                        }
                                    }
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                        if (morphiaConfig.applyDocumentValidations()) {
                            datastore.enableDocumentValidation();
                        }
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
