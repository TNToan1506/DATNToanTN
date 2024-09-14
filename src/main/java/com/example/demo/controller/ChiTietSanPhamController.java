package com.example.demo.controller;

import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("chi-tiet-san-pham")
public class ChiTietSanPhamController {

    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(chiTietSanPhamRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ChiTietSanPham chiTietSanPham) {
        chiTietSanPham.setMa(chiTietSanPham.getMa().trim());
        chiTietSanPham.setNgayTao(LocalDateTime.now());
        chiTietSanPham.setNgaySua(null);

        if (chiTietSanPhamRepository.getByMa(chiTietSanPham.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
        }
        chiTietSanPhamRepository.save(chiTietSanPham);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ChiTietSanPham chiTietSanPham, @RequestParam(name = "id") Integer id) {
        if (chiTietSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }
        chiTietSanPham.setNgayTao(chiTietSanPhamRepository.getReferenceById(id).getNgayTao());
        chiTietSanPham.setNgaySua(LocalDateTime.now());

        if (chiTietSanPhamRepository.getByMaAndId(chiTietSanPham.getMa(), id) != null) {
            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
        }
        ChiTietSanPham chiTietSanPhamUpdate = chiTietSanPhamRepository.getReferenceById(id);
        BeanUtils.copyProperties(chiTietSanPham, chiTietSanPhamUpdate, "id");
        chiTietSanPhamRepository.save(chiTietSanPhamUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
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
