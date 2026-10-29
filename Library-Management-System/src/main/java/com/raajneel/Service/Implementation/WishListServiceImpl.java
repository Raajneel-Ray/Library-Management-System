package com.raajneel.Service.Implementation;

import com.raajneel.Exception.BookException;
import com.raajneel.Exception.WishListException;
import com.raajneel.Mapper.WishListMapper;
import com.raajneel.Model.Book;
import com.raajneel.Model.User;
import com.raajneel.Model.WishList;
import com.raajneel.Payload.DTO.WishListDTO;
import com.raajneel.Payload.Request.WishListRequest;
import com.raajneel.Payload.Response.PageResponse;
import com.raajneel.Repository.BookRepository;
import com.raajneel.Repository.WishListRepository;
import com.raajneel.Service.UserService;
import com.raajneel.Service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final WishListMapper wishListMapper;

    @Override
    @Transactional
    public WishListDTO addToWishList(WishListRequest request) throws Exception {
        User currentUser = userService.getCurrentUser();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException("Book not found with ID: " + request.getBookId()));

        if (wishListRepository.existsByUserIdAndBookId(currentUser.getId(), book.getId())) {
            throw new WishListException("Book is already in your wishlist");
        }

        WishList wishList = WishList.builder()
                .user(currentUser)
                .book(book)
                .notes(request.getNotes())
                .build();

        WishList saved = wishListRepository.save(wishList);
        return wishListMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void removeFromWishList(Long wishListId) throws Exception {
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new WishListException("Wishlist item not found with ID: " + wishListId));

        User currentUser = userService.getCurrentUser();
        if (!wishList.getUser().getId().equals(currentUser.getId())) {
            throw new WishListException("You can only remove items from your own wishlist");
        }

        wishListRepository.delete(wishList);
    }

    @Override
    @Transactional
    public void removeBookFromWishList(Long bookId) throws Exception {
        User currentUser = userService.getCurrentUser();
        WishList wishList = wishListRepository.findByUserIdAndBookId(currentUser.getId(), bookId)
                .orElseThrow(() -> new WishListException("Book not found in your wishlist"));

        wishListRepository.delete(wishList);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WishListDTO> getMyWishList(int page, int size, String sortBy, String sortDirection) throws Exception {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        Page<WishList> wishListPage = wishListRepository.findByUserId(currentUser.getId(), pageable);
        return buildPageResponse(wishListPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishListDTO> getAllMyWishList() throws Exception {
        User currentUser = userService.getCurrentUser();
        List<WishList> wishLists = wishListRepository.findByUserId(currentUser.getId());
        return wishLists.stream()
                .map(wishListMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookInWishList(Long bookId) throws Exception {
        User currentUser = userService.getCurrentUser();
        return wishListRepository.existsByUserIdAndBookId(currentUser.getId(), bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyWishListCount() throws Exception {
        User currentUser = userService.getCurrentUser();
        return wishListRepository.countByUserId(currentUser.getId());
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        int validatedSize = Math.max(1, Math.min(size, 100));
        String validSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        Sort sort = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.by(validSortBy).ascending()
                : Sort.by(validSortBy).descending();
        return PageRequest.of(Math.max(0, page), validatedSize, sort);
    }

    private PageResponse<WishListDTO> buildPageResponse(Page<WishList> wishListPage) {
        List<WishListDTO> dtoList = wishListPage.getContent().stream()
                .map(wishListMapper::toDTO)
                .toList();

        return new PageResponse<>(
                dtoList,
                wishListPage.getNumber(),
                wishListPage.getSize(),
                (int) wishListPage.getTotalElements(),
                wishListPage.getTotalPages(),
                wishListPage.isLast(),
                wishListPage.isFirst(),
                wishListPage.isEmpty()
        );
    }
}
