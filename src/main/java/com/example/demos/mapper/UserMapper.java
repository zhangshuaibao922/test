package com.example.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demos.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
