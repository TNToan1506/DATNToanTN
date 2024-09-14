package com.example.demo.repositories;

import com.example.demo.entities.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham,Integer> {
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma")
    ChiTietSanPham getByMa(@Param("ma")String ma);
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma AND ctsp.id<>:id")
    ChiTietSanPham getByMaAndId(@Param("ma")String ma,@Param("id")Integer id);
}
