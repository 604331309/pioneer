package com.slk.cgateway.utils.jwt;

import lombok.Data;

import java.util.List;

/**
 * Payload 示例：
 * {
 *   "exp": 1569256113,
 *   "user_name": "15123114227",
 *   "authorities": [
 *     "ROLE_USER"
 *   ],
 *   "jti": "f8346bdf-f4e6-408e-b3cb-98ce1aa811d7",
 *   "client_id": "6d2eb2c236fa4e8da541a6356c28768c",
 *   "scope": [
 *     "ALL"
 *   ]
 * }
 * @author chunliucq
 * @since 23/09/2019 17:01
 */
@Data
public class Payload {
    private Long exp;
    private String user_name;
    private List<String> authorities;
    private String jti;
    private String client_id;
    private List<String> scope;
}
