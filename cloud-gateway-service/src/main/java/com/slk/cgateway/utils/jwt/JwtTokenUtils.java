package com.slk.cgateway.utils.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 *
 */
@Component
public class JwtTokenUtils {

    private final JwtProperties jwtProperties;
    private final SignatureVerifier verifier;

    public JwtTokenUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.verifier = new MacSigner(this.jwtProperties.getSecret());
    }

    /**
     * 获取jwt对象，如果解析失败直接抛出异常
     * @param token
     * @return
     */
    public Jwt getJWT(String token) throws Exception{
        return JwtHelper.decodeAndVerify(token, verifier);
    }

    public Object getSpecifyField(String token,String field) throws Exception{
        Jwt jwt = this.getJWT(token);
        Map content = JSON.parseObject(jwt.getClaims(),Map.class);
        return content.get(field);
    }


}
