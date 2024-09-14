package com.example.demo.controller;

import com.example.demo.entities.SanPham;
import com.example.demo.repositories.SanPhamRepository;
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
@RequestMapping("san-pham")
public class SanPhamController {

    @Autowired
    SanPhamRepository sanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sanPhamRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody SanPham sanPham) {
        sanPham.setMaSP(sanPham.getMaSP().trim());
        sanPham.setNgayTao(LocalDateTime.now());
        if (sanPhamRepository.getByMa(sanPham.getMaSP()) != null) {
            return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
        }
        sanPhamRepository.save(sanPham);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody SanPham sanPham, @RequestParam(name = "id") Integer id) {
        if (sanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        sanPham.setNgayTao(sanPhamRepository.getReferenceById(id).getNgayTao());
        sanPham.setNgaySua(LocalDateTime.now());
        if (sanPhamRepository.getByMaAndId(sanPham.getMaSP(), id) != null) {
            return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
        }
        SanPham sanPhamUpdate = sanPhamRepository.getReferenceById(id);
        BeanUtils.copyProperties(sanPham, sanPhamUpdate, "id");
        sanPhamRepository.save(sanPhamUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (sanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        sanPhamRepository.deleteById(id);
        return ResponseEntity.ok("Delete done!");
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
