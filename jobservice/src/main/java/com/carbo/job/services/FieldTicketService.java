package com.carbo.job.services;

import com.carbo.job.model.FieldTicket;
import com.carbo.job.repository.FieldTicketMongoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FieldTicketService {
    private final FieldTicketMongoDbRepository fieldTicketRepository;

    @Autowired
    public FieldTicketService(FieldTicketMongoDbRepository fieldTicketRepository) {
        this.fieldTicketRepository = fieldTicketRepository;
    }

    public List<FieldTicket> getAll() {
        return fieldTicketRepository.findAll();
    }

    public List<FieldTicket> getByOrganizationId(String organizationId) {
        return fieldTicketRepository.findByOrganizationId(organizationId);
    }

    public Optional<FieldTicket> getFieldTicket(String fieldTicketId) {
        return fieldTicketRepository.findById(fieldTicketId);
    }

    public List<FieldTicket> findByOrganizationIdAndJobId(String organizationId, String jobId) {
        return fieldTicketRepository.findByOrganizationIdAndJobId(organizationId, jobId);
    }

    public List<FieldTicket> findByOrganizationIdAndWellAndStage(String organizationId, String well, String name) {
        return fieldTicketRepository.findByOrganizationIdAndWellAndName(organizationId, well, name);
    }

    public Optional<FieldTicket> findByJobIdAndWellAndStage(String jobId, String well, String name) {
        List<FieldTicket> fieldTickets = fieldTicketRepository.findByJobIdAndWellAndName(jobId, well, name);
        if (fieldTickets.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(fieldTickets.get(0));
        }
    }

    public FieldTicket saveFieldTicket(FieldTicket fieldTicket) {
        return fieldTicketRepository.save(fieldTicket);
    }

    public void updateFieldTicket(FieldTicket fieldTicket) {
        fieldTicketRepository.save(fieldTicket);
    }

    public void deleteFieldTicket(String fieldTicketId) {
        fieldTicketRepository.deleteById(fieldTicketId);
    }
}
