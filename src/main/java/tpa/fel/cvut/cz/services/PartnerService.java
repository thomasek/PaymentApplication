package tpa.fel.cvut.cz.services;

import java.util.HashMap;
import java.util.Map;

public class PartnerService {
    final Map<String, Object> accounts = new HashMap<>();

    public PartnerService(){
        accounts.put("Tom", 123.0);
        accounts.put("Alois", 456.0);
    }

    public Object getRemainingCredit(String partnerName){
        var remainingCredit = accounts.get(partnerName);
        System.out.println("remainingCredit: " + remainingCredit);
        return remainingCredit;
    }
}
