package ru.szherbekov.productms.service;


import ru.szherbekov.productms.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;


public interface ProductService {

    String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;

}
