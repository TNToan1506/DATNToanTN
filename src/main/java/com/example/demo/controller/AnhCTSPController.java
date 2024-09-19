package com.example.demo.controller;

import com.example.demo.entities.AnhCTSP;
import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.repositories.AnhCTSPRepository;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import com.example.demo.request.AnhCTSPRequest;
import com.example.demo.respone.AnhCTSPResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("anh-sp")
public class AnhCTSPController {

    @Autowired
    private AnhCTSPRepository anhCTSPRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<AnhCTSP> anhCTSPList = anhCTSPRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
        List<AnhCTSPResponse> responseList = anhCTSPList.stream()
                .map(AnhCTSP::toAnhSPResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<AnhCTSP> anhCTSPPage = anhCTSPRepository.findAll(pageRequest);
        List<AnhCTSPResponse> responseList = anhCTSPPage.stream()
                .map(AnhCTSP::toAnhSPResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/detail")
    public ResponseEntity<?>detail(@RequestParam(name = "id")String id){
        if (anhCTSPRepository.findById(id)!=null){
            return ResponseEntity.ok(anhCTSPRepository.findById(id).stream().map(AnhCTSP::toAnhSPResponse));
        }
        return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: "+id);

    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody AnhCTSPRequest anhSPRequest) {
        AnhCTSP anhSP = new AnhCTSP();

        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getById(anhSPRequest.getIdCTSP());

        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ChiTietSanPham có id: " + anhSPRequest.getIdCTSP());
        }

        BeanUtils.copyProperties(anhSPRequest, anhSP);
        anhSP.setNgayTao(LocalDateTime.now());
        anhSP.setNgaySua(null);
        anhSP.setLink(anhSP.getLink().trim());
        anhSP.setChiTietSanPham(chiTietSanPham);

        anhCTSPRepository.save(anhSP);
        return ResponseEntity.ok("Thêm ảnh thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AnhCTSPRequest anhSPRequest, @RequestParam(name = "id") String id) {
        AnhCTSP existingAnhSP = anhCTSPRepository.getById(id);

        if (existingAnhSP != null) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getById(anhSPRequest.getIdCTSP());

            if (chiTietSanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy ChiTietSanPham có id: " + anhSPRequest.getIdCTSP());
            }

            BeanUtils.copyProperties(anhSPRequest, existingAnhSP, "id", "ngayTao");

            existingAnhSP.setChiTietSanPham(chiTietSanPham);
            existingAnhSP.setNgaySua(LocalDateTime.now());
            anhCTSPRepository.save(existingAnhSP);
            return ResponseEntity.ok("Cập nhật ảnh thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
        if (anhCTSPRepository.getById(id)==null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
        }
        anhCTSPRepository.deleteById(id);
        return ResponseEntity.ok("Xóa ảnh thành công!");
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
