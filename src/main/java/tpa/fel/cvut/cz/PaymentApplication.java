package tpa.fel.cvut.cz;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import tpa.fel.cvut.cz.handlers.CreditCardServiceHandler;
import tpa.fel.cvut.cz.handlers.PartnerServiceHandler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class PaymentApplication
{
    private static final String ZEEBE_ADDRESS = "f4550c8d-2c11-4276-866d-8f736c2e11e4.bru-2.zeebe.camunda.io:443";
    private static final String ZEEBE_CLIENT_ID = "INfVKTjH2bKKWFS_y2-HgYFby8F-bqIt";
    private static final String ZEEBE_CLIENT_SECRET = "foMRlp98_OaImhMo5pn9K-aL9o4e~VuLV_7tIMU0AQm_dHt_rOKUpkcu0TZ4sa.m";
    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
    private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";


    public static void main( String[] args )
    {
        final OAuthCredentialsProvider credentialsProvider;
        credentialsProvider = new OAuthCredentialsProviderBuilder()
            .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
            .audience(ZEEBE_TOKEN_AUDIENCE)
            .clientId(ZEEBE_CLIENT_ID)
            .clientSecret(ZEEBE_CLIENT_SECRET)
            .build();

        try (final ZeebeClient client =
                     ZeebeClient.newClientBuilder()
                             .gatewayAddress(ZEEBE_ADDRESS)
                             .credentialsProvider(credentialsProvider)
                             .build()) {

            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("reference", "C8_12345");
            variables.put("amount", Double.valueOf(100.00));
            variables.put("cardNumber", "1234567812345678");
            variables.put("cardExpiry", "12/2023");
            variables.put("cardCVC", "123");
            variables.put("partnerName", "Alois");

            client.newCreateInstanceCommand()
                    .bpmnProcessId("Process_PaymentProcess")
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();

            final JobWorker creditCardWorker =
                    client.newWorker()
                            .jobType("chargeCreditCard")
                            .handler(new CreditCardServiceHandler())
                            .timeout(Duration.ofSeconds(10).toMillis())
                            .open();

            final JobWorker partnerWorker =
                    client.newWorker()
                            .jobType("getRemainingCredit")
                            .handler(new PartnerServiceHandler())
                            .timeout(Duration.ofSeconds(10).toMillis())
                            .open();

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
