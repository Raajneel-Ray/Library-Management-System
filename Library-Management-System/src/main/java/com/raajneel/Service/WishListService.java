package com.raajneel.Service;

import com.raajneel.Payload.DTO.WishListDTO;
import com.raajneel.Payload.Request.WishListRequest;
import com.raajneel.Payload.Response.PageResponse;

import java.util.List;

public interface WishListService {

    WishListDTO addToWishList(WishListRequest request) throws Exception;

    void removeFromWishList(Long wishListId) throws Exception;

    void removeBookFromWishList(Long bookId) throws Exception;

    PageResponse<WishListDTO> getMyWishList(int page, int size, String sortBy, String sortDirection) throws Exception;

    List<WishListDTO> getAllMyWishList() throws Exception;

    boolean isBookInWishList(Long bookId) throws Exception;

    long getMyWishListCount() throws Exception;
}
