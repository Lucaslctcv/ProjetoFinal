package com.example.demo.Repository;

import com.example.demo.Models.Convidado;
import com.example.demo.Models.Evento;
import org.springframework.data.repository.CrudRepository;

public interface ConvidadoRepository  extends CrudRepository<Convidado, String> {
    Iterable<Convidado> findByEvento(Evento evento);
    Convidado findByRg(String rg);
}

