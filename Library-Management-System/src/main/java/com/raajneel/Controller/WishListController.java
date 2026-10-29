package com.raajneel.Controller;

import com.raajneel.Payload.DTO.WishListDTO;
import com.raajneel.Payload.Request.WishListRequest;
import com.raajneel.Payload.Response.ApiResponse;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Service.WishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Wishlist & Reading Lists", description = "Endpoints for managing saved books in a patron's personal reading wishlist")
@SecurityRequirement(name = "bearerAuth")
public class WishListController {

    private final WishListService wishListService;

    @PostMapping
    @Operation(
            summary = "Add book to patron's wishlist",
            description = "Saves a book to the current authenticated patron's personal wishlist with optional reading notes. Duplicates are rejected."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Book successfully added to wishlist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WishListDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book ID invalid, book already in wishlist, or validation failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - JWT token required",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server processing failure",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<WishListDTO> addToWishList(
            @Valid @RequestBody WishListRequest request) throws Exception {
        WishListDTO dto = wishListService.addToWishList(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remove an item from wishlist by wishlist record ID",
            description = "Removes a saved book item from the authenticated patron's wishlist using the wishlist entry ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Item successfully removed from wishlist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Wishlist item not found or user is not the owner",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> removeFromWishList(
            @Parameter(description = "Wishlist record identifier to remove", example = "601", required = true)
            @PathVariable Long id) throws Exception {
        wishListService.removeFromWishList(id);
        return ResponseEntity.ok(new ApiResponse("Item removed from wishlist successfully", true));
    }

    @DeleteMapping("/book/{bookId}")
    @Operation(
            summary = "Remove a book from wishlist by book ID",
            description = "Removes a book from the current patron's wishlist using the target book's identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Book removed from wishlist successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Book was not found in patron's wishlist",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse> removeBookFromWishList(
            @Parameter(description = "Identifier of book to remove from wishlist", example = "1", required = true)
            @PathVariable Long bookId) throws Exception {
        wishListService.removeBookFromWishList(bookId);
        return ResponseEntity.ok(new ApiResponse("Book removed from wishlist successfully", true));
    }

    @GetMapping("/my")
    @Operation(
            summary = "Get current patron's paginated wishlist",
            description = "Returns a paginated list of books in the authenticated user's wishlist, including inventory availability status."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paginated wishlist returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<PageResponse<WishListDTO>> getMyWishList(
            @Parameter(description = "Page index (0-based)", example = "0", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size limit", example = "20", required = false)
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort property", example = "createdAt", required = false)
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", schema = @Schema(allowableValues = {"ASC", "DESC"}), example = "DESC", required = false)
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) throws Exception {
        PageResponse<WishListDTO> response = wishListService.getMyWishList(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/all")
    @Operation(
            summary = "Get all items in patron's wishlist as a list",
            description = "Retrieves the complete unpaged list of all books saved in the patron's wishlist."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Complete wishlist list returned successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WishListDTO.class)))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<List<WishListDTO>> getAllMyWishList() throws Exception {
        List<WishListDTO> items = wishListService.getAllMyWishList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/check/{bookId}")
    @Operation(
            summary = "Check if a book is present in current patron's wishlist",
            description = "Returns a boolean flag `{ inWishlist: true|false }` indicating whether the target book has already been added to the authenticated patron's wishlist."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Wishlist status checked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"inWishlist\": true}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<Map<String, Boolean>> isBookInWishList(
            @Parameter(description = "Book identifier to check", example = "1", required = true)
            @PathVariable Long bookId) throws Exception {
        boolean inWishlist = wishListService.isBookInWishList(bookId);
        return ResponseEntity.ok(Collections.singletonMap("inWishlist", inWishlist));
    }

    @GetMapping("/count")
    @Operation(
            summary = "Get total count of books in patron's wishlist",
            description = "Calculates and returns the aggregate count of wishlist items saved by the currently logged-in user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Total wishlist items count returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"count\": 5}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<Map<String, Long>> getMyWishListCount() throws Exception {
        long count = wishListService.getMyWishListCount();
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }
}
