package com.example.demos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transactions")
public class Transaction {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Double amount;
    private LocalDateTime timestamp;
    private String destroy;
    // getters and setters
}
