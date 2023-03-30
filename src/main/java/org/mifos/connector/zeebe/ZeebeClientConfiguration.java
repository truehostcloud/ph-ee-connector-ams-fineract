package org.mifos.connector.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for zeebe client.
 */
@Configuration
public class ZeebeClientConfiguration {

    @Value("${zeebe.broker.contactpoint}")
    private String zeebeBrokerContactPoint;

    @Value("${zeebe.client.max-execution-threads}")
    private int zeebeClientMaxThreads;

    /**
     * Configures the zeebe client to be used in communicating with the zeebe broker.
     *
     * @return {@link ZeebeClient}
     */
    @Bean
    public ZeebeClient setup() {
        return ZeebeClient.newClientBuilder()
            .gatewayAddress(zeebeBrokerContactPoint)
            .usePlaintext()
            .numJobWorkerExecutionThreads(zeebeClientMaxThreads)
            .build();
    }
}
