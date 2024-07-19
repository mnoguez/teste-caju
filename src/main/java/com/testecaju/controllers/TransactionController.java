package com.testecaju.controllers;

import com.testecaju.dtos.TransactionDTO;
import com.testecaju.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping(path = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> authorize(@RequestBody TransactionDTO transaction){
        HashMap<String, String> map = new HashMap<>();

        String code = this.transactionService.authorize(transaction);
        map.put("code", code);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
