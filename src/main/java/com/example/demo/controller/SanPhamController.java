package com.example.demo.controller;

import com.example.demo.entities.LoaiSanPham;
import com.example.demo.entities.SanPham;
import com.example.demo.entities.ThuongHieu;
import com.example.demo.repositories.LoaiSanPhamRepository;
import com.example.demo.repositories.SanPhamRepository;
import com.example.demo.repositories.ThuongHieuRepository;
import com.example.demo.request.SanPhamRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("san-pham")
public class SanPhamController {

    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    LoaiSanPhamRepository loaiSanPhamRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<SanPham> sanPhamList = sanPhamRepository.findAll(sort);
        return  ResponseEntity.ok(sanPhamList.stream().map(SanPham::toResponse));
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?>phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page){
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(sanPhamRepository.findAll(pageRequest));
    }
    @GetMapping("/detail")
    public ResponseEntity<?>detail(@RequestParam(name = "id")String id){
        Optional<SanPham> existingSanPham = sanPhamRepository.findById(id);
        if (existingSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        return ResponseEntity.ok(sanPhamRepository.findById(id).stream().map(SanPham::toResponse));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody SanPhamRequest sanPhamRequest) {
        sanPhamRequest.setMaSP(sanPhamRequest.getMaSP().trim());
        if (sanPhamRepository.getByMa(sanPhamRequest.getMaSP()) != null) {
            return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
        }
        Optional<LoaiSanPham> loaiSanPham = loaiSanPhamRepository.findById(sanPhamRequest.getIdSanPham());
        if (loaiSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + sanPhamRequest.getIdSanPham());
        }
        Optional<ThuongHieu> thuongHieu = thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu());
        if (thuongHieu.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu());
        }

        SanPham sanPham = new SanPham();
        BeanUtils.copyProperties(sanPhamRequest, sanPham);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(null);
        sanPham.setLoaiSanPham(loaiSanPham.get());
        sanPham.setThuongHieu(thuongHieu.get());

        sanPhamRepository.save(sanPham);
        return ResponseEntity.ok("Thêm sản phẩm thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody SanPhamRequest sanPhamRequest, @RequestParam(name = "id") String id) {
        Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id);
        if (optionalSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }

        if (sanPhamRepository.getByMaAndId(sanPhamRequest.getMaSP(), id) != null) {
            return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
        }

        Optional<LoaiSanPham> loaiSanPham = loaiSanPhamRepository.findById(sanPhamRequest.getIdSanPham());
        if (loaiSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + sanPhamRequest.getIdSanPham());
        }

        Optional<ThuongHieu> thuongHieu = thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu());
        if (thuongHieu.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu());
        }

        SanPham existingSanPham = optionalSanPham.get();
        BeanUtils.copyProperties(sanPhamRequest, existingSanPham, "id", "ngayTao");
        existingSanPham.setNgaySua(LocalDateTime.now());
        existingSanPham.setLoaiSanPham(loaiSanPham.get());
        existingSanPham.setThuongHieu(thuongHieu.get());

        sanPhamRepository.save(existingSanPham);
        return ResponseEntity.ok("Cập nhật sản phẩm thành công!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
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
