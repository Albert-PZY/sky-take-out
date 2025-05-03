package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import org.springframework.stereotype.Service;

@Service
public interface ShoppingCartService {


    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
