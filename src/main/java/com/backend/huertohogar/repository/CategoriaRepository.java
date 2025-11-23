package com.backend.huertohogar.repository;

import com.backend.huertohogar.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    java.util.Optional<Categoria> findByNombre(String nombre);
}
