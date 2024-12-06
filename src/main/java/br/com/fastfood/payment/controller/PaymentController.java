package br.com.fastfood.payment.controller;

import br.com.fastfood.payment.dto.PaymentDto;
import br.com.fastfood.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService service;

    @GetMapping
    public Page<PaymentDto> list(@PageableDefault(size = 10) Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> detail(@PathVariable @NotNull Long id) {
        PaymentDto dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PaymentDto> register(@RequestBody @Valid PaymentDto dto, UriComponentsBuilder builder) {
        PaymentDto paymentDto = service.add(dto);
        URI uri = builder.path("/payment/{id}").buildAndExpand(paymentDto.getId()).toUri();

        return ResponseEntity.created(uri).body(paymentDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> update(@PathVariable @NotNull Long id, @RequestBody @Valid PaymentDto dto) {
        PaymentDto updatedDto = service.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentDto> remove(@PathVariable @NotNull Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmed")
    @CircuitBreaker(name = "updatePayment", fallbackMethod = "authorizedPaymentWithPendingIntegration")
    public void confirmePayment(@PathVariable @NotNull Long id) {
        service.confirmePayment(id);
    }

    public void authorizedPaymentWithPendingIntegration(Long id, Exception exception) {
        service.updateStatus(id);
    }

}
