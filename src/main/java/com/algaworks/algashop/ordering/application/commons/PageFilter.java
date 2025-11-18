package com.algaworks.algashop.ordering.application.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageFilter {

    private int size = 15;
    private int page = 0;

    public static PageFilter of(int size, int page) {
        return new PageFilter(size, page);
    }

}
