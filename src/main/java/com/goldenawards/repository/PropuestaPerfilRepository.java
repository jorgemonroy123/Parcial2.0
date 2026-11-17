package com.goldenawards.repository;

import com.goldenawards.model.PropuestaPerfil;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropuestaPerfilRepository extends MongoRepository<PropuestaPerfil, String> {
    List<PropuestaPerfil> findByEstado(String estado);
    List<PropuestaPerfil> findByRepresentanteId(String representanteId);
    boolean existsByJugadorIdAndEstado(String jugadorId, String estado);
}
