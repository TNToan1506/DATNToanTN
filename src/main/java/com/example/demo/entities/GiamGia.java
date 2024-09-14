package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "GIAMGIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(max = 255, message = "Mã giảm giá không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Mã giảm giá chỉ được chứa chữ cái và số!")
    @Column(name = "ma")
    private String ma;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    @Column(name = "ten")
    private String ten;

    @PastOrPresent(message = "Ngày tạo phải là ngày hiện tại hoặc trong quá khứ")
    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc tương lai")
    @Column(name = "ngayBatDau")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    @Column(name = "ngayKetThuc")
    private LocalDateTime ngayKetThuc;

    @NotBlank(message = "Giá giảm không được để trống")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Giá giảm phải là số dương, có thể có tối đa hai chữ số thập phân")
    @Column(name = "giaGiam")
    private String giaGiam;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @Column(name = "trangThai")
    private Integer trangThai;

    // Phương thức kiểm tra logic ngày bắt đầu và kết thúc
    @AssertTrue(message = "Ngày bắt đầu phải trước ngày kết thúc")
    public boolean isNgayBatDauBeforeNgayKetThuc() {
        if (ngayBatDau != null && ngayKetThuc != null) {
            return ngayBatDau.isBefore(ngayKetThuc);
        }
        return true;  // Không áp dụng nếu không có đủ dữ liệu
    }
}
