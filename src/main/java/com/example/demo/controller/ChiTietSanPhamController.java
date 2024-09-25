package com.example.demo.controller;

import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.entities.GiamGia;
import com.example.demo.entities.SanPham;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import com.example.demo.repositories.GiamGiaRepository;
import com.example.demo.repositories.SanPhamRepository;
import com.example.demo.request.ChiTietSanPhamRequest;
import com.example.demo.respone.ChiTietSanPhamResponse;
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
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chi-tiet-san-pham")
public class ChiTietSanPhamController {
    @Autowired
    SanPhamRepository sanPhamRepository;
//    @Autowired
//    GiamGiaRepository giamGiaRepository;
    @Autowired
    ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll(Sort.by(Sort.Order.desc("ngayTao")));
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhams.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());

        // Kiểm tra các sản phẩm sắp hết hạn
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);

        List<ChiTietSanPham> expiringSoonList = chiTietSanPhams.stream()
                .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
                .collect(Collectors.toList());

        if (!expiringSoonList.isEmpty()) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("products", responseList);
                put("message", "Có sản phẩm sắp hết hạn trong vòng 6 tháng. Xem chi tiết tại: /chi-tiet-san-pham/expiring-soon");
            }});
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/page")
    public ResponseEntity<?> page(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("ngayTao")));
        Page<ChiTietSanPham> chiTietSanPhamPage = chiTietSanPhamRepository.findAll(pageRequest);
        List<ChiTietSanPhamResponse> responseList = chiTietSanPhamPage.stream()
                .map(ChiTietSanPham::toChiTietSanPhamResponse)
                .collect(Collectors.toList());

        // Kiểm tra các sản phẩm sắp hết hạn
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);

        List<ChiTietSanPham> expiringSoonList = chiTietSanPhamPage.getContent().stream()
                .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
                .collect(Collectors.toList());

        if (!expiringSoonList.isEmpty()) {
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("products", responseList);
                put("message", "Có sản phẩm sắp hết hạn trong vòng 6 tháng. Xem chi tiết tại: /chi-tiet-san-pham/expiring-soon");
            }});
        }

        return ResponseEntity.ok(responseList);
    }
    @GetMapping("/expiring-soon")
    public ResponseEntity<?> getExpiringSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);

        // Tìm tất cả các chi tiết sản phẩm và lọc những sản phẩm sắp hết hạn
        List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll();
        List<Map<String, Object>> expiringSoonList = chiTietSanPhams.stream()
                .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
                .map(ctsp -> {
                    Map<String, Object> productDetails = new HashMap<>();
                    ChiTietSanPhamResponse response = ctsp.toChiTietSanPhamResponse();
                    productDetails.put("product", response);
                    productDetails.put("detailsLink", "/chi-tiet-san-pham/detail?id=" + ctsp.getId()); // Tạo liên kết chi tiết sản phẩm
                    return productDetails;
                })
                .collect(Collectors.toList());

        if (expiringSoonList.isEmpty()) {
            return ResponseEntity.ok("Không có sản phẩm nào sắp hết hạn trong vòng 6 tháng.");
        }

        return ResponseEntity.ok(expiringSoonList);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam(name = "id")String id) {
        if (chiTietSanPhamRepository.getById(id)==null){
            return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: "+id);
        }
        return ResponseEntity.ok(chiTietSanPhamRepository.findById(id)
                .stream().map(ChiTietSanPham::toChiTietSanPhamResponse));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest) {
        chiTietSanPhamRequest.setMa(chiTietSanPhamRequest.getMa().trim());
        chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());

        // Kiểm tra mã và tạo mã mới nếu mã không được cung cấp
        if (chiTietSanPhamRequest.getMa() == null || chiTietSanPhamRequest.getMa().isEmpty()) {
            String generatedMa;
            do {
                String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
                generatedMa = "CTSP" + randomString;
            } while (chiTietSanPhamRepository.getByMa(generatedMa) != null);
            chiTietSanPhamRequest.setMa(generatedMa);
        } else if (!Pattern.matches("^CTSP[A-Z0-9]{6}$", chiTietSanPhamRequest.getMa().trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
        } else if (chiTietSanPhamRepository.getByMa(chiTietSanPhamRequest.getMa().trim()) != null) {
            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
        }

        // Kiểm tra sản phẩm trùng
        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.trungCTSP(
                chiTietSanPhamRequest.getIdSP(),
                chiTietSanPhamRequest.getSoNgaySuDung(),
                chiTietSanPhamRequest.getNgaySanXuat(),
                chiTietSanPhamRequest.getHsd(),
                chiTietSanPhamRequest.getGia());

        if (existingChiTietSanPham != null) {
            existingChiTietSanPham.setSoLuong(existingChiTietSanPham.getSoLuong() + chiTietSanPhamRequest.getSoLuong());
            chiTietSanPhamRepository.save(existingChiTietSanPham);
            return ResponseEntity.ok("Sản phẩm đã tồn tại, số lượng đã được cập nhật!");
        }

        ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
        BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);
        chiTietSanPham.setNgayTao(LocalDateTime.now());
        chiTietSanPham.setNgaySua(null);

        // Xử lý sản phẩm
        if (chiTietSanPhamRequest.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(chiTietSanPhamRequest.getIdSP()).orElse(null);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với id: " + chiTietSanPhamRequest.getIdSP());
            }
            chiTietSanPham.setSanPham(sanPham);
        }
        chiTietSanPhamRepository.save(chiTietSanPham);
        return ResponseEntity.ok("Thêm mới chi tiết sản phẩm thành công!");
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest,
                                    @RequestParam(name = "id") String id) {
        ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.findById(id).orElse(null);
        if (existingChiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }

        chiTietSanPhamRequest.setMa(chiTietSanPhamRequest.getMa().trim());
        chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());

        ChiTietSanPham trungCTSP = chiTietSanPhamRepository.trungCTSP(
                chiTietSanPhamRequest.getIdSP(),
                chiTietSanPhamRequest.getSoNgaySuDung(),
                chiTietSanPhamRequest.getNgaySanXuat(),
                chiTietSanPhamRequest.getHsd(),
                chiTietSanPhamRequest.getGia());

        if (trungCTSP != null && !trungCTSP.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Chi tiết sản phẩm đã tồn tại với các thuộc tính tương ứng!");
        }
//        Mã thì không được update
//        if (!Pattern.matches("^CTSP[A-Z0-9]{6}$", chiTietSanPhamRequest.getMa())) {
//            return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
//        }
//        if (chiTietSanPhamRepository.getByMaAndId(chiTietSanPhamRequest.getMa(), id) != null) {
//            return ResponseEntity.badRequest().body("Mã chi tiết sản phẩm không được trùng!");
//        }

        if (chiTietSanPhamRequest.getIdSP() != null) {
            SanPham sanPham = sanPhamRepository.findById(chiTietSanPhamRequest.getIdSP()).orElse(null);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với id: " + chiTietSanPhamRequest.getIdSP());
            }
            existingChiTietSanPham.setSanPham(sanPham);
        }

        BeanUtils.copyProperties(chiTietSanPhamRequest, existingChiTietSanPham, "id","ma","ngayTao");
        existingChiTietSanPham.setNgaySua(LocalDateTime.now());

        chiTietSanPhamRepository.save(existingChiTietSanPham);
        return ResponseEntity.ok("Cập nhật chi tiết sản phẩm thành công!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam(name = "id") String id) {
        if (chiTietSanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
        }
        chiTietSanPhamRepository.deleteById(id);
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
