package com.shanzhu.pet.config.interceptor;

import java.lang.annotation.*;

/**
 * 权限注解
 *
 * @author: ShanZhu
 * @date: 2024-01-08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {
}
