package com.store.Online.Store.controllers;

import com.store.Online.Store.dto.ProductRequest;
import com.store.Online.Store.service.productService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class ProductControllerTest {

    @Mock
    private productService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @Test
    public void testAddProduct() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        // Мокаем данные
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                Files.readAllBytes(Paths.get("D:\\javaCode\\electronics_online_shop\\Online-Store\\src\\test\\java\\image\\test.jpg"))
        );

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(MockMvcRequestBuilders.multipart("/products")
                        .file(multipartFile)
                        .param("productName", "Название продукта")
                        .param("description", "Описание продукта")
                        .param("stockQuantity", "10")
                        .param("price", "50.00")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
        verify(productService, times(1)).addProduct(any(ProductRequest.class), any(MultipartFile.class));

    }



}