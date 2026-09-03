package com.raajneel.Service;

import com.raajneel.Payload.DTO.GenreDTO;

import java.util.List;

public interface GenreService {
    GenreDTO createGenre(GenreDTO genre);
    List<GenreDTO> getAllGenres();
}
