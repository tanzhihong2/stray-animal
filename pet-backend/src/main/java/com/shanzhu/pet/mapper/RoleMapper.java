package com.shanzhu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanzhu.pet.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from sys_role where flag = #{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}
