package com.example.demo.controller;

import com.example.demo.entities.AnhSP;
import com.example.demo.repositories.AnhSPRepository;
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
@RequestMapping("anh-sp")
public class AnhSPController {

    @Autowired
    private AnhSPRepository anhSPRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(anhSPRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody AnhSP anhSP) {
        anhSP.setNgayTao(LocalDateTime.now());
        anhSP.setLink(anhSP.getLink().trim());


        anhSPRepository.save(anhSP);
        return ResponseEntity.ok("Thêm ảnh thành công!");
    }

    // PUT (Update) an existing AnhSP
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AnhSP anhSP, @RequestParam(name = "id") Integer id) {
        return anhSPRepository.findById(id).map(existingAnhSP -> {
            anhSP.setNgayTao(existingAnhSP.getNgayTao());
            anhSP.setNgaySua(LocalDateTime.now());

            BeanUtils.copyProperties(anhSP, existingAnhSP, "id");
            anhSPRepository.save(existingAnhSP);
            return ResponseEntity.ok("Cập nhật ảnh thành công!");
        }).orElseGet(() -> ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (anhSPRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
        }
        anhSPRepository.deleteById(id);
        return ResponseEntity.ok("Xóa ảnh thành công!");
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
