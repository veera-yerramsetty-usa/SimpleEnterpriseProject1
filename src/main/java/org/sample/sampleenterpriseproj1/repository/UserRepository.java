package org.sample.sampleenterpriseproj1.repository;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.unsigned_indices_model.FetchResponseWithUnsignedIndices;
import io.pinecone.unsigned_indices_model.VectorWithUnsignedIndices;
import org.openapitools.db_data.client.model.ListItem;
import org.openapitools.db_data.client.model.ListResponse;
import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.beans.factory.annotation.Value as SpringValue;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserRepository {

    private final Index index;
    private static final int VECTOR_DIMENSION = 8;

    public UserRepository(Pinecone pinecone, @SpringValue("${pinecone.index-name}") String indexName) {
        this.index = pinecone.getIndexConnection(indexName);
    }

    public void save(User user) {
        Struct metadata = Struct.newBuilder()
                .putFields("name", Value.newBuilder().setStringValue(nullSafe(user.getName())).build())
                .putFields("email", Value.newBuilder().setStringValue(nullSafe(user.getEmail())).build())
                .putFields("password", Value.newBuilder().setStringValue(nullSafe(user.getPassword())).build())
                .putFields("phone", Value.newBuilder().setStringValue(nullSafe(user.getPhone())).build())
                .putFields("address", Value.newBuilder().setStringValue(nullSafe(user.getAddress())).build())
                .putFields("role", Value.newBuilder().setStringValue(nullSafe(user.getRole())).build())
                .putFields("createdAt", Value.newBuilder().setStringValue(nullSafe(Objects.toString(user.getCreatedAt(), ""))).build())
                .putFields("updatedAt", Value.newBuilder().setStringValue(nullSafe(Objects.toString(user.getUpdatedAt(), ""))).build())
                .putFields("deleted", Value.newBuilder().setBoolValue(false).build())
                .build();

        // Use a dummy vector since we're using Pinecone for storage, not similarity search
        List<Float> dummyVector = Collections.nCopies(VECTOR_DIMENSION, 0.0f);
        index.upsert(user.getId(), dummyVector, null, null, metadata, "");
    }

    public Optional<User> findById(String id) {
        FetchResponseWithUnsignedIndices response = index.fetch(List.of(id), "");
        Map<String, VectorWithUnsignedIndices> vectors = response.getVectorsMap();

        if (!vectors.containsKey(id)) {
            return Optional.empty();
        }

        VectorWithUnsignedIndices vector = vectors.get(id);
        Struct metadata = vector.getMetadata();

        if (metadata.getFieldsOrDefault("deleted", Value.newBuilder().setBoolValue(false).build()).getBoolValue()) {
            return Optional.empty();
        }

        return Optional.of(toUser(id, metadata));
    }

    public List<User> findAll() {
        ListResponse listResponse = index.list("");
        List<User> users = new ArrayList<>();

        if (listResponse.getVectors() == null) {
            return users;
        }

        List<String> ids = listResponse.getVectors().stream()
                .map(ListItem::getId)
                .toList();

        if (ids.isEmpty()) {
            return users;
        }

        FetchResponseWithUnsignedIndices response = index.fetch(ids, "");

        for (Map.Entry<String, VectorWithUnsignedIndices> entry : response.getVectorsMap().entrySet()) {
            Struct metadata = entry.getValue().getMetadata();
            boolean deleted = metadata.getFieldsOrDefault("deleted", Value.newBuilder().setBoolValue(false).build()).getBoolValue();
            if (!deleted) {
                users.add(toUser(entry.getKey(), metadata));
            }
        }

        return users;
    }

    public void deleteById(String id) {
        findById(id).ifPresent(user -> {
            user.setUpdatedAt(LocalDateTime.now());
            Struct metadata = Struct.newBuilder()
                    .putFields("name", Value.newBuilder().setStringValue(nullSafe(user.getName())).build())
                    .putFields("email", Value.newBuilder().setStringValue(nullSafe(user.getEmail())).build())
                    .putFields("password", Value.newBuilder().setStringValue(nullSafe(user.getPassword())).build())
                    .putFields("phone", Value.newBuilder().setStringValue(nullSafe(user.getPhone())).build())
                    .putFields("address", Value.newBuilder().setStringValue(nullSafe(user.getAddress())).build())
                    .putFields("role", Value.newBuilder().setStringValue(nullSafe(user.getRole())).build())
                    .putFields("createdAt", Value.newBuilder().setStringValue(nullSafe(Objects.toString(user.getCreatedAt(), ""))).build())
                    .putFields("updatedAt", Value.newBuilder().setStringValue(nullSafe(Objects.toString(user.getUpdatedAt(), ""))).build())
                    .putFields("deleted", Value.newBuilder().setBoolValue(true).build())
                    .build();

            List<Float> dummyVector = Collections.nCopies(VECTOR_DIMENSION, 0.0f);
            index.upsert(id, dummyVector, null, null, metadata, "");
        });
    }

    private User toUser(String id, Struct metadata) {
        User user = new User();
        user.setId(id);
        user.setName(getStringField(metadata, "name"));
        user.setEmail(getStringField(metadata, "email"));
        user.setPassword(getStringField(metadata, "password"));
        user.setPhone(getStringField(metadata, "phone"));
        user.setAddress(getStringField(metadata, "address"));
        user.setRole(getStringField(metadata, "role"));
        String createdAt = getStringField(metadata, "createdAt");
        if (!createdAt.isEmpty()) {
            user.setCreatedAt(LocalDateTime.parse(createdAt));
        }
        String updatedAt = getStringField(metadata, "updatedAt");
        if (!updatedAt.isEmpty()) {
            user.setUpdatedAt(LocalDateTime.parse(updatedAt));
        }
        return user;
    }

    private String getStringField(Struct metadata, String field) {
        Value value = metadata.getFieldsOrDefault(field, Value.newBuilder().setStringValue("").build());
        return value.getStringValue();
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
