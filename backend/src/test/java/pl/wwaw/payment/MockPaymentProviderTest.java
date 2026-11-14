package pl.wwaw.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MockPaymentProviderTest {

    private final MockPaymentProvider provider = new MockPaymentProvider();
    private static final BigDecimal AMOUNT = new BigDecimal("4.40");

    @Test
    void approvesValidBlikCode() {
        PaymentResult result = provider.charge(AMOUNT, PaymentMethod.BLIK, "123456");

        assertThat(result.approved()).isTrue();
        assertThat(result.reference()).startsWith("MOCK-");
    }

    @Test
    void declinesTheMagicBlikCode() {
        assertThat(provider.charge(AMOUNT, PaymentMethod.BLIK, "000000").approved()).isFalse();
    }

    @Test
    void declinesMalformedBlikCode() {
        assertThat(provider.charge(AMOUNT, PaymentMethod.BLIK, "12ab").approved()).isFalse();
        assertThat(provider.charge(AMOUNT, PaymentMethod.BLIK, null).approved()).isFalse();
    }

    @Test
    void approvesCardPayments() {
        assertThat(provider.charge(AMOUNT, PaymentMethod.CARD, null).approved()).isTrue();
    }
}
