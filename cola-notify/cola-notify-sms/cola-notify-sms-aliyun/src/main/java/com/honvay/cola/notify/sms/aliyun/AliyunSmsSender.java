package com.honvay.cola.notify.sms.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import com.honvay.cola.notify.sms.api.sender.SmsParameter;
import com.honvay.cola.notify.sms.api.sender.SmsSendResult;
import com.honvay.cola.notify.sms.api.sender.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 阿里云短信发送器
 *
 * @author LIQIU
 * @date 2017-12-20
 */
@Slf4j
public class AliyunSmsSender implements SmsSender {

	private String accessKeyId;

	private String accessKeySecret;

	public AliyunSmsSender() {
		log.info("初始化阿里云接口:" + this);
	}

	@Override
	public SmsSendResult send(SmsParameter parameter) {

		SmsSendResult result = new SmsSendResult();
		result.setSuccess(true);

		// 短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";
		// 短信API产品域名（接口地址固定，无需修改）

		// 替换成你的AK
		// 初始化ascClient,暂时不支持多region（请勿修改）
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", domain);

		IAcsClient acsClient = new DefaultAcsClient(profile);
		// 组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		// 使用post提交
		// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		request.setPhoneNumbers(StringUtils.join(parameter.getPhoneNumbers(), ","));
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName(parameter.getSignName());
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(parameter.getTemplateCode());
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${credential}"时,此处的值为
		// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
		
		 Gson gson = new Gson();
		 
		if (parameter.getParams() != null) {
			request.setTemplateParam(gson.toJson(parameter.getParams()));
		}
		//request.setOutId("yourOutId");

		// 请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = acsClient.getAcsResponse(request);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setCode("SEND_SMS_FAILURE");
			throw new RuntimeException("发送短信发生错误：" + e);
		}
		String okStatus = "OK";
		if (sendSmsResponse.getCode() == null || !okStatus.equals(sendSmsResponse.getCode())) {
			log.error("发送短信失败：" + sendSmsResponse.getMessage());
			result.setSuccess(false);
			result.setCode(sendSmsResponse.getCode());
			return result;
		}
		log.info("发送短信成功：" + sendSmsResponse.getCode());
		return result;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

}
