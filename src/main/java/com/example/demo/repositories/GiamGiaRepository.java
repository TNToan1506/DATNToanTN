package com.example.demo.repositories;

import com.example.demo.entities.GiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GiamGiaRepository extends JpaRepository<GiamGia,String> {
    @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma")
    GiamGia getByMa(@Param("ma")String ma);
    @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma and gg.id<>:id")
    GiamGia getByMaAndId(@Param("ma")String ma,@Param("id")String id);
}
