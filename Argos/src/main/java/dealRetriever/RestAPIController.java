package dealRetriever;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPIController {

    
    @RequestMapping(value = "/getDeals", produces = "application/json")
    public String getDeals() {
    	String jsonResponse = HUKDRetriever.startRetrieval();
    	return jsonResponse;
    }
    
}