package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatecartItemRequest {
    @NotNull(message = "Quantity must be provided.")
    @Min(value = 1, message = "Quantity must be greater that 0.")
    @Max(value = 1000, message = "Quantity must be less than or equal than 1000")
    private Integer quantity;
}
