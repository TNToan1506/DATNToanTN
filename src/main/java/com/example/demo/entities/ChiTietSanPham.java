package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CHITIETSANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Mã không được để trống")
    @Size(max = 255, message = "Mã không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã chỉ được chứa chữ cái và số!")
    @Column(name = "ma")
    private String ma;

    @NotBlank(message = "Giá không được để trống")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Giá phải là số dương, có thể có tối đa hai chữ số thập phân")
    @Column(name = "gia")
    private String gia;

    @NotBlank(message = "Số ngày sử dụng không được để trống")
    @Size(max = 255, message = "Số ngày sử dụng không được vượt quá 255 ký tự")
    @Column(name = "soNgaySuDung")
    private String soNgaySuDung;

    @NotBlank(message = "Thành phần không được để trống")
    @Size(max = 255, message = "Thành phần không được vượt quá 255 ký tự")
    @Column(name = "thanhPhan")
    private String thanhPhan;

    @NotBlank(message = "Công dụng không được để trống")
    @Size(max = 255, message = "Công dụng không được vượt quá 255 ký tự")
    @Column(name = "congDung")
    private String congDung;

    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    @Size(max = 255, message = "Hướng dẫn sử dụng không được vượt quá 255 ký tự")
    @Column(name = "HDSD")
    private String HDSD;

    @Min(value = 0, message = "Tuổi tối thiểu không hợp lệ")
    @Column(name = "tuoiMin")
    private int tuoiMin;

    @Min(value = 0, message = "Tuổi tối đa không hợp lệ")
    @Column(name = "tuoiMax")
    private int tuoiMax;

    @NotNull(message = "Hạn sử dụng không được để trống")
    @FutureOrPresent(message = "Hạn sử dụng phải là ngày hiện tại hoặc trong tương lai")
    @Column(name = "HSD")
    private LocalDate HSD;

    @NotNull(message = "Ngày nhập không được để trống")
    @PastOrPresent(message = "Ngày nhập phải là ngày hiện tại hoặc trong quá khứ")
    @Column(name = "ngayNhap")
    private LocalDateTime ngayNhap;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Column(name = "soLuong")
    private int soLuong;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
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

    // Phương thức kiểm tra logic tuổi
    @AssertTrue(message = "Tuổi tối đa phải lớn hơn hoặc bằng tuổi tối thiểu")
    public boolean isTuoiMaxValid() {
        return tuoiMax >= tuoiMin;
    }
}
