package com.example.microservices.orderservice.Service;

import com.example.microservices.orderservice.DTO.InventoryResponse;
import com.example.microservices.orderservice.DTO.OrderLineItemsDto;
import com.example.microservices.orderservice.DTO.OrderRequest;
import com.example.microservices.orderservice.Model.Order;
import com.example.microservices.orderservice.Model.OrderLineItem;
import com.example.microservices.orderservice.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItemsList =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::MapToDTO)
                .toList();

        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();


        //Call inventory service and place order if the item is in stock
        InventoryResponse[] inventoryResponsesArray = webClient.get()
                .uri("http://localhost:8082/api/inventory" ,
                        uriBuilder -> uriBuilder.queryParam("skuCode" , skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class).block();

        boolean AllProductsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);


        if(AllProductsInStock){
            orderRepository.save(order);
        }
        else{
            throw new IllegalArgumentException("Product is not in stock");
        }


    }

    private OrderLineItem MapToDTO(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItems = new OrderLineItem();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setId(orderLineItemsDto.getId());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }

}

