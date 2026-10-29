package com.raajneel.Controller;

import com.raajneel.Payload.DTO.WishListDTO;
import com.raajneel.Payload.Request.WishListRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.WishListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/wishlist", "/api/wishlists"})
public class WishListController {

    private final WishListService wishListService;

    @PostMapping
    public ResponseEntity<WishListDTO> addToWishList(@Valid @RequestBody WishListRequest request) throws Exception {
        WishListDTO dto = wishListService.addToWishList(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> removeFromWishList(@PathVariable Long id) throws Exception {
        wishListService.removeFromWishList(id);
        return ResponseEntity.ok(new ApiResponse("Item removed from wishlist successfully", true));
    }

    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse> removeBookFromWishList(@PathVariable Long bookId) throws Exception {
        wishListService.removeBookFromWishList(bookId);
        return ResponseEntity.ok(new ApiResponse("Book removed from wishlist successfully", true));
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<WishListDTO>> getMyWishList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) throws Exception {
        PageResponse<WishListDTO> response = wishListService.getMyWishList(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/all")
    public ResponseEntity<List<WishListDTO>> getAllMyWishList() throws Exception {
        List<WishListDTO> items = wishListService.getAllMyWishList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/check/{bookId}")
    public ResponseEntity<Map<String, Boolean>> isBookInWishList(@PathVariable Long bookId) throws Exception {
        boolean inWishlist = wishListService.isBookInWishList(bookId);
        return ResponseEntity.ok(Collections.singletonMap("inWishlist", inWishlist));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getMyWishListCount() throws Exception {
        long count = wishListService.getMyWishListCount();
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }
}
