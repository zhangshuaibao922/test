package com.example.demos.controller;

import com.example.demos.entity.Transaction;
import com.example.demos.service.WalletService;
import com.example.demos.service.impl.WalletServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * 获取钱包余额
     * @param userId
     * @return
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<Double> getBalance(@PathVariable Long userId) {
        Double balance = walletService.getBalance(userId);
        return balance != null ? ResponseEntity.ok(balance) : ResponseEntity.notFound().build();
    }

    /**
     * 消费
     * @param transaction
     * @return
     */
    @PostMapping("/consume/{userId}")
    public ResponseEntity<String> consume(@RequestBody Transaction transaction) {
        boolean consume = walletService.consume(transaction);
        if(consume){
            return ResponseEntity.ok("consume successful");
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/refund/{userId}")
    public ResponseEntity<String> refund(@RequestBody Transaction transaction) {
        boolean refund = walletService.refund(transaction);
        if(refund){
            return ResponseEntity.ok("refund successful");
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/balanceList/{userId}")
    public ResponseEntity<List<Transaction>> getBalanceList(@PathVariable Long userId) {
        List<Transaction> balanceList = walletService.getBalanceList(userId);
        return balanceList != null ?  ResponseEntity.ok(balanceList): ResponseEntity.notFound().build();
    }

}
