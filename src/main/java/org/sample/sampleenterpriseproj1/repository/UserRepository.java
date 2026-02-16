package org.sample.sampleenterpriseproj1.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.sample.sampleenterpriseproj1.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final InfluxDBClient influxDBClient;
    private final String bucket;

    public UserRepository(InfluxDBClient influxDBClient, @Value("${influxdb.bucket}") String bucket) {
        this.influxDBClient = influxDBClient;
        this.bucket = bucket;
    }

    public void save(User user) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.NS, user);
    }

    public Optional<User> findById(String id) {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String query = String.format(
                "from(bucket: \"%s\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"users\" and r.id == \"%s\") |> last() |> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                bucket, id);
        List<User> users = queryApi.query(query, User.class);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public List<User> findAll() {
        QueryApi queryApi = influxDBClient.getQueryApi();
        String query = String.format(
                "from(bucket: \"%s\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"users\") |> last() |> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")",
                bucket);
        return queryApi.query(query, User.class);
    }

    public void deleteById(String id) {
        // InfluxDB doesn't support direct deletes by tag easily;
        // overwrite with a tombstone marker by saving with version = -1
        findById(id).ifPresent(user -> {
            user.setVersion(-1);
            save(user);
        });
    }
}
