package com.example.dcloud.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import java.io.IOException;

public class MessageUtil {
    public static void main(String[] args) {
        // 短信应用SDK AppID
        int appid = 1400510593; // 1400开头

        // 短信应用SDK AppKey
        String appkey = "37cfeca8ddf03c54ec70a81985eaae79";
        // 需要发送短信的手机号码
        String[] phoneNumbers = {"13385989351"};

// 短信模板ID，需要在短信应用中申请
        int templateId = 363359; // NOTE: 真实的模板ID需要在短信控制台中申请
        //templateId7839对应的内容是"您的验证码是: {1}"
        try {
            String[] params = {"5678"};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    templateId, params, "", "", "");
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON 解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络 IO 错误
            e.printStackTrace();
        }
    }
}
