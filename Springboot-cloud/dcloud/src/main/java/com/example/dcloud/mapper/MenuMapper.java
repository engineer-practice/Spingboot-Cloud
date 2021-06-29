package com.example.dcloud.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.dcloud.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    List<Map<String,Object>> getMenuTree();
}
