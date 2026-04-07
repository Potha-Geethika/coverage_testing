package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "migration-status-entries")
@CompoundIndex(def = "{'jobId': 1, 'migrationType': 1}", name = "job_id_migration_type_index", unique = true)
public class MigrationStatusEntry {
    @Id
    private String id;

    @Field("jobId")
    private String jobId;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified  = new Date().getTime();

    @Field("migrationType")
    private MigrationType migrationType;

    @Field("emailMigrated")
    private Map<EmailType, Boolean> emailMigrated = new HashMap<>();

    @Field("migrated")
    private Boolean migrated;

    public MigrationStatusEntry(String jobId) {
        this.jobId = jobId;
        this.migrationType = MigrationType.PROPPANT_DELIVERY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public void setMigrated() {
        this.migrated = true;
    }

    public boolean isMigrated() {
        return this.migrated;
    }

    public boolean isMigrated(EmailType emailType) {
        if (emailMigrated.containsKey(emailType)) {
            return emailMigrated.get(emailType);
        }
        else {
            return false;
        }
    }
}
