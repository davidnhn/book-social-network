package com.davidnhn.book.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int number;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
}
