package com.example.demo.entities;

import com.example.demo.respone.ChiTietSanPhamResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CHITIETSANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "ma")
    private String ma;

    @Column(name = "gia")
    private String gia;


    @Column(name = "soNgaySuDung")
    private String soNgaySuDung;


    @Column(name = "thanhPhan")
    private String thanhPhan;


    @Column(name = "congDung")
    private String congDung;


    @Column(name = "HDSD")
    private String HDSD;

    @Column(name = "tuoiMin")
    private int tuoiMin;

    @Column(name = "tuoiMax")
    private int tuoiMax;


    @Column(name = "HSD")
    private LocalDate HSD;


    @Column(name = "ngayNhap")
    private LocalDateTime ngayNhap;

    @Column(name = "soLuong")
    private int soLuong;


    @Column(name = "trangThai")
    private int trangThai;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "idSP")
    SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "idGiamGia")
    GiamGia giamGia;

    public ChiTietSanPhamResponse toChiTietSanPhamResponse() {
        return new ChiTietSanPhamResponse(
                id,
                ma,
                gia,
                soNgaySuDung,
                thanhPhan,
                congDung,
                HDSD,
                tuoiMin,
                tuoiMax,
                HSD,
                ngayNhap,
                soLuong,
                trangThai,
                ngayTao,
                ngaySua,
                sanPham != null ? sanPham.getMaSP() : null,
                giamGia != null ? giamGia.getMa() : null
        );
    }
}
