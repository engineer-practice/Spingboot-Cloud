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

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fifteen
 * @since 2020-04-01
 */
@Mapper
@Repository
public interface MenuMapper extends BaseMapper<Menu> {
//    @Override
//    List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<Menu> wrapper);
//
//    @Override
//    Menu selectById(Serializable serializable);

    List<Map<String,Object>> getMenuTree();
}
