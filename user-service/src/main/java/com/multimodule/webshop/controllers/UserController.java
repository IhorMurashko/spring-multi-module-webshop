package com.multimodule.webshop.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @GetMapping("/get")
    public String getUser(@RequestHeader (value = "X-Gateway-Auth", required = false) String gatewayHeader) {
        System.out.println("HEADER X-Gateway-Auth: " + gatewayHeader);


        return "user";
    }

}
