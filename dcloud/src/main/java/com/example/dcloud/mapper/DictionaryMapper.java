package com.example.dcloud.mapper;

import com.example.dcloud.entity.Dictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

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
public interface DictionaryMapper extends BaseMapper<Dictionary> {
    @Select("SELECT COUNT(*) FROM dictionary where isDelete = 0")
    int selDictNum();  //查询用户数

    @Select("SELECT * FROM dictionary WHERE isDelete = 0 LIMIT #{offset},10")
    List<Map> selectDictionary(int offset);  //查询用户列表
}
