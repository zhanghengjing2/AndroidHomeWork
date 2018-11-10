package es.source.code.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.source.code.Utils.CommonUtils;
import es.source.code.Utils.Constant;
import es.source.code.model.LoginParam;
import es.source.code.model.ResultJson;
import es.source.code.model.User;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static cn.bmob.v3.b.From.e;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{

    private Button btn_login ,btn_back,register;
    private EditText username,password;
    private Drawable ic_error;
    private MyApplication myApplication;
    boolean isFit=false;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor ;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        //初始化部件
        InitView();
        myApplication=(MyApplication) getApplication();

        //检查账号密码

        Boolean flag=sp.getBoolean("is_remember",false);
        if(!flag)
        {
            //Toast.makeText(getApplicationContext(), "首次登录!", Toast.LENGTH_SHORT).show();
            btn_login.setVisibility(View.GONE);
            //监听退出按钮
            btn_back.setOnClickListener(this);
            //监听注册按钮
            register.setOnClickListener(this);
        }
        else if(flag) {

            username.setText(sp.getString("userName",""));

            //监听登录按钮
            btn_login.setOnClickListener(this);
            //监听退出按钮
            btn_back.setOnClickListener(this);
            register.setVisibility(View.GONE);
            //监听注册按钮
            //register.setOnClickListener(this);
        }

    }
    //检查账号密码
    private void CheckNamePwd() {
        String regEx = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        String regPa ="^[a-zA-Z0-9]{6,16}$";

        String userName = username.getText().toString();
        String userPwd = password.getText().toString();

        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userName);
        boolean a = matcher.matches();

        Pattern patternpw = Pattern.compile(regPa);
        Matcher matcherpw = patternpw.matcher(userPwd);
        boolean b= matcherpw.matches();

        if(a&&b)
        {
            isFit= true;
        }
        else if(!a)
        {
            username.setError("输入内容不符合规则");
        }
        else if(!b)
        {
            password.setError("输入内容不符合规则");
        }
    }

    //初始化部件
    private void InitView() {
        btn_login = (Button)findViewById(R.id.btn_login_succ);
        btn_back = (Button)findViewById(R.id.btn_login_return);
        register = (Button)findViewById(R.id.btn_register);
        username=(EditText)findViewById(R.id.login_userName);
        password=(EditText)findViewById(R.id.login_password);
        ic_error=getResources().getDrawable(R.drawable.ic_error_red);
        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        editor= sp.edit();//获取编辑器
    }

    @SuppressLint("CheckResult")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login_succ:
                CheckNamePwd();
                if(isFit)
                {
                    ShowProgressDialog("正在登录");
                    final String userName = username.getText().toString();
                    final String userPwd = password.getText().toString();
                    Observable.create(new ObservableOnSubscribe<ResultJson>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<ResultJson> e) throws Exception {
                            String resultString = CommonUtils.requestPost(new LoginParam(userName, userPwd), Constant.URL
                                    .LOGIN);
                            Log.i("resultString",resultString);
                            ResultJson resultJson = new Gson().fromJson(resultString, ResultJson.class);
                            Log.i("resultJson", resultJson.getMsg());
                            e.onNext(resultJson);
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResultJson>() {
                        @Override
                        public void accept(ResultJson result) throws Exception {

                            CloseProgressDialog();
                            if(result==null)
                                Toast.makeText(getApplicationContext(), "登录失败!", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getApplicationContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                                if(result.getRESULTCODE()==1)
                                {
                                    //登录成功
                                    //创建User对象
                                    final User loginUser = new User();

                                    //传入账号和密码
                                    loginUser.setUserName(username.getText().toString());
                                    loginUser.setPassword(password.getText().toString());
                                    loginUser.setOldUser(true);


                                    //存储键值对
                                    editor.putString("userName", username.getText().toString().trim());
                                    editor.putString("password", password.getText().toString().trim());
                                    editor.putBoolean("is_remember",true);
                                    editor.putInt("loginState",1);
                                    editor.putInt("user_type",1);//1表示老用户登录
                                    //提交
                                    editor.commit();//提交修改

                                    Intent intent = new Intent();
                                    //传值
                                    intent.putExtra("id","LoginSuccess");
                                    //传递对象
                                    intent.putExtra("login_userId",loginUser);
                                    myApplication.setLoginUser(loginUser);
                                    intent.setClass(LoginOrRegister.this,MainScreen.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                } else
                {
                    CloseProgressDialog();
                    Toast.makeText(getApplicationContext(), "登录失败!", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.btn_login_return:
                //Toast.makeText(LoginOrRegister.this, "返回",Toast.LENGTH_SHORT).show();
                if(sp.getBoolean("is_remember",false)){
                    //获取editor对象
                    SharedPreferences.Editor editor = sp.edit();//获取编辑器
                    //存储键值对
                    editor.putInt("loginState",0);
                    editor.putBoolean("is_remember",false);
                    //提交
                    editor.commit();//提交修改
                }
                Intent intent = new Intent();
                intent.putExtra("id","Return");
                intent.setClass(LoginOrRegister.this,MainScreen.class);
                startActivity(intent);
                break;

            case R.id.btn_register:
                CheckNamePwd();
                if(isFit)
                {
                    //创建User对象
                    User loginUser = new User();

                    //传入账号和密码
                    loginUser.setUserName(username.getText().toString());
                    loginUser.setPassword(password.getText().toString());
                    loginUser.setOldUser(false);

                    //Android-存储：SharedPreferences使用及其存储类型,https://blog.csdn.net/sinat_31057219/article/details/73477331
                    //获取editor对象
                    SharedPreferences.Editor editor = sp.edit();//获取编辑器
                    //存储键值对
                    editor.putString("userName", username.getText().toString().trim());
                    editor.putString("password", password.getText().toString().trim());
                    editor.putBoolean("is_remember",true);
                    editor.putInt("loginState",1);
                    editor.putInt("user_type",2);//2表示新用户注册
                    //提交
                    editor.commit();//提交修改

                    Intent intent2 = new Intent();
                    //传值
                    intent2.putExtra("id","RegisterSuccess");
                    //传递对象
                    intent2.putExtra("register_userId",loginUser);
                    intent2.setClass(LoginOrRegister.this,MainScreen.class);
                    startActivity(intent2);
                }
        }
    }


    protected void ShowProgressDialog(String content)
    {
        pd = new ProgressDialog(LoginOrRegister.this);
        pd.setTitle("请稍等");
        //设置对话进度条样式为水平
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置提示信息
        pd.setMessage("正在玩命登录中......");
        //设置对话进度条显示在屏幕中央
        pd.getWindow().setGravity(Gravity.CENTER);
        pd.show();//调用show方法显示进度条对话框
    }

    protected void CloseProgressDialog()
    {
        if(pd.isShowing())
            pd.dismiss();
    }
}
