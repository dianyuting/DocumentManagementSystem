package edu.wtbu.utils;

import java.util.Random;

public class CodeUtils {
    /**
     * 随机生成验证码
     */
    public static Integer generateValidateCode(int length){
        Integer code = null;
        if(length == 4){
            code = new Random().nextInt(9999);
            if(code < 1000){
                code = code + 1000;
            }
        }else if(length == 6){
            code = new Random().nextInt(999999);
            if(code < 100000){
                code = code + 100000;
            }
        }else {
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }
}
