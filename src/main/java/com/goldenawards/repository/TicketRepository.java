package com.goldenawards.repository;

import com.goldenawards.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByCreadoPorId(String usuarioId);
    List<Ticket> findByEstado(String estado);
}
