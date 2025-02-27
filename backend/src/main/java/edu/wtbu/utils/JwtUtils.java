package edu.wtbu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

public class JwtUtils {

    //常量
    public static final long EXPIRE = 1000 * 60 * 60 * 24; //token过期时间，24小时
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; //秘钥 这是自定义的，可以根据自己的想法写

    //生成token字符串的方法
    public static String getJwtToken(Integer id, String username){

        String JwtToken = Jwts.builder()
                //1.设置JWT头部信息，类型和加密算法
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")

                //2.设置JWT载荷数据
                .setId(UUID.randomUUID().toString()) //设置内置字段jti，token编号，唯一id
                .setSubject("all")  //设置内置字段sub：面向所有用户
                .setIssuedAt(new Date())//设置字段iat：token创建时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE)) //内置字段wxp：token过期时间
                //自定义字段，kv格式，设置token主体部分 ，存储用户信息
                .claim("id", id)
                .claim("username", username)

                //3.设置JWT签名信息(加密算法，密钥)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                //4.生成最终token
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        //非空判断
        // isBlank、isEmpty区别：
        // isBlank判断某字符串是否为空或长度为0或由空白符（whitespace）构成
        // isEmpty判断标准是str==null或str.length()==0
        if(StringUtils.isBlank(jwtToken)) return false;
        try {
            //通过密钥验证签名是否被篡改
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authorization");
            if(StringUtils.isBlank(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token字符串获取id
     * @param request
     * @return
     * HttpServletRequest 由于后端会将token传前端，前端会将token存储在cookie,所以这里传入HttpServletRequest 获取cookie里面的token，然后返回用户id
     */
    public static Claims getMemberByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if(StringUtils.isBlank(jwtToken)) return null;
        //判断token是否被篡改
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        //获取载荷信息
        Claims claims = claimsJws.getBody();
        return claims; //返回token载荷部分，可使用get("名称")获取信息
    }
}

