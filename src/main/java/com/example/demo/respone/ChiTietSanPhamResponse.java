package com.example.demo.respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChiTietSanPhamResponse {
    private String id;
    private String ma;
    private String gia;
    private String soNgaySuDung;
    private String thanhPhan;
    private String congDung;
    private String HDSD;
    private int tuoiMin;
    private int tuoiMax;
    private LocalDate HSD;
    private LocalDateTime ngayNhap;
    private int soLuong;
    private int trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String maSP;
    private String maGiamGia;
}
