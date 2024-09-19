package com.example.demo.repositories;

import com.example.demo.entities.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham,String> {
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma")
    ChiTietSanPham getByMa(@Param("ma")String ma);
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.ma=:ma AND ctsp.id<>:id")
    ChiTietSanPham getByMaAndId(@Param("ma")String ma,@Param("id")String id);
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.sanPham.id = :idSP AND ctsp.soNgaySuDung = :soNgaySuDung AND " +
            "ctsp.thanhPhan = :thanhPhan " +
            "AND ctsp.congDung = :congDung " +
            "AND ctsp.tuoiMin = :tuoiMin " +
            "AND ctsp.tuoiMax = :tuoiMax")
    ChiTietSanPham trungCTSP(@Param("idSP") String idSP,
                             @Param("soNgaySuDung") String soNgaySuDung,
                             @Param("thanhPhan") String thanhPhan,
                             @Param("tuoiMin") int tuoiMin,
                             @Param("tuoiMax") int tuoiMax,
                             @Param("congDung") String congDung);

}
