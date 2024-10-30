package br.com.fastfood.payment.dto;

import br.com.fastfood.payment.model.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDto {

    private Long id;
    private BigDecimal value;
    private String name;
    private String number;
    private String expiry;
    private String code;
    private Status status;
    private Long orderId;
    private Long paymentMethodId;
}
