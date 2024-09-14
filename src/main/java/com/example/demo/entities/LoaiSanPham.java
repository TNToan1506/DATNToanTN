package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOAISP")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(max = 255, message = "Mã sản phẩm không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã sản phẩm chỉ được chứa chữ cái và số!")
    @Column(name = "MA")
    private String ma;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]+$", message = "Tên sản phẩm chỉ được chứa chữ cái!")
    @Column(name = "TEN")
    private String ten;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "TRANGTHAI")
    private Integer trangThai;
    @ManyToOne
    @JoinColumn(name = "IDDANHMUC")
    private DanhMuc danhMuc;

}
