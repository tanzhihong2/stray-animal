package com.shanzhu.pet.exception;


/**
 * 自定义业务异常
 *
 * @author: ShanZhu
 * @date: 2024-01-08
 */
public class BizException extends RuntimeException {

    private String code;

    public BizException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
