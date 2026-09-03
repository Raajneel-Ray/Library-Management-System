package com.raajneel.Service.Implementation;

import com.raajneel.Mapper.GenreMapper;
import com.raajneel.Model.Genre;
import com.raajneel.Payload.DTO.GenreDTO;
import com.raajneel.Repository.GenreRepository;
import com.raajneel.Service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    @Override
    public GenreDTO createGenre(GenreDTO genreDTO) {
        Genre genre = genreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toDTO(savedGenre);
    }

    @Override
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre-> genreMapper.toDTO(genre))
                .collect(Collectors.toList());
    }
}
