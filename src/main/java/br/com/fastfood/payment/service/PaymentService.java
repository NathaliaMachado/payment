package br.com.fastfood.payment.service;

import br.com.fastfood.payment.dto.PaymentDto;
import br.com.fastfood.payment.http.OrderClient;
import br.com.fastfood.payment.model.Payment;
import br.com.fastfood.payment.model.Status;
import br.com.fastfood.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository repository;

    @Autowired
    OrderClient orderClient;

    @Autowired
    ModelMapper mapper;

    public Page<PaymentDto> findAll(Pageable pageable) {
        return repository
                .findAll(pageable)
                .map(p -> mapper.map(p, PaymentDto.class));
    }

    public PaymentDto findById(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        return mapper.map(payment, PaymentDto.class);
    }

    public PaymentDto add(PaymentDto dto) {
        Payment payment = mapper.map(dto, Payment.class);
        payment.setStatus(Status.CREATED);
        repository.save(payment);

        return mapper.map(payment, PaymentDto.class);
    }

    public PaymentDto update(Long id, PaymentDto dto) {
        Payment payment = mapper.map(dto, Payment.class);
        payment.setId(id);
        payment = repository.save(payment);

        return mapper.map(payment, PaymentDto.class);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void confirmePayment(Long id) {
        Optional<Payment> payment = repository.findById(id);

        if(!payment.isPresent()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.CONFIRMED);
        repository.save(payment.get());
        orderClient.updatePayment(payment.get().getOrderId());
    }

    public void updateStatus(Long id) {
        Optional<Payment> payment = repository.findById(id);

        if(!payment.isPresent()) {
            throw new EntityNotFoundException();
        }

        payment.get().setStatus(Status.CONFIRMED_WITHOUT_INTEGRATION);
        repository.save(payment.get());
    }
}
