package com.example.demo.controller;

import com.example.demo.entities.LoaiSanPham;
import com.example.demo.repositories.LoaiSanPhamRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("loai-sp")
public class LoaiSanPhamController {
    @Autowired
    LoaiSanPhamRepository loaiSanPhamRepository;
    @GetMapping()
    public ResponseEntity<?>getALl(){
        return  ResponseEntity.ok(loaiSanPhamRepository.findAll());
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody LoaiSanPham loaiSanPham) {
        loaiSanPham.setMa(loaiSanPham.getMa().trim());
        loaiSanPham.setNgayTao(LocalDateTime.now());
        if (loaiSanPhamRepository.getLSPByMa(loaiSanPham.getMa().trim())!=null){
            return ResponseEntity.badRequest().body("Mã không được trùng!");
        }
        loaiSanPhamRepository.save(loaiSanPham);

        return ResponseEntity.ok("Add done!");
    }
    @PutMapping("/update")
    public ResponseEntity<?>update(@Valid @RequestBody LoaiSanPham loaiSanPham,@RequestParam(name = "id")Integer id){
        if (loaiSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + id);
        }
        loaiSanPham.setNgayTao(loaiSanPhamRepository.getReferenceById(id).getNgayTao());
        loaiSanPham.setNgaySua(LocalDateTime.now());
        if (loaiSanPhamRepository.getLSPByMaAndId(loaiSanPham.getMa(),id)!=null){
            return ResponseEntity.badRequest().body("Mã không được trùng!");
        }
        LoaiSanPham loaiSanPhamUpdate = loaiSanPhamRepository.getReferenceById(id);
        BeanUtils.copyProperties(loaiSanPham, loaiSanPhamUpdate,"id");
        loaiSanPhamRepository.save(loaiSanPhamUpdate);
        return ResponseEntity.ok("Update done!");
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?>deleta(@RequestParam(name = "id")Integer id){
        if (loaiSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + id);
        }
        loaiSanPhamRepository.deleteById(id);
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
