package org.sample.sampleenterpriseproj1.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Measurement(name = "users")
public class User {

    @Column(tag = true)
    private String id;

    @NotBlank
    @Column
    private String name;

    @NotBlank
    @Email
    @Column(tag = true)
    private String email;

    @NotBlank
    @Column
    private String password;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(tag = true)
    private String role;

    @Column(timestamp = true)
    private Instant time;

    @Column
    private String createdAt;

    @Column
    private String updatedAt;

    // Dummy field required by InfluxDB (at least one non-tag, non-timestamp field)
    @Column
    private long version = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
