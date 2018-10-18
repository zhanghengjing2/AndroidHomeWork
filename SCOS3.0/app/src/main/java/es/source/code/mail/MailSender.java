package es.source.code.mail;

import android.net.wifi.aware.DiscoverySession;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import es.source.code.Utils.Constant;

//多线程发送邮件
public class MailSender implements Runnable{

    String content;
    int type=0;
    Handler handler;

    public MailSender(String content,Handler handler)
    {
        this.content=content;
        this.handler=handler;
    }

    public MailSender() {

    }

    @Override
    public void run() {
        Mail mailSender = new Mail(
                "smtp.163.com",     //服务器地址
                "25",               //服务器端口
                "zhj490241840@163.com",   //你的邮箱
                "zhj1996520",           //你的邮箱密码或者授权码
                "zhj490241840@163.com",   //你的地址 （一般与你的邮箱相同）
                "490241840@qq.com",    //目的地址
                true,               //是否身份认证
                "邮件",              //邮件标题
                this.content,                //邮件正文
                null);              //附件

        boolean isSuccess = false;
        if (type == 0) {
            isSuccess = MailUtils.sendTextMail(mailSender);// 发送文体格式
        } else if (type == 1) {
            isSuccess = MailUtils.sendHtmlMail(mailSender);// 发送文体格式
        }
        if (isSuccess) {
            Message message = new Message();
            message.what = Constant.EMAIL_SEND_SUCCESS;
            handler.sendMessage(message);
            Log.i("TAG--", "发送成功");
        } else {
            Log.i("TAG--", "发送失败");
        }
    }

}
