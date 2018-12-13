package yellow.mongo.proxy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageController {

    @RequestMapping(value="/client", method = RequestMethod.GET)
    public String clientList(@RequestParam(value = "size", required = false) Integer size) {
        
        return"Hello!";  
    }  
}
