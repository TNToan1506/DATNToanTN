package com.example.demo.controller;

import com.example.demo.entities.DanhGia;
import com.example.demo.repositories.DanhGiaRepository;
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
@RequestMapping("danh-gia")
public class DanhGiaController {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(danhGiaRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhGia danhGia) {
        danhGia.setNgayDanhGia(LocalDateTime.now());
        danhGiaRepository.save(danhGia);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhGia danhGia, @RequestParam(name = "id") Integer id) {
        if (danhGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }
        danhGia.setNgayDanhGia(danhGiaRepository.getReferenceById(id).getNgayDanhGia());
        danhGia.setNgaySua(LocalDateTime.now());
        DanhGia danhGiaUpdate = danhGiaRepository.getReferenceById(id);
        BeanUtils.copyProperties(danhGia, danhGiaUpdate, "id");
        danhGiaRepository.save(danhGiaUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (danhGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }
        danhGiaRepository.deleteById(id);
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
