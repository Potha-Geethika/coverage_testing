package com.carbo.job.model.widget;

import com.carbo.job.model.FileDescription;
import org.apache.xmlbeans.impl.xb.ltgfmt.FileDesc;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "demo-file")
public class DemoFile {

    @Id
    private String id;

    @Field("organizationId")
    private String organizationId;

    @Field("fileDesc")
    private List<FileDescription> fileDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<FileDescription> getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(List<FileDescription> fileDesc) {
        this.fileDesc = fileDesc;
    }
}
