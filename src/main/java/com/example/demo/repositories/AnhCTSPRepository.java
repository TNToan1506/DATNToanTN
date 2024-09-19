package com.example.demo.repositories;

import com.example.demo.entities.AnhCTSP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnhCTSPRepository extends JpaRepository<AnhCTSP,String> {

}
