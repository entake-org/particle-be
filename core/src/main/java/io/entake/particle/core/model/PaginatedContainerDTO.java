package io.entake.particle.core.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedContainerDTO<T> {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalCount;
    private List<T> results;
}
