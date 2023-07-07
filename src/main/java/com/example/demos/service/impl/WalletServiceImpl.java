package com.example.demos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demos.entity.Transaction;
import com.example.demos.entity.User;
import com.example.demos.mapper.TransactionMapper;
import com.example.demos.mapper.UserMapper;
import com.example.demos.service.WalletService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;


@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 获取钱包余额
     * @param userId
     * @return
     */
    @Override
    public Double getBalance(Long userId) {
        User user = userMapper.selectById(userId);
        //TODO 这里如果需要可以判断一下存款是否为负
        return user.getBalance();
    }

    /**
     * 消费
     * @param transaction
     * @return
     */
    @Override
    public boolean consume(Transaction transaction) {
        //判断transaction信息完整性
        if(ObjectUtils.isEmpty(transaction.getUserId())){
            return false;
        }
        if(ObjectUtils.isEmpty(transaction.getAmount())){
            return false;
        }
        //是否已经逻辑删除
        if(!ObjectUtils.isEmpty(transaction.getDestroy())){
            return false;
        }
        //金额是否足够消费
        User user = userMapper.selectById(transaction.getUserId());
        if(user.getBalance()< transaction.getAmount()){
            return false;
        }
        return startSpending(transaction, user);
    }

    /**
     * 详细消费过程
     * @param transaction
     * @param user
     * @return
     */
    public boolean startSpending(Transaction transaction, User user) {
        //创建锁对象
        RLock lock = redissonClient.getLock("lock:consume" + transaction.getUserId());
        boolean isLock = lock.tryLock();
        if(!isLock){
            return false;
        }
        try {
            //开始消费
            int insert = transactionMapper.insert(transaction);
            if(insert!=1){
                return false;
            }
            user.setBalance(user.getBalance()-transaction.getAmount());
            int i = userMapper.updateById(user);
            if(i!=1){
                return false;
            }
            return true;
        } finally {
            //释放锁
            lock.unlock();
        }
    }


    /**
     * 退款
     * @param transaction
     * @return
     */
    @Override
    @Transactional
    public boolean refund(Transaction transaction) {
        //判断transaction信息完整性
        if(ObjectUtils.isEmpty(transaction.getUserId())){
            return false;
        }
        if(ObjectUtils.isEmpty(transaction.getAmount())){
            return false;
        }
        //是否已经逻辑删除
        if(!ObjectUtils.isEmpty(transaction.getDestroy())){
            return false;
        }
        return refundStart(transaction);
    }

    /**
     * 订单记录
     * @param userId
     * @return
     */
    @Override
    public List<Transaction> getBalanceList(Long userId) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getUserId,userId);
        return transactionMapper.selectList(wrapper);
    }

    /**
     * 退款详细过程
     * @param transaction
     * @return
     */
    private boolean refundStart(Transaction transaction) {
        //创建锁对象
        RLock lock = redissonClient.getLock("lock:refund" + transaction.getUserId());
        boolean isLockRefund = lock.tryLock();
        if(!isLockRefund){
            return false;
        }
        try {
            //开始退款
            transaction.setDestroy("delete");
            int i = transactionMapper.updateById(transaction);
            if(i!=1){
                return false;
            }
            User user = userMapper.selectById(transaction.getUserId());
            user.setBalance(transaction.getAmount()+ user.getBalance());
            int i1 = userMapper.updateById(user);
            if(i1!=1){
                return false;
            }
            return true;
        } finally {
            //释放锁
            lock.unlock();
        }
    }
}
