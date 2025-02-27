package edu.wtbu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan("edu.wtbu.mapper")
public class DocumentManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentManagementSystemApplication.class, args);
    }

}
