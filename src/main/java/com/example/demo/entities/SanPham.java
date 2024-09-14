    package com.example.demo.entities;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "SANPHAM")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SanPham {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Integer id;

        @NotBlank(message = "Mã sản phẩm không được để trống")
        @Size(max = 255, message = "Mã sản phẩm không được vượt quá 255 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã sản phẩm chỉ được chứa chữ cái và số!")
        @Column(name = "maSP")
        private String maSP;

        @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9À-ỹà-ỹ\\s]+$", message = "Tên sản phẩm chỉ được chứa chữ cái, số và khoảng trắng!")
        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Column(name = "tenSP")
        private String tenSP;

        @Column(name = "ngayTao")
        private LocalDateTime ngayTao;

        @Column(name = "ngaySua")
        private LocalDateTime ngaySua;

        @NotNull(message = "Trạng thái không được để trống")
        @Min(value = 0, message = "Trạng thái không hợp lệ")
        @Max(value = 4, message = "Trạng thái không hợp lệ")
        @Column(name = "trangThai")
        private Integer trangThai;

        @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
        @Column(name = "moTa")
        private String moTa;

        @NotNull(message = "Danh mục không được để trống")
        @ManyToOne
        @JoinColumn(name = "idLoaiSP")
        private LoaiSanPham loaiSanPham;

        @NotNull(message = "Thương hiệu không được để trống")
        @ManyToOne
        @JoinColumn(name = "idThuongHieu")
        private ThuongHieu thuongHieu;
    }
