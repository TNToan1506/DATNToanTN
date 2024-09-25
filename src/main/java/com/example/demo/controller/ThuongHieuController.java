package com.example.demo.controller;

import com.example.demo.entities.ThuongHieu;
import com.example.demo.repositories.ThuongHieuRepository;
import com.example.demo.request.ThuongHieuRequest;
import com.example.demo.respone.ThuongHieuResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("thuong-hieu")
public class ThuongHieuController {

    @Autowired
    ThuongHieuRepository thuongHieuRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(thuongHieuRepository.findAll().stream()
                .map(ThuongHieu::toResponse).collect(Collectors.toList()));
    }
    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam(name = "id")String id) {
        if (thuongHieuRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        return ResponseEntity.ok(thuongHieuRepository.findById(id).stream().map(ThuongHieu::toResponse));
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ThuongHieuRequest thuongHieuRequest) {
        if (thuongHieuRepository.getByName(thuongHieuRequest.getTen().trim())!=null){
            return ResponseEntity.badRequest().body("Tên không được trùng!");
        }
        String maThuongHieu = thuongHieuRequest.getMa();
        if (maThuongHieu == null || maThuongHieu.trim().isEmpty()) {
            String prefix = "TH";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (thuongHieuRepository.getByMa(prefix + uniqueID) != null);
            maThuongHieu = prefix + uniqueID;
        } else {
            maThuongHieu = maThuongHieu.trim();
            if (!Pattern.matches("^TH[A-Z0-9]{8}$", maThuongHieu)) {
                return ResponseEntity.badRequest().body("Mã thương hiệu phải có định dạng THXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (thuongHieuRepository.getByMa(maThuongHieu) != null) {
                return ResponseEntity.badRequest().body("Mã không được trùng!");
            }
        }

        ThuongHieu thuongHieu = new ThuongHieu();
        BeanUtils.copyProperties(thuongHieuRequest, thuongHieu);
        thuongHieu.setMa(maThuongHieu);
        thuongHieu.setNgayTao(LocalDateTime.now());
        thuongHieu.setNgaySua(null);

        thuongHieuRepository.save(thuongHieu);
        return ResponseEntity.ok("Thêm thương hiệu thành công!");
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ThuongHieuRequest thuongHieuRequest, @RequestParam(name = "id") String id) {
        Optional<ThuongHieu> optionalThuongHieu = thuongHieuRepository.findById(id);
        if (optionalThuongHieu.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        if (thuongHieuRepository.getByNameAndId(thuongHieuRequest.getTen().trim(),id)!=null){
            return ResponseEntity.badRequest().body("Tên không được trùng!");
        }
        String maThuongHieu = thuongHieuRequest.getMa();
        if (maThuongHieu != null && !maThuongHieu.trim().isEmpty()) {
            maThuongHieu = maThuongHieu.trim();
            if (!Pattern.matches("^TH[A-Z0-9]{8}$", maThuongHieu)) {
                return ResponseEntity.badRequest().body("Mã thương hiệu phải có định dạng THXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (thuongHieuRepository.getByIdAndMa(id, maThuongHieu) != null) {
                return ResponseEntity.badRequest().body("Mã không được trùng!");
            }
        }

        ThuongHieu thuongHieuUpdate = optionalThuongHieu.get();
        BeanUtils.copyProperties(thuongHieuRequest, thuongHieuUpdate, "id", "ngayTao");
        thuongHieuUpdate.setNgaySua(LocalDateTime.now());

        thuongHieuRepository.save(thuongHieuUpdate);
        return ResponseEntity.ok("Cập nhật thương hiệu thành công!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
        if (thuongHieuRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        thuongHieuRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thương hiệu thành công!");
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
