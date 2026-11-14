package pl.wwaw.payment;

import java.math.BigDecimal;

public interface PaymentProvider {

    PaymentResult charge(BigDecimal amount, PaymentMethod method, String blikCode);
}
