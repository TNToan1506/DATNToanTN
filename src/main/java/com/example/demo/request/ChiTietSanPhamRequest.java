package com.example.demo.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChiTietSanPhamRequest {

    @Size(max = 10, message = "Mã không được vượt quá 10 ký tự")
//    @Pattern(regexp = "^CTSP[A-Z0-9]{6}$", message = "Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!")
    private String ma;

    @NotBlank(message = "Giá không được để trống")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Giá phải là số dương, có thể có tối đa hai chữ số thập phân")
    private String gia;

    @NotBlank(message = "Số ngày sử dụng không được để trống")
    @Size(max = 255, message = "Số ngày sử dụng không được vượt quá 255 ký tự")
    private String soNgaySuDung;

    @NotBlank(message = "Thành phần không được để trống")
    @Size(max = 255, message = "Thành phần không được vượt quá 255 ký tự")
    private String thanhPhan;

    @NotBlank(message = "Công dụng không được để trống")
    @Size(max = 255, message = "Công dụng không được vượt quá 255 ký tự")
    private String congDung;

    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    @Size(max = 255, message = "Hướng dẫn sử dụng không được vượt quá 255 ký tự")
    private String HDSD;

    @Min(value = 0, message = "Tuổi tối thiểu không hợp lệ")
    @Max(value = 200, message = "Tuổi tối thiểu không hợp lệ")
    private int tuoiMin;

    @Min(value = 0, message = "Tuổi tối đa không hợp lệ")
    @Max(value = 200, message = "Tuổi tối đa không hợp lệ")
    private int tuoiMax;

    @NotNull(message = "Hạn sử dụng không được để trống")
    @FutureOrPresent(message = "Hạn sử dụng phải là ngày hiện tại hoặc trong tương lai")
    private LocalDate HSD;

    @NotNull(message = "Ngày nhập không được để trống")
    @PastOrPresent(message = "Ngày nhập phải là ngày hiện tại hoặc trong quá khứ")
    private LocalDateTime ngayNhap;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int soLuong;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private int trangThai;

    @NotNull(message = "Vui lòng nhập vào id sản phẩm")
    private String idSP;

    private String idGiamGia;
    @AssertTrue(message = "Tuổi tối đa phải lớn hơn hoặc bằng tuổi tối thiểu")
    public boolean isTuoiMaxValid() {
        return tuoiMax >= tuoiMin;
    }
}
