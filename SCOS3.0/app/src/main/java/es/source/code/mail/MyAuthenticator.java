package es.source.code.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


//密码认证器类：
public class MyAuthenticator extends Authenticator {
    String username = null;
    String password = null;

    public MyAuthenticator() {}

    public MyAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}

