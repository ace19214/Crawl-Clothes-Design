package crawl.controller;

import crawl.service.DesignByHumansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class DesignByHumansController {

    @Autowired
    private DesignByHumansService designByHumansService;

    @GetMapping("/crawl")
    public String getAllImage(@RequestParam String path, @RequestParam int from, @RequestParam int to) {
        try {
            designByHumansService.crawlShirt(path, from, to);
            return "Success";
        } catch (Exception e){
            return "Error " + e.getMessage();
        }
    }
}
