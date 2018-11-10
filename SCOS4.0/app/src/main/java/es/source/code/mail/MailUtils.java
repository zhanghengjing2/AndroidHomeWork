package es.source.code.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


//邮件发送工具类
public class MailUtils {
    /**
     * 发送文本类型的邮件
     * @param mail 待发送的mailSender对象
     * @return 是否发送成功
     */
    public static boolean sendTextMail(Mail mail) {

        // 身份认证器类
        MyAuthenticator authenticator = null;
        if (mail.isValidate()) {
            authenticator = new MyAuthenticator(mail.getUsername(),
                    mail.getPassword());
        }

        // 邮件相关配置
        Properties properties = mail.getProperties();
        // 根根配置以及验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(properties, authenticator);

        try {
            // 根据生成的session创建一个待发送的消息
            Message mailMessage = new MimeMessage(sendMailSession);
            Address from = new InternetAddress(mail.getFromAddress());
            // 设置邮件发送者的地址
            mailMessage.setFrom(from);
            Address to = new InternetAddress(mail.getToAddress());
            // 设置邮件接收者的地址
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的标题
            mailMessage.setSubject(mail.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            String mailContent = mail.getContent();
            // 设置发送的正文文本
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送HTML格式的邮件
     * @param mail 待发送的邮件对象
     * @return 是否发送成功
     */
    public static boolean sendHtmlMail(Mail mail) {
        // 身份认证器类
        MyAuthenticator authenticator = null;
        // 如果需要身份认证，则创建一个密码验证器
        if (mail.isValidate()) {
            authenticator = new MyAuthenticator(mail.getUsername(), mail.getPassword());
        }
        // 相关配置
        Properties pro = mail.getProperties();
        // 根据邮件相关配置和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
            // 根据session创建一个待发送的邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            Address from = new InternetAddress(mail.getFromAddress());
            // 设置邮件发送者的地址
            mailMessage.setFrom(from);
            Address to = new InternetAddress(mail.getToAddress());
            // 设置邮件接收者的地址
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的标题
            mailMessage.setSubject(mail.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(mail.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }


}
