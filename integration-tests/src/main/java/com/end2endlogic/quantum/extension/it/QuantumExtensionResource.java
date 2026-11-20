/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.end2endlogic.quantum.extension.it;


import dev.morphia.MorphiaDatastore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import dev.morphia.mapping.codec.pojo.EntityModel;

import java.util.stream.Collectors;

@Path("/hello")
@ApplicationScoped
public class QuantumExtensionResource {
    @Inject
    @Default
    MorphiaDatastore datastore;

    @GET
    public String hello() {
        return "Hello";
    }

    @GET
    @Path("/mapped-types")
    @Produces("application/text")
    public Response mappedTypes() {
        var list = datastore.getMapper().getMappedEntities().stream()
                .map(model -> model.getType().getName()).sorted()
                .collect(Collectors.joining(","));
        return Response.ok(list).build();
    }

    @GET
    @Path("/indexes/{collection}")
    @Produces("application/text")
    public Response indexes(@jakarta.ws.rs.PathParam("collection") String collection) {
        var names = new java.util.ArrayList<String>();
        datastore.getDatabase().getCollection(collection).listIndexes()
                .forEach(index -> names.add(index.getString("name")));
        java.util.Collections.sort(names);
        return Response.ok(String.join(",", names)).build();
    }

    @GET
    @Path("/collections")
    @Produces("application/text")
    public Response collections() {
        var names = datastore.getDatabase().listCollectionNames().into(new java.util.ArrayList<>());
        java.util.Collections.sort(names);
        return Response.ok(String.join(",", names)).build();
    }

    @GET
    @Path("/mapping")
    @Produces("application/text")
    public Response mapping() {
        datastore.getMapper().getConfig().packages().forEach(System.out::println);
        var entities = datastore.getMapper().getMappedEntities();
        var list = entities.stream()
                .map(EntityModel::getName).sorted()
                .collect(Collectors.joining(", "));

        return Response.ok(list).build();
    }


}
