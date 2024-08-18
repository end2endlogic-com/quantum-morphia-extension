package com.end2endlogic.quantum.extension;

import com.end2endlogic.quantum.extension.runtime.MapperConfig;
import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.quarkus.runtime.annotations.Recorder;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Recorder
public class MorphiaRecorder {

    public Supplier<Datastore> datastoreSupplier(
        Supplier<MongoClient> mongoClientSupplier,
        MorphiaConfig morphiaConfig,
        List<String> entities,
        String clientName
    ) {
        return () -> {
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
    }
}
