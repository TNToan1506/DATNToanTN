package com.example.demo.controller;

import com.example.demo.entities.GiamGia;
import com.example.demo.repositories.GiamGiaRepository;
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
@RequestMapping("giam-gia")
public class GiamGiaController {

    @Autowired
    GiamGiaRepository giamGiaRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(giamGiaRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody GiamGia giamGia) {
        giamGia.setMa(giamGia.getMa().trim());
        giamGia.setNgayTao(LocalDateTime.now());
        if (giamGiaRepository.getByMa(giamGia.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã giảm giá không được trùng!");
        }
        giamGiaRepository.save(giamGia);
        return ResponseEntity.ok("Add done!");
    }

    // Cập nhật giảm giá
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody GiamGia giamGia, @RequestParam(name = "id") Integer id) {
        if (giamGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
        }
        giamGia.setNgayTao(giamGiaRepository.getReferenceById(id).getNgayTao());
        if (giamGiaRepository.getByMaAndId(giamGia.getMa(), id) != null) {
            return ResponseEntity.badRequest().body("Mã giảm giá không được trùng!");
        }
        GiamGia giamGiaUpdate = giamGiaRepository.getReferenceById(id);
        BeanUtils.copyProperties(giamGia, giamGiaUpdate, "id");
        giamGiaRepository.save(giamGiaUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (giamGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
        }
        giamGiaRepository.deleteById(id);
        return ResponseEntity.ok("Delete done!");
    }

    // Xử lý ngoại lệ validate lỗi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
