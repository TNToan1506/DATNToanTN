package com.example.demo.controller;

import com.example.demo.entities.DanhMuc;
import com.example.demo.repositories.DanhMucRepository;
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
@RequestMapping("danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(danhMucRepository.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhMuc danhMuc) {
        danhMuc.setMa(danhMuc.getMa().trim());
        danhMuc.setNgayTao(LocalDateTime.now());
        if (danhMucRepository.getByMa(danhMuc.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã danh mục không được trùng!");
        }
        danhMucRepository.save(danhMuc);
        return ResponseEntity.ok("Thêm danh mục thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhMuc danhMuc, @RequestParam(name = "id") Integer id) {
        if (danhMucRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        danhMuc.setNgayTao(danhMucRepository.getReferenceById(id).getNgayTao());
        danhMuc.setNgaySua(LocalDateTime.now());
        if (danhMucRepository.getByMaAndId(danhMuc.getMa(), id) != null) {
            return ResponseEntity.badRequest().body("Mã danh mục không được trùng!");
        }
        DanhMuc danhMucUpdate = danhMucRepository.getReferenceById(id);
        BeanUtils.copyProperties(danhMuc, danhMucUpdate, "id");
        danhMucRepository.save(danhMucUpdate);
        return ResponseEntity.ok("Cập nhật danh mục thành công!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") Integer id) {
        if (danhMucRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        danhMucRepository.deleteById(id);
        return ResponseEntity.ok("Xóa danh mục thành công!");
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
