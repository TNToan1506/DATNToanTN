package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANHMUC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Bỏ qua proxy của Hibernate

public class DanhMuc {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(max = 255, message = "Mã danh mục không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã danh mục chỉ được chứa chữ cái và số!")
    @Column(name = "MA")
    private String ma;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]+$", message = "Tên danh mục chỉ được chứa chữ cái!")
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
}
