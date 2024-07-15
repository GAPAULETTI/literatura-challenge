package com.challenge.literatura.repository;

import com.challenge.literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    Autor findByNombre(String nombre);
    @Query("select a from Autor a where a.nacimiento <=:anio and a.fallecimiento >=:anio")
    List<Autor> buscarAutoresVivosPorAnio(int anio);
}
