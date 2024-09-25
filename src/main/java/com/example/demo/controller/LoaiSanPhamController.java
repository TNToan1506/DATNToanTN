package com.example.demo.controller;

import com.example.demo.entities.GiamGia;
import com.example.demo.entities.LoaiSanPham;
import com.example.demo.repositories.DanhMucRepository;
import com.example.demo.repositories.LoaiSanPhamRepository;
import com.example.demo.request.LoaiSanPhamRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("loai-sp")
public class LoaiSanPhamController {
    @Autowired
    LoaiSanPhamRepository loaiSanPhamRepository;
    @Autowired
    DanhMucRepository danhMucRepository;
    @GetMapping()
    public ResponseEntity<?>getALl(){
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<LoaiSanPham> loaiSanPhamList = loaiSanPhamRepository.findAll(sort);
        return  ResponseEntity.ok(loaiSanPhamList.stream().map(LoaiSanPham::toResponse));
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?>phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page){
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(loaiSanPhamRepository.findAll(pageRequest));
    }
    @GetMapping("/detail")
    public ResponseEntity<?>detail(@RequestParam(name = "id")String id){
        Optional<LoaiSanPham> existingLoaiSanPham = loaiSanPhamRepository.findById(id);
        if (existingLoaiSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + id);
        }
        return ResponseEntity.ok(loaiSanPhamRepository.findById(id).stream().map(LoaiSanPham::toResponse));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody LoaiSanPhamRequest loaiSanPhamRequest) {
        if (loaiSanPhamRepository.getLSPByName(loaiSanPhamRequest.getTen().trim())!=null){
            return ResponseEntity.badRequest().body("Tên loại sản phẩm không được trùng!");
        }
        String maLoaiSanPham = loaiSanPhamRequest.getMa();
        if (maLoaiSanPham == null || maLoaiSanPham.trim().isEmpty()) {
            String prefix = "LSP";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 7).toUpperCase();
            } while (loaiSanPhamRepository.getLSPByMa(prefix + uniqueID) != null);
            maLoaiSanPham = prefix + uniqueID;
        } else {
            maLoaiSanPham = maLoaiSanPham.trim();
            if (!Pattern.matches("^LSP[A-Z0-9]{7}$", maLoaiSanPham)) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng LSPXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (loaiSanPhamRepository.getLSPByMa(maLoaiSanPham) != null) {
                return ResponseEntity.badRequest().body("Mã không được trùng!");
            }
        }

        LoaiSanPham loaiSanPham = new LoaiSanPham();
        BeanUtils.copyProperties(loaiSanPhamRequest, loaiSanPham);

        // Kiểm tra danh mục
        if (danhMucRepository.findById(loaiSanPhamRequest.getIdDanhMuc()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tồn tại id danh mục: " + loaiSanPhamRequest.getIdDanhMuc());
        }
        loaiSanPham.setDanhMuc(danhMucRepository.getById(loaiSanPhamRequest.getIdDanhMuc()));

        loaiSanPham.setMa(maLoaiSanPham);
        loaiSanPham.setNgayTao(LocalDateTime.now());
        loaiSanPham.setNgaySua(null);
        loaiSanPhamRepository.save(loaiSanPham);

        return ResponseEntity.ok("Thêm loại sản phẩm thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody LoaiSanPhamRequest loaiSanPhamRequest, @RequestParam(name = "id") String id) {

        Optional<LoaiSanPham> existingLoaiSanPham = loaiSanPhamRepository.findById(id);
        if (existingLoaiSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + id);
        }
        if (loaiSanPhamRepository.getLSPByNameAndId(loaiSanPhamRequest.getTen().trim(),id)!=null){
            return ResponseEntity.badRequest().body("Tên loại sản phẩm không được trùng!");
        }
        String maLoaiSanPham = loaiSanPhamRequest.getMa();
        if (maLoaiSanPham != null && !maLoaiSanPham.trim().isEmpty()) {
            maLoaiSanPham = maLoaiSanPham.trim();
            if (!Pattern.matches("^LSP[A-Z0-9]{7}$", maLoaiSanPham)) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng LSPXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (loaiSanPhamRepository.getLSPByMaAndId(maLoaiSanPham,id) != null) {
                return ResponseEntity.badRequest().body("Mã không được trùng!");
            }
        }

        LoaiSanPham loaiSanPham = existingLoaiSanPham.get();
        BeanUtils.copyProperties(loaiSanPhamRequest, loaiSanPham, "id", "ngayTao");

        if (danhMucRepository.findById(loaiSanPhamRequest.getIdDanhMuc()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tồn tại id danh mục: " + loaiSanPhamRequest.getIdDanhMuc());
        }
        loaiSanPham.setDanhMuc(danhMucRepository.getById(loaiSanPhamRequest.getIdDanhMuc()));

        loaiSanPham.setNgaySua(LocalDateTime.now());
        loaiSanPhamRepository.save(loaiSanPham);

        return ResponseEntity.ok("Cập nhật loại sản phẩm thành công!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?>deleta(@RequestParam(name = "id")String id){
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
