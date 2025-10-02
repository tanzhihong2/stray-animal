package com.shanzhu.pet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色菜单
 *
 * @author: ShanZhu
 * @date: 2024-01-08
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu {

    private Integer roleId;
    private Integer menuId;

}
