package edu.wtbu.entity;

import lombok.Data;

@Data
public class Result {
    String flag;
    Object data;


    public Result() {
    }

    public Result(String flag) {
        this.flag = flag;
    }
    public Result(String flag, Object data) {
        this.flag = flag;
        this.data = data;
    }
}
