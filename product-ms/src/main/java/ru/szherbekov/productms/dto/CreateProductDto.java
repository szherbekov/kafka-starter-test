package ru.szherbekov.productms.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {

    private String title;
    private BigDecimal price;
    private Integer quantity;

}
