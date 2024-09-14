package com.example.demo.repositories;

import com.example.demo.entities.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham,Integer> {
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.ma=:ma")
    LoaiSanPham getLSPByMa(@Param("ma")String ma);
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.id<>:id and  loaiSP.ma=:ma ")
    LoaiSanPham getLSPByMaAndId(@Param("ma")String ma,@Param("id")Integer id);
}
