
package com.payment_gateway_integration.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secretKey}")
    private String apiKey;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = apiKey;
    }
}
