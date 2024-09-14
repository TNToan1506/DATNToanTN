package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ANHSP")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnhSP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @NotBlank(message = "Link ảnh không được để trống")
    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    @Column(name = "LINK")
    private String link;

    @NotBlank(message = "Tên ảnh không được để trống")
    @Size(max = 255, message = "Tên ảnh không được vượt quá 255 ký tự")
    @Column(name = "TEN")
    private String ten;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private ChiTietSanPham chiTietSanPham;
}
