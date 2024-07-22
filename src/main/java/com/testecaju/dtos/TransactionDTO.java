package com.testecaju.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Transaction Data Transfer Object")
public record TransactionDTO(
        @Schema(description = "Identificador do usuário", example = "1e0fa047-7f57-41f5-873e-12c31e7c74e4") @NotNull String account,
        @Schema(description = "Valor total da transação", example = "150.99") @NotNull BigDecimal totalAmount,
        @Schema(description = "Merchant Category Code", example = "5812") @NotNull String mcc,
        @Schema(description = "Nome do lojista", example = "UBER TRIP                   SAO PAULO BR") @NotNull String merchant) {
}
