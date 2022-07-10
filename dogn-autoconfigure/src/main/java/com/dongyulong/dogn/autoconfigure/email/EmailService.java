package com.dongyulong.dogn.autoconfigure.email;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/97:14 上午
 * @since v1.0
 */
public class EmailService {
    private JavaMailSender sender;
    private String mailEncoding;
    private String fromEmail;

    public JavaMailSender getSender() {
        return sender;
    }

    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void sendMessage(String subject, String[] toEmails, String content, File[] attachments) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mailMessage = sender.createMimeMessage();
        //设置utf-8编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, mailEncoding);
        //接受者
        messageHelper.setTo(toEmails);
        //发送者
        messageHelper.setFrom(fromEmail);
        //主题
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        if (null != attachments) {
            for (File file : attachments) {
                messageHelper.addAttachment(MimeUtility.encodeWord(file.getName()), file);
            }
        }
        sender.send(mailMessage);
    }

    public static void main(String[] args) throws Exception {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setDefaultEncoding("UTF-8");
        sender.setHost("smtp.exmail.qq.com");
        sender.setPort(25);
        sender.setUsername("report");
        sender.setPassword("pinche0901");
//		sender.setUsername("hongshuiqiao");
//		sender.setPassword("0p;/(OL>");
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "300000");
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        javaMailProperties.setProperty("mail.smtp.ssl.trust", "smtp.exmail.qq.com");
        javaMailProperties.setProperty("mail.smtp.socketFactory.fallback", "false");
        sender.setJavaMailProperties(javaMailProperties);

        EmailService service = new EmailService();
        service.setSender(sender);
        service.setFromEmail("report@didapinche.com");
//		service.setFromEmail("hongshuiqiao@didapinche.com");

        service.sendMessage("test", new String[]{"hongshuiqiao@didapinche.com"}, "aaaaaaaaa", null);
    }
}
