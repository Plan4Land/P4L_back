package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PayController {
    private final PaymentService paymentService;



}
