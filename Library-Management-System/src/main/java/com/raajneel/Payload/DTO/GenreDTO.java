package com.raajneel.Payload.DTO;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Data transfer object representing a book genre taxonomy classification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreDTO {

    @Schema(description = "Unique identifier of the genre", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @Schema(description = "System alphanumeric code representing the genre", example = "SCI-FI", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String code;

    @NotBlank(message = "Genre name is Mandatory")
    @Schema(description = "Human-readable genre name", example = "Science Fiction", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Detailed description of the subject area covered by this genre", example = "Speculative fiction dealing with imaginative concepts such as futuristic science and technology.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Min(value = 0, message = "Display order cannot be negative")
    @Builder.Default
    @Schema(description = "Sequential presentation order for navigational menus", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer displayOrder = 0;

    @Schema(description = "Whether the genre is currently active and publicly visible", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean active;

    @Schema(description = "Identifier of parent genre for hierarchical grouping (null if top-level)", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long parentGenreId;

    @Schema(description = "Display name of the parent genre", example = "Fiction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String parentGenreName;

    @ArraySchema(
            arraySchema = @Schema(description = "List of direct child sub-genres nested under this genre", requiredMode = Schema.RequiredMode.NOT_REQUIRED),
            schema = @Schema(description = "Child genre definition", implementation = GenreDTO.class)
    )
    private List<GenreDTO> subGenre;

    @Schema(description = "Count of active book titles cataloged under this genre", example = "128", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long bookCount;

    @Schema(description = "Record creation timestamp", example = "2026-01-01T10:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "Record last modification timestamp", example = "2026-02-15T16:45:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime updatedAt;
}
