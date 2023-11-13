package tpa.fel.cvut.cz.handlers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import tpa.fel.cvut.cz.services.PartnerService;

import java.util.HashMap;
import java.util.Map;

public class PartnerServiceHandler implements JobHandler {
    PartnerService partnerService = new PartnerService();

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        final Map<String, Object> inputVariables = job.getVariablesAsMap();
        final String partnerName = (String) inputVariables.get("partnerName");
        final Object remainingCredit = partnerService.getRemainingCredit(partnerName);
        final Map<String, Object> outputVariables = new HashMap<String, Object>();
        outputVariables.put("remainingCredit", remainingCredit);
        client.newCompleteCommand(job.getKey()).variables(outputVariables).send().join();
    }
}
