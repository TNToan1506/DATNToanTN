package com.example.demo.controller;

import com.example.demo.entities.DanhGia;
import com.example.demo.entities.DanhMuc;
import com.example.demo.repositories.DanhMucRepository;
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
@RequestMapping("danh-muc")
public class DanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<DanhMuc> danhMucList = danhMucRepository.findAll(sort);
        return ResponseEntity.ok(danhMucList);
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(danhMucRepository.findAll(pageRequest));
    }
    @GetMapping("/detail")
    public ResponseEntity<?>detail(@RequestParam(name = "id")String id){
        if (danhMucRepository.getById(id)==null){
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: "+id);
        }
        return ResponseEntity.ok(danhMucRepository.getById(id));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody DanhMuc danhMuc) {
        danhMuc.setMa(danhMuc.getMa().trim());
        danhMuc.setNgayTao(LocalDateTime.now());
        danhMuc.setNgaySua(null);
        if (danhMucRepository.getByMa(danhMuc.getMa()) != null) {
            return ResponseEntity.badRequest().body("Mã danh mục không được trùng!");
        }
        danhMucRepository.save(danhMuc);
        return ResponseEntity.ok("Thêm danh mục thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhMuc danhMuc, @RequestParam(name = "id") String id) {
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
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
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
