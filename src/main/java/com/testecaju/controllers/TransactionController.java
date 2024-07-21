package com.testecaju.controllers;

import com.testecaju.dtos.TransactionDTO;
import com.testecaju.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    @Autowired
    private TransactionService transactionService;

    @PostMapping(path = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> authorize(@RequestBody TransactionDTO transaction) {
        HashMap<String, String> map = new HashMap<>();

        String code = this.transactionService.authorize(transaction);
        map.put("code", code);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMissingRequestBody(HttpMessageNotReadableException exception){
        logger.error("Missing request body!", exception);

        return new ResponseEntity<>(new HashMap<String, String>(){{
            put("code", "07"); // Return default message if the request does not contain a body
        }}, HttpStatus.OK);
    }
}
