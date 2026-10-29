package com.raajneel.Mapper;

import com.raajneel.Model.WishList;
import com.raajneel.Payload.DTO.WishListDTO;
import org.springframework.stereotype.Component;

@Component
public class WishListMapper {

    public WishListDTO toDTO(WishList wishList) {
        if (wishList == null) {
            return null;
        }

        WishListDTO dto = new WishListDTO();
        dto.setId(wishList.getId());

        if (wishList.getUser() != null) {
            dto.setUserId(wishList.getUser().getId());
            dto.setUserName(wishList.getUser().getFullName());
        }

        if (wishList.getBook() != null) {
            dto.setBookId(wishList.getBook().getId());
            dto.setBookTitle(wishList.getBook().getTitle());
            dto.setBookAuthor(wishList.getBook().getAuthor());
            dto.setBookIsbn(wishList.getBook().getIsbn());
            dto.setBookCoverImageUrl(wishList.getBook().getCoverImageUrl());
            dto.setAvailableCopies(wishList.getBook().getAvailableCopies());
            dto.setIsBookAvailable(wishList.getBook().getAvailableCopies() != null && wishList.getBook().getAvailableCopies() > 0);
        }

        dto.setNotes(wishList.getNotes());
        dto.setCreatedAt(wishList.getCreatedAt());
        dto.setUpdatedAt(wishList.getUpdatedAt());

        return dto;
    }
}
