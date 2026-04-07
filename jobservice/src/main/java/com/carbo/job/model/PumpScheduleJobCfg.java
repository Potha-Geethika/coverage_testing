package com.carbo.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotEmpty;
import java.util.Date;

@Document(collection = "pump-schedule-job-cfgs")
public class PumpScheduleJobCfg {
    @Id
    private String id;

    @Field("jobId")
    @Indexed(unique = true)
    @NotEmpty(message = "job id can not be empty")
    private String jobId;

    @Field("casingCap")
    private String casingCap;

    @Field("overflush")
    private String overflush;

    @Field("casingOd")
    private String casingOd;

    @Field("weight")
    private String weight;

    @Field("customId")
    private String customId;

    @Field("grade")
    private String grade;

    @Field("completionType")
    private String completionType;

    @Field("maxPressure")
    private String maxPressure;

    @Field("maxRate")
    private String maxRate;

    @Field("created")
    private Long created = new Date().getTime();

    @Field("modified")
    private Long modified = new Date().getTime();

    @Field("organizationId")
    private String organizationId;

    @Field("ts")
    private Long ts;

    @Field("lastModifiedBy")
    private String lastModifiedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public void updateModified() {
        this.modified = new Date().getTime();
    }

    public Long getCreated() {
        return created;
    }

    public String getCasingCap() {
        return casingCap;
    }

    public void setCasingCap(String casingCap) {
        this.casingCap = casingCap;
    }

    public String getOverflush() {
        return overflush;
    }

    public void setOverflush(String overflush) {
        this.overflush = overflush;
    }

	public String getCasingOd() {
		return casingOd;
	}

	public void setCasingOd(String casingOd) {
		this.casingOd = casingOd;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCompletionType() {
		return completionType;
	}

	public void setCompletionType(String completionType) {
		this.completionType = completionType;
	}

	public String getMaxPressure() {
		return maxPressure;
	}

	public void setMaxPressure(String maxPressure) {
		this.maxPressure = maxPressure;
	}

	public String getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(String maxRate) {
		this.maxRate = maxRate;
	}
    
}
