package com.carbo.job.services;

import com.carbo.job.model.OpsActivityCode;
import com.carbo.job.repository.OpsActivityCodeMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
"This code is not in use and comments have been added by Jyotish Kumar, Walkingtree on 2022-11-28 as part of JobService Stabilization activity."

*/
@Service
public class OpsActivityCodeService {
    private final OpsActivityCodeMongoDbRepository opsActivityCodeRepository;

    @Autowired
    public OpsActivityCodeService(OpsActivityCodeMongoDbRepository opsActivityCodeRepository) {
        this.opsActivityCodeRepository = opsActivityCodeRepository;
    }

    public List<OpsActivityCode> getAll() {
        return opsActivityCodeRepository.findAll();
    }

    public List<OpsActivityCode> getByOrganizationId(String organizationId) {
        return opsActivityCodeRepository.findByOrganizationId(organizationId);
    }

    public Optional<OpsActivityCode> getOpsActivityCode(String opsActivityCodeId) {
        return opsActivityCodeRepository.findById(opsActivityCodeId);
    }

    public OpsActivityCode saveOpsActivityCode(OpsActivityCode opsActivityCode) {
        return opsActivityCodeRepository.save(opsActivityCode);
    }

    public void updateOpsActivityCode(OpsActivityCode opsActivityCode) {
        opsActivityCodeRepository.save(opsActivityCode);
    }

    public void deleteOpsActivityCode(String opsActivityCodeId) {
        opsActivityCodeRepository.deleteById(opsActivityCodeId);
    }
}
