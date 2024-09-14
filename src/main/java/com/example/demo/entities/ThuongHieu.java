package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "THUONGHIEU")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Mã thương hiệu không được để trống")
    @Size(max = 255, message = "Mã thương hiệu không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-z A-Z 0-9]+$", message = "Mã thương hiệu chỉ được chứa chữ cái và số!")
    @Column(name = "ma")
    private String ma;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-z A-Z À-ỹ à-ỹ\\s]+$", message = "Tên thương hiệu chỉ được chứa chữ cái!")
    @Column(name = "ten")
    private String ten;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "trangThai")
    private Integer trangThai;

    @NotBlank(message = "Xuất xứ không được để trống")
    @Size(max = 255, message = "Xuất xứ không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-z A-Z À-ỹ à-ỹ\\s]+$", message = "Xuất xứ chỉ được chứa chữ cái!")
    @Column(name = "xuatXu")
    private String xuatXu;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")

    @Column(name = "moTa")
    private String moTa;
}