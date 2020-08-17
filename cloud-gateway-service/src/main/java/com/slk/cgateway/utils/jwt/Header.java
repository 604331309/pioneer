package com.slk.cgateway.utils.jwt;

import lombok.Data;

/**
 * @author chunliucq
 * @since 23/09/2019 16:57
 */
@Data
public class Header {
    private String alg;
    private String typ;
}
