package com.example.demo.Repository;

import com.example.demo.Models.Evento;
import org.springframework.data.repository.CrudRepository;

public interface EventoRepository extends CrudRepository<Evento, String> {

    Evento findByCodigo(long codigo);


}
