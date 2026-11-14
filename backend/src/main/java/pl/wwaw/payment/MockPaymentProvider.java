package pl.wwaw.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class MockPaymentProvider implements PaymentProvider {

    static final String declinedBlikCode = "000000";

    @Override
    public PaymentResult charge(BigDecimal amount, PaymentMethod method, String blikCode) {
        if (method == PaymentMethod.BLIK) {
            if (blikCode == null || !blikCode.matches("\\d{6}")) {
                return PaymentResult.declined("Kod BLIK musi składać się z 6 cyfr.");
            }
            if (declinedBlikCode.equals(blikCode)) {
                return PaymentResult.declined("Płatność BLIK została odrzucona.");
            }
        }
        return PaymentResult.approved("MOCK-" + UUID.randomUUID());
    }
}
