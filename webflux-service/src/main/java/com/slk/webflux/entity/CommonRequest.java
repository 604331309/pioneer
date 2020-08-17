package com.slk.webflux.entity;

import lombok.Data;

/**
 * @author lshao
 * 2020/6/29
 */
@Data
public class CommonRequest<T> {
    private T data;
    private String code;

}
