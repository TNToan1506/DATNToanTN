package com.example.demo.controller;

import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.entities.DanhGia;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import com.example.demo.repositories.DanhGiaRepository;
import com.example.demo.request.DanhGiaRequest;
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

@RestController
@RequestMapping("danh-gia")
public class DanhGiaController {
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private DanhGiaRepository danhGiaRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayDanhGia");
        List<DanhGia> danhGiaList = danhGiaRepository.findAll(sort);
        return ResponseEntity.ok(danhGiaList.stream().map(DanhGia::toRespone));
    }

    @GetMapping("/detail")
    public ResponseEntity<?>detail(@RequestParam(name = "id")String id){
        if (danhGiaRepository.getById(id)==null){
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: "+id);
        }
        return ResponseEntity.ok(danhGiaRepository.getById(id));
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayDanhGia"));
        return ResponseEntity.ok(danhGiaRepository.findAll(pageRequest).stream().map(DanhGia::toRespone));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhGiaRequest danhGiaRequest) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(danhGiaRequest.getIdCTSP()).orElse(null);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + danhGiaRequest.getIdCTSP());
        }

        DanhGia danhGia = new DanhGia();
        BeanUtils.copyProperties(danhGiaRequest, danhGia);
        danhGia.setChiTietSanPham(chiTietSanPham);
        danhGia.setNgayDanhGia(LocalDateTime.now());
        danhGia.setNgaySua(null);
        danhGiaRepository.save(danhGia);
        return ResponseEntity.ok("Add done!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhGiaRequest danhGiaRequest, @RequestParam(name = "id") String id) {
        DanhGia existingDanhGia = danhGiaRepository.findById(id).orElse(null);
        if (existingDanhGia == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(danhGiaRequest.getIdCTSP()).orElse(null);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + danhGiaRequest.getIdCTSP());
        }
        BeanUtils.copyProperties(danhGiaRequest, existingDanhGia, "id", "ngayDanhGia");
        existingDanhGia.setChiTietSanPham(chiTietSanPham);
        existingDanhGia.setNgaySua(LocalDateTime.now());
        danhGiaRepository.save(existingDanhGia);
        return ResponseEntity.ok("Update done!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
        if (danhGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá có id: " + id);
        }
        danhGiaRepository.deleteById(id);
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
