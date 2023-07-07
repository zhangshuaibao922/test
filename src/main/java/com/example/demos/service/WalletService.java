package com.example.demos.service;

import com.example.demos.entity.Transaction;

import java.util.List;

/**
 * @author cardo
 * @Version 1.0
 * @Description TODO
 * @date 2023/7/7 11:02
 */
public interface WalletService {
    /**
     * 获取钱包余额
     * @param userId
     * @return
     */
    Double getBalance(Long userId);

    /**
     * 消费
     * @param transaction
     */
    boolean consume(Transaction transaction);

    /**
     * 退款
     * @param transaction
     * @return
     */
    boolean refund(Transaction transaction);

    /**
     * 详细消费信息list
     * @param userId
     * @return
     */

    List<Transaction> getBalanceList(Long userId);
}
