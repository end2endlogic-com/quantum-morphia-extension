package com.end2endlogic.quantum.extension.deployment;

import com.end2endlogic.quantum.extension.QuarkusMorphiaConfig;
import dev.morphia.MorphiaDatastore;
import dev.morphia.annotations.*;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

import static io.quarkus.mongodb.runtime.MongoClientBeanUtil.DEFAULT_MONGOCLIENT_NAME;
import static java.util.List.of;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

import org.bson.codecs.Codec;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;
import org.jetbrains.annotations.NotNull;

import dev.morphia.MorphiaDatastore;

import dev.morphia.mapping.codec.references.ReferenceCodec;
import dev.morphia.config.MorphiaConfig;

import com.end2endlogic.quantum.extension.MorphiaRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.mongodb.MongoClientName;
import io.quarkus.mongodb.deployment.MongoClientNameBuildItem;
import io.quarkus.mongodb.runtime.MongoClientBeanUtil;
import io.quarkus.mongodb.runtime.MongoClientRecorder;
import io.quarkus.mongodb.runtime.MongodbConfig;

@SuppressWarnings("removal")
class QuantumExtensionProcessor {

    private static final List<DotName> MAPPED_TYPE_ANNOTATIONS = of(
        DotName.createSimple(Entity.class.getName())
    );

    private static final String FEATURE = "quantum-extension";
    public static final DotName MONGO_CLIENT_NAME = DotName.createSimple(
        MongoClientName.class.getName()
    );
    private static final String ENTITY_MODEL_IMPORTER =
        "dev.morphia.mapping.EntityModelImporter";

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public void datastoreRecorder(
            MongoClientRecorder clientRecorder,
            MongodbConfig mongodbConfig,
            MorphiaRecorder recorder,
            QuarkusMorphiaConfig config,
            MorphiaEntitiesBuildItem entitiesBuildItem,
            List<MongoClientNameBuildItem> mongoClientNames,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<ServiceProviderBuildItem> items
    ) {
        syntheticBeanBuildItemBuildProducer.produce(
            SyntheticBeanBuildItem.configure(MorphiaDatastore.class)
                .scope(ApplicationScoped.class)
                .supplier(
                    recorder.datastoreSupplier(
                        clientRecorder.mongoClientSupplier(
                            DEFAULT_MONGOCLIENT_NAME,
                            mongodbConfig
                        ),
                        config,
                        entitiesBuildItem.getNames(),
                        DEFAULT_MONGOCLIENT_NAME
                    )
                )
                .setRuntimeInit()
                .done()
        );

        for (MongoClientNameBuildItem mongoClientName : mongoClientNames) {
            String clientName = mongoClientName.getName();

            SyntheticBeanBuildItem.ExtendedBeanConfigurator configurator =
                SyntheticBeanBuildItem.configure(MorphiaDatastore.class)
                    .scope(ApplicationScoped.class)
                    .supplier(
                        recorder.datastoreSupplier(
                            clientRecorder.mongoClientSupplier(
                                clientName,
                                mongodbConfig
                            ),
                            config,
                            entitiesBuildItem.getNames(),
                            clientName
                        )
                    )
                    .setRuntimeInit();

            if (MongoClientBeanUtil.isDefault(clientName)) {
                configurator.addQualifier(Default.class);
            } else {
                configurator
                    .addQualifier()
                    .annotation(DotNames.NAMED)
                    .addValue("value", clientName)
                    .done();
                if (mongoClientName.isAddQualifier()) {
                    configurator
                        .addQualifier()
                        .annotation(MONGO_CLIENT_NAME)
                        .addValue("value", clientName)
                        .done();
                }
            }

            syntheticBeanBuildItemBuildProducer.produce(configurator.done());
        }
        items.produce(
            ServiceProviderBuildItem.allProvidersFromClassPath(
                ENTITY_MODEL_IMPORTER
            )
        );
    }

    @BuildStep
    public void registerReflectiveItems(
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
        BuildProducer<GeneratedResourceBuildItem> resourceItems
    ) throws IOException {
        registerWrapperArrays(reflectiveClasses);
        Index index = indexJar();
        registerImplementors(
            index,
            reflectiveClasses,
            DotName.createSimple(Codec.class.getName())
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, ReferenceCodec.class)
        );
        registerAnnotations(reflectiveClasses);
    }

    private void registerAnnotations(
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses
    ) {
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, AlsoLoad.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, CappedAt.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Collation.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Entity.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, EntityListeners.class)
        );
        reflectiveClasses.produce(
                new ReflectiveClassBuildItem(true, true, ExternalEntity.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Field.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Handler.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Id.class)
        );
        reflectiveClasses.produce(
                new ReflectiveClassBuildItem(true, true, IdField.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, IdGetter.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Indexed.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(
                true,
                true,
                dev.morphia.annotations.Index.class
            )
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Indexes.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, IndexOptions.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, LoadOnly.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, PostLoad.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, PostPersist.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, PreLoad.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, PrePersist.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Property.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Reference.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Text.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Transient.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Validation.class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, true, Version.class)
        );
    }

    @NotNull
    private Index indexJar() throws IOException {
        URL location =
            MorphiaDatastore.class.getProtectionDomain().getCodeSource().getLocation();
        Indexer indexer = new Indexer();
        JarIndexer.createJarIndex(
            new File(location.getFile()),
            indexer,
            false,
            false,
            false
        );
        return indexer.complete();
    }

    private void registerImplementors(
        Index index,
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
        DotName type
    ) {
        Set<ClassInfo> set = index.getAllKnownImplementors(type);
        set.forEach(codec ->
            registerImplementors(index, reflectiveClasses, codec.name())
        );
        registerSubclasses(index, reflectiveClasses, type);
    }

    @BuildStep
    public MorphiaEntitiesBuildItem registerMappedTypes(
        CombinedIndexBuildItem indexBuildItem,
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses
    ) {
        ArrayList<String> list = new ArrayList<>();

        IndexView index = indexBuildItem.getIndex();
        for (DotName annotation : MAPPED_TYPE_ANNOTATIONS) {
            var classes = index
                .getKnownClasses()
                .stream()
                .filter(info -> info.classAnnotation(annotation) != null)
                .collect(Collectors.toList());
            classes.forEach(info -> {
                reflectiveClasses.produce(
                    new ReflectiveClassBuildItem(
                        true,
                        true,
                        info.name().toString()
                    )
                );
                list.add(info.name().toString());
            });
        }

        return new MorphiaEntitiesBuildItem(list);
    }

    private void registerSubclasses(
        Index index,
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
        DotName type
    ) {
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, type.toString())
        );
        index
            .getAllKnownSubclasses(type)
            .forEach(codec -> registerSubclasses(index, reflectiveClasses, type)
            );
    }

    private void registerWrapperArrays(
        BuildProducer<ReflectiveClassBuildItem> reflectiveClasses
    ) {
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Boolean[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Byte[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Character[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Double[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Float[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Integer[].class)
        );
        reflectiveClasses.produce(
            new ReflectiveClassBuildItem(true, false, Long[].class)
        );
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
