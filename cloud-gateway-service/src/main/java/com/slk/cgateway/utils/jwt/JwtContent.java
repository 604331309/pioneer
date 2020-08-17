package com.slk.cgateway.utils.jwt;

import lombok.Data;

/**
 * Jwt信息
 * @author chunliucq
 * @since 23/09/2019 16:57
 */
@Data
public class JwtContent {
    private Header header;
    private Payload payload;
    private String signature;
}
