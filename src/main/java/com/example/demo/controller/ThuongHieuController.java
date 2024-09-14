package com.example.demo.controller;

import com.example.demo.entities.ThuongHieu;
import com.example.demo.repositories.ThuongHieuRepository;
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
@RequestMapping("thuong-hieu")
public class ThuongHieuController {
    @Autowired
    ThuongHieuRepository thuongHieuRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(thuongHieuRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ThuongHieu thuongHieu) {
        thuongHieu.setMa(thuongHieu.getMa().trim());
        thuongHieu.setNgayTao(LocalDateTime.now());
        if (thuongHieuRepository.getByMa(thuongHieu.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã không được trùng!");
        }
        thuongHieuRepository.save(thuongHieu);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ThuongHieu thuongHieu, @RequestParam(name = "id") Integer id) {
        if (thuongHieuRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        thuongHieu.setNgayTao(thuongHieuRepository.getReferenceById(id).getNgayTao());
        thuongHieu.setNgaySua(LocalDateTime.now());
        if (thuongHieuRepository.getByIdAndMa(id,thuongHieu.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã không được trùng!");
        }
        ThuongHieu thuongHieuUpdate = thuongHieuRepository.getReferenceById(id);
        BeanUtils.copyProperties(thuongHieu, thuongHieuUpdate, "id");
        thuongHieuRepository.save(thuongHieuUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (thuongHieuRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        thuongHieuRepository.deleteById(id);
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
