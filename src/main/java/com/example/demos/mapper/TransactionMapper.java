package com.example.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demos.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}
