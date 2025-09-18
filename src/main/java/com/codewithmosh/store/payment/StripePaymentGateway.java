package com.codewithmosh.store.payment;

import com.codewithmosh.store.orders.Order;
import com.codewithmosh.store.orders.OrderItem;
import com.codewithmosh.store.orders.OrderStatus;
import com.codewithmosh.store.orders.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {

    private final OrderService orderService;
    @Value("${websiteUrl}")
    private String successUrl;

    @Value("${stripe.webhookSecreKey}")
    private String webhookSecretKey;

    public StripePaymentGateway(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            String payload = request.getPayload();
            String signature = request.getHeaders().get("stripe-signature");
            Event event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            return switch (event.getType()){
                case "payment_intent.succeeded" ->
                    Optional.of(new PaymentResult(extractOrderId(event), OrderStatus.PAID));
                case "payment_intent.payment_failed" ->
                    Optional.of(new PaymentResult(extractOrderId(event), OrderStatus.FAILED));
                default ->
                    Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }
    }

    private Long extractOrderId(Event event) {
        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow(
                () -> new PaymentException("Could not deserialize Stripe event. Check the SDK and API version.")
        );
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(successUrl + "/checkout-cancel")
                    .setPaymentIntentData(createPaymentIntentData(order));
            //使用登陆服务来处理这个异常, 比如sentry

            order.getOrderItems().forEach(orderItem -> {
                SessionCreateParams.LineItem lineItem = createLineItem(orderItem);
                builder.addLineItem(lineItem);
            });
            Session session = Session.create(builder.build());

            return new CheckoutSession(session.getUrl());
        }catch (StripeException se){
            throw new PaymentException();
        }
    }

    private SessionCreateParams.PaymentIntentData createPaymentIntentData(Order order) {
        return SessionCreateParams.PaymentIntentData.builder()
                .putMetadata("order_id", order.getId().toString())
                .build();
    }

    private SessionCreateParams.LineItem createLineItem(OrderItem orderItem) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(orderItem.getQuantity()))
                .setPriceData(creatPriceData(orderItem))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData creatPriceData(OrderItem orderItem) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("eur")
                .setUnitAmountDecimal(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(orderItem)
                ).build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem orderItem) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(orderItem.getProduct().getName())
                .build();
    }
}
