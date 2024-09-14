package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "DANHGIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @NotNull(message = "Sao không được để trống")
    @Min(value = 1, message = "Sao phải là số dương từ 1 đến 5")
    @Max(value = 5, message = "Sao phải là số dương từ 1 đến 5")
    @Column(name = "SAO")
    private int sao;

    @Size(max = 255, message = "Nhận xét không được vượt quá 255 ký tự")
    @Column(name = "NHANXET")
    private String nhanXet;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYDANHGIA")
    private LocalDateTime ngayDanhGia;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private ChiTietSanPham chiTietSanPham;
}
