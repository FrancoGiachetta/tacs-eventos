package tacs.eventos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirect {

    @GetMapping("/swagger")
    public String redirect() {
        return "redirect:/swagger-ui/index.html";
    }
}
