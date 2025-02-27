package edu.wtbu.controller;

import edu.wtbu.entity.Result;
import edu.wtbu.interceptor.PassToken;
import edu.wtbu.utils.CodeUtils;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequestMapping("email")
@RestController
public class EmailController {
    @Resource
    private JavaMailSenderImpl javaMailSender;
    @Value("${spring.mail.username}")
    private String sendMailer;
    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("{receive}")
    @PassToken
    public Result sendEmail(@PathVariable String receive) {
        try {
            System.out.println(sendMailer);
            //true设置支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            //发信人
            mimeMessageHelper.setFrom(sendMailer);
            //收信人
            mimeMessageHelper.setTo(receive);
            //主题
            mimeMessageHelper.setSubject("胖达文档");
            //内容
            if (redisTemplate.hasKey(receive)) {
                redisTemplate.delete(receive);
            }
            Integer code = CodeUtils.generateValidateCode(4);
            String str = "您好，感谢使用胖达文档系统，您的验证码为：" + code +
                    "。请在5分钟内使用该验证码进行操作。如非本人操作，请忽略此邮件";
            mimeMessageHelper.setText(str);
            mimeMessageHelper.setSentDate(new Date());
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            redisTemplate.opsForValue().set(receive, code);
            redisTemplate.opsForValue().set(receive, code, 5 * 60, TimeUnit.SECONDS);
            System.out.println("发送成功");
            return new Result("success", "发送成功");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("发送失败");
            return new Result("fail", "发送失败");
        }
    }

}
