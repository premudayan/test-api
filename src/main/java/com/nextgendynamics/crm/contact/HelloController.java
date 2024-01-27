package com.nextgendynamics.crm.contact;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("v1/hello")
public class HelloController {
    //for internationalization of messages
    private MessageSource messageSource;

    public HelloController( MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping("")
    public String sayHello(){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("good.morning.message", null, "Default Message", locale);
    }

}
