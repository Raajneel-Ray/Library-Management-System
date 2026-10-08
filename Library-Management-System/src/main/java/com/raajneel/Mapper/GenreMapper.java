package com.raajneel.Mapper;

import com.raajneel.Model.Genre;
import com.raajneel.Payload.DTO.GenreDTO;
import com.raajneel.Repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;
    // it receives the entity and converts it to DTO
    public GenreDTO toDTO(Genre savedGenre) {
        if(savedGenre == null) {
            return null;
        }
        Map<Long, Long> bookCountMap = getBookCountMap();
        return toDTO(savedGenre, bookCountMap);
    }

    public GenreDTO toDTO(Genre savedGenre, Map<Long, Long> bookCountMap) {
        if(savedGenre == null) {
            return null;
        }
        Long count = (bookCountMap != null && savedGenre.getId() != null)
                ? bookCountMap.getOrDefault(savedGenre.getId(), 0L)
                : (savedGenre.getId() != null ? genreRepository.countBooksByGenre(savedGenre.getId()) : 0L);

        GenreDTO dto = GenreDTO.builder()
                .id(savedGenre.getId())
                .code(savedGenre.getCode())
                .name(savedGenre.getName())
                .description(savedGenre.getDescription())
                .displayOrder(savedGenre.getDisplayOrder())
                .active(savedGenre.isActive())
                .bookCount(count)
                .createdAt(savedGenre.getCreatedAt())
                .updatedAt(savedGenre.getUpdatedAt())
                .build();
        if(savedGenre.getParentGenre()!=null) {
            dto.setParentGenreId(savedGenre.getParentGenre().getId());
            dto.setParentGenreName(savedGenre.getParentGenre().getName());
        }
        if(savedGenre.getSubGenres()!=null && !savedGenre.getSubGenres().isEmpty()) {
            dto.setSubGenre(savedGenre.getSubGenres().stream()
                    .filter(subGenre -> subGenre.isActive())
                    .map(subGenre -> toDTO(subGenre, bookCountMap))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // it receives the dto and converts to entity
    public Genre toEntity(GenreDTO genreDTO) {
        if(genreDTO==null) {
            return null;
        }
        Genre genre = Genre.builder()
                .code(genreDTO.getCode())
                .name(genreDTO.getName())
                .description(genreDTO.getDescription())
                .displayOrder(genreDTO.getDisplayOrder())
                .active(true)
                .build();
        if(genreDTO.getParentGenreId()!=null){
            genreRepository.findById(genreDTO.getParentGenreId()).
                    ifPresent(genre::setParentGenre);
        }
        return genre;
    }

    public void updateEntityFromDTO(GenreDTO genreDTO, Genre existingGenre) {
        if(genreDTO == null || existingGenre == null) {
            return;
        }
        existingGenre.setCode(genreDTO.getCode());
        existingGenre.setName(genreDTO.getName());
        existingGenre.setDescription(genreDTO.getDescription());
        existingGenre.setDisplayOrder(genreDTO.getDisplayOrder() != null ? genreDTO.getDisplayOrder() : 0);
        if(genreDTO.getActive() != null) {
            existingGenre.setActive(genreDTO.getActive());
        }
        if(genreDTO.getParentGenreId() != null) {
            genreRepository.findById(genreDTO.getParentGenreId()).ifPresent(existingGenre::setParentGenre);
        }
    }

    public List<GenreDTO> toDTOList(List<Genre> genreList) {
        if (genreList == null || genreList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> bookCountMap = getBookCountMap();
        return genreList.stream()
                .map(genre -> toDTO(genre, bookCountMap))
                .collect(Collectors.toList());
    }

    public Map<Long, Long> getBookCountMap() {
        List<Object[]> results = genreRepository.countBooksGroupedByGenre();
        Map<Long, Long> map = new HashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                    Long genreId = ((Number) row[0]).longValue();
                    Long count = ((Number) row[1]).longValue();
                    map.put(genreId, count);
                }
            }
        }
        return map;
    }
}
