package com.example.demo.controller;

import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.entities.GiamGia;
import com.example.demo.entities.SanPham;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import com.example.demo.repositories.GiamGiaRepository;
import com.example.demo.repositories.SanPhamRepository;
import com.example.demo.request.ChiTietSanPhamRequest;
import com.example.demo.respone.ChiTietSanPhamResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chi-tiet-san-pham")
public class ChiTietSanPhamController {
    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    GiamGiaRepository giamGiaRepository;
    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll(Sort.by(Sort.Order.desc("ngayTao")));
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhams.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/page")
    public ResponseEntity<?> page(@RequestParam(name = "page",defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("ngayTao")));
        Page<ChiTietSanPham> chiTietSanPhamPage = chiTietSanPhamRepository.findAll(pageRequest);
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhamPage.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam(name = "id")String id) {
        if (chiTietSanPhamRepository.getById(id)==null){
            return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: "+id);
        }
        return ResponseEntity.ok(chiTietSanPhamRepository.findById(id)
                .stream().map(ChiTietSanPham::toChiTietSanPhamResponse));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest) {
        chiTietSanPhamRequest.setMa(chiTietSanPhamRequest.getMa().trim());
        chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());
        chiTietSanPhamRequest.setThanhPhan(chiTietSanPhamRequest.getThanhPhan().trim());
        chiTietSanPhamRequest.setCongDung(chiTietSanPhamRequest.getCongDung().trim());

        // Kiểm tra mã và tạo mã mới nếu mã không được cung cấp
        if (chiTietSanPhamRequest.getMa() == null || chiTietSanPhamRequest.getMa().isEmpty()) {
            String generatedMa;
            do {
                String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
                generatedMa = "CTSP" + randomString;
            } while (chiTietSanPhamRepository.getByMa(generatedMa) != null);
            chiTietSanPhamRequest.setMa(generatedMa);
        } else if (!Pattern.matches("^CTSP[A-Z0-9]{6}$", chiTietSanPhamRequest.getMa().trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
        } else if (chiTietSanPhamRepository.getByMa(chiTietSanPhamRequest.getMa().trim()) != null) {
            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
        }

        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.trungCTSP(
                chiTietSanPhamRequest.getIdSP(),
                chiTietSanPhamRequest.getSoNgaySuDung(),
                chiTietSanPhamRequest.getThanhPhan(),
                chiTietSanPhamRequest.getTuoiMin(),
                chiTietSanPhamRequest.getTuoiMax(),
                chiTietSanPhamRequest.getCongDung());

        if (existingChiTietSanPham != null) {
            existingChiTietSanPham.setSoLuong(existingChiTietSanPham.getSoLuong() + chiTietSanPhamRequest.getSoLuong());
            chiTietSanPhamRepository.save(existingChiTietSanPham);
            return ResponseEntity.ok("Sản phẩm đã tồn tại, số lượng đã được cập nhật!");
        }

        ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
        BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);
        chiTietSanPham.setNgayTao(LocalDateTime.now());
        chiTietSanPham.setNgaySua(null);

        if (chiTietSanPhamRequest.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(chiTietSanPhamRequest.getIdSP()).orElse(null);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với id: " + chiTietSanPhamRequest.getIdSP());
            }
            chiTietSanPham.setSanPham(sanPham);
        }

        if (chiTietSanPhamRequest.getIdGiamGia() != null) {
            GiamGia giamGia = giamGiaRepository.findById(chiTietSanPhamRequest.getIdGiamGia()).orElse(null);
            if (giamGia == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy giảm giá với id: " + chiTietSanPhamRequest.getIdGiamGia());
            }
            chiTietSanPham.setGiamGia(giamGia);
        }

        chiTietSanPhamRepository.save(chiTietSanPham);
        return ResponseEntity.ok("Add successful!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest,
                                    @RequestParam(name = "id") String id) {
        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.findById(id).orElse(null);
        if (existingChiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }

        chiTietSanPhamRequest.setMa(chiTietSanPhamRequest.getMa().trim());
        chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());
        chiTietSanPhamRequest.setThanhPhan(chiTietSanPhamRequest.getThanhPhan().trim());
        chiTietSanPhamRequest.setCongDung(chiTietSanPhamRequest.getCongDung().trim());

        ChiTietSanPham trungCTSP = chiTietSanPhamRepository.trungCTSP(
                chiTietSanPhamRequest.getIdSP(),
                chiTietSanPhamRequest.getSoNgaySuDung(),
                chiTietSanPhamRequest.getThanhPhan(),
                chiTietSanPhamRequest.getTuoiMin(),
                chiTietSanPhamRequest.getTuoiMax(),
                chiTietSanPhamRequest.getCongDung());

        if (trungCTSP != null && !trungCTSP.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm đã tồn tại với các thuộc tính tương ứng!");
        }

        if (chiTietSanPhamRequest.getMa() != null && !Pattern.matches("^CTSP[A-Z0-9]{6}$", chiTietSanPhamRequest.getMa().trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
        }

        if (chiTietSanPhamRepository.getByMaAndId(chiTietSanPhamRequest.getMa(), id) != null) {
            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
        }

        if (chiTietSanPhamRequest.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(chiTietSanPhamRequest.getIdSP()).orElse(null);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với id: " + chiTietSanPhamRequest.getIdSP());
            }
            existingChiTietSanPham.setSanPham(sanPham);
        }

        if (chiTietSanPhamRequest.getIdGiamGia() != null) {
            GiamGia giamGia = giamGiaRepository.findById(chiTietSanPhamRequest.getIdGiamGia()).orElse(null);
            if (giamGia == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy giảm giá với id: " + chiTietSanPhamRequest.getIdGiamGia());
            }
            existingChiTietSanPham.setGiamGia(giamGia);
        }

        BeanUtils.copyProperties(chiTietSanPhamRequest, existingChiTietSanPham, "id", "ngayTao");
        existingChiTietSanPham.setNgaySua(LocalDateTime.now());

        chiTietSanPhamRepository.save(existingChiTietSanPham);
        return ResponseEntity.ok("Update successful!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
        if (chiTietSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }
        chiTietSanPhamRepository.deleteById(id);
        return ResponseEntity.ok("Delete done!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
