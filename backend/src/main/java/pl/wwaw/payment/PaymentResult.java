package pl.wwaw.payment;

public record PaymentResult(boolean approved, String reference, String message) {

    public static PaymentResult approved(String reference) {
        return new PaymentResult(true, reference, "OK");
    }

    public static PaymentResult declined(String message) {
        return new PaymentResult(false, null, message);
    }
}
