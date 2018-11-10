package es.source.code.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import es.source.code.model.Dish;
import es.source.code.model.Param;
import es.source.code.model.ResultXml;

public class CommonUtils {

    //从jSon读数据
    public static String getJson(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
                //Log.d("AAA", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    //发送Post请求，得到Json数据
    public static <T extends Param> String requestPost(T param, String urlString) {
        try {
            String postUrl = Constant.URL.BASE + urlString;

            // 构造参数json字符串
            String paramString = new Gson().toJson(param);
            // 请求的参数转换为byte数组
            byte[] postData = paramString.getBytes("utf8");

            // 新建一个URL对象
            URL url = new URL(postUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConnection.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConnection.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConnection.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConnection.setDoInput(true);
            // Post请求不能使用缓存
            urlConnection.setUseCaches(false);
            // 设置为Post请求
            urlConnection.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConnection.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConnection.setRequestProperty("Content-Type", "application/json");
            // 开始连接
            urlConnection.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
            dos.write(postData);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConnection.getResponseCode() == 200) {
                // 获取返回的数据
                return streamToString(urlConnection.getInputStream());
            } else {
                Log.d("requestPost", "请求失败");
            }
            // 关闭连接
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.e("requestPost", e.toString());
        }
        return null;
    }

    //发送Get请求，得到Json数据
    public static InputStream requestGet(String param, String urlString, String contentType) {
        try {
            String postUrl = Constant.URL.BASE + urlString;

            // 新建一个URL对象
            URL url = new URL(postUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
//            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Content-Type", contentType);
//            urlConn.setRequestProperty("charset", "utf-8");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 统计Xml输入流长度
                if( contentType.startsWith("t")) {
                    Log.d("requestGet", "Xml test，urlConn.getContentLength() = " + urlConn.getContentLength());
                }
                // 返回输入流以便于分情况处理
                return urlConn.getInputStream();
            } else {
                Log.d("requestGet", "Get方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            Log.e("requestGet", e.toString());
        }
        return null;
    }


    //网络流转String
    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();
            is.close();
            Log.d("streamToString", "Json test outputStream.size() = " + outputStream.size());
            byte[] byteArray = outputStream.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e("streamToString", e.toString());
            return null;
        }
    }

    //解析Xml
    public static ResultXml getResultFromXml(Document document) {
        ResultXml resultXml = new ResultXml();

        Element result = document.getDocumentElement();

        // 获取该节点下面的所有子节点
        NodeList resultChildNodes = result.getChildNodes();
        Log.d("解析xml", "resultChildNodes.getLength() = " + resultChildNodes.getLength());
        //把节点转换成元素节点
        Element resultCodeElement = (Element) resultChildNodes.item(0);
        Element messageElement = (Element) resultChildNodes.item(1);
        resultXml.setRESULTCODE(Integer.valueOf(resultCodeElement.getTextContent()));
        resultXml.setMsg(messageElement.getTextContent());

        // 计时开始
        Date startDate = new Date(System.currentTimeMillis());
        // 开始解析列表
        Element dataStringElement = (Element) resultChildNodes.item(2);
        NodeList foodElemList = dataStringElement.getElementsByTagName(Constant.ELEMENT_ID.FOOD);
        List<Dish> foodList = new ArrayList<>();
        // 遍历food结点
        for (int i = 0; i < foodElemList.getLength(); i++) {
            Element foodElement = (Element) foodElemList.item(i);
            // 遍历food内容
            NodeList foodParams = foodElement.getChildNodes();
            Dish food = new Dish();
            for (int j = 0; j < foodParams.getLength(); j++) {
                Element param = (Element) foodParams.item(j);
                switch (param.getTagName()) {
                    case Constant.ELEMENT_ID.FOOD_NAME:
                        food.setName(param.getTextContent());
                        break;
                    case Constant.ELEMENT_ID.PRICE:
                        food.setPrice(Integer.valueOf(param.getTextContent()));
                        break;
                    case Constant.ELEMENT_ID.STORE:
                        food.setDishcount(Integer.valueOf(param.getTextContent()));
                        break;
                    case Constant.ELEMENT_ID.ORDER:
                        food.setOrder(Boolean.valueOf(param.getTextContent()));
                        break;
                    case Constant.ELEMENT_ID.IMGID:
                        food.setImageurl(Integer.valueOf(param.getTextContent()));
                        break;
                }
            }
            foodList.add(food);
        }
        Date endDate = new Date(System.currentTimeMillis());
        long duration = endDate.getTime() - startDate.getTime();
        Log.d("解析xml", "Xml test parse time = " + String.valueOf(duration) + "ms , size = " + String
                .valueOf(foodList.size()));

        resultXml.setDataList(foodList);
        return resultXml;
    }

    //从网络获取的输入流转化为Xml的Document
    public static Document streamToXml(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }


}
