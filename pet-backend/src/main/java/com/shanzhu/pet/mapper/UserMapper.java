package com.shanzhu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanzhu.pet.controller.dto.UserPasswordDTO;
import com.shanzhu.pet.entity.User;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

}
