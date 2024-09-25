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
import java.util.UUID;
import java.util.regex.Pattern;

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
        danhMuc.setNgayTao(LocalDateTime.now());
        danhMuc.setNgaySua(null);

        if (danhMucRepository.getByName(danhMuc.getTen().trim())!=null){
            return ResponseEntity.badRequest().body("Tên danh mục không được trùng!");
        }
        if (danhMuc.getMa() == null || danhMuc.getMa().trim().isEmpty()) {
            String prefix = "DM";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (danhMucRepository.getByMa(prefix + uniqueID) != null);
            danhMuc.setMa(prefix + uniqueID);
        } else {
            if (!Pattern.matches("^DM[A-Z0-9]{8}$", danhMuc.getMa().trim())) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng DMXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (danhMucRepository.getByMa(danhMuc.getMa().trim()) != null) {
                return ResponseEntity.badRequest().body("Mã danh mục không được trùng!");
            }
        }


        danhMucRepository.save(danhMuc);
        return ResponseEntity.ok("Thêm danh mục thành công!");
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DanhMuc danhMuc, @RequestParam(name = "id") String id) {
        if (danhMucRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + id);
        }
        if (danhMucRepository.getByNameAndId(id,danhMuc.getTen().trim())!=null){
            return ResponseEntity.badRequest().body("Tên danh mục không được trùng!");
        }
        if (danhMuc.getMa() == null || danhMuc.getMa().trim().isEmpty()) {
            String prefix = "DM";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (danhMucRepository.getByMa(prefix + uniqueID) != null);
            danhMuc.setMa(prefix + uniqueID);
        } else {
            if (!Pattern.matches("^DM[A-Z0-9]{8}$", danhMuc.getMa().trim())) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng DMXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (danhMucRepository.getByMa(danhMuc.getMa().trim()) != null && !danhMucRepository.getByMa(danhMuc.getMa().trim()).getId().equals(id)) {
                return ResponseEntity.badRequest().body("Mã danh mục không được trùng!");
            }
        }
        DanhMuc danhMucUpdate = danhMucRepository.getReferenceById(id);
        danhMucUpdate.setNgaySua(LocalDateTime.now());
        BeanUtils.copyProperties(danhMuc, danhMucUpdate, "id", "ngayTao","ma"); // Giữ nguyên ngày tạo
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
