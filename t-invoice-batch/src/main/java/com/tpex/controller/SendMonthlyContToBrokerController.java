package com.tpex.controller;

import com.tpex.service.SendMonthlyContToBrokerService;
import com.tpex.util.ConstantUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class SendMonthlyContToBrokerController {

@Autowired
SendMonthlyContToBrokerService sendMonthlyContToBrokerService;
    @PostMapping(path = "/sendMonthlyContToBroker")
    public ResponseEntity<String> sendMonthlyCont(@RequestParam String vanMonth){

        sendMonthlyContToBrokerService.sendMonthlyContToBrokerBatchJob(vanMonth);

    return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
}


}
