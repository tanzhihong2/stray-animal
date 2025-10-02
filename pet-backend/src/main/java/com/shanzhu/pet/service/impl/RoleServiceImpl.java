package com.shanzhu.pet.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.pet.entity.Menu;
import com.shanzhu.pet.entity.Role;
import com.shanzhu.pet.entity.RoleMenu;
import com.shanzhu.pet.mapper.RoleMapper;
import com.shanzhu.pet.mapper.RoleMenuMapper;
import com.shanzhu.pet.service.IMenuService;
import com.shanzhu.pet.service.IRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        // 先删除当前角色id所有的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);

        // 再把前端传过来的菜单id数组绑定到当前的这个角色id上去
        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);
        for (Integer menuId : menuIds) {
            Menu menu = menuService.getById(menuId);
            if (menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())) { // 二级菜单 并且传过来的menuId数组里面没有它的父级id
                // 那么我们就得补上这个父级id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getPid());
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add(menu.getPid());
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }

}
