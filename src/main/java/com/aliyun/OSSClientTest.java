package com.aliyun;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import com.aliyun.oss.OSSClient;

public class OSSClientTest {

	public static void main(String[] args) throws Exception {

		// endpoint以杭州为例，其它region请按实际情况填写
		String endpoint = "http://oss-cn-qingdao.aliyuncs.com";
		// accessKey请登录https://ak-console.aliyun.com/#/查看
		String accessKeyId = "dA2poxeQFvSergfR";
		String accessKeySecret = "avd92V3NYCA1PVUkyWReVB9mJLrAHA";
		// 创建OSSClient实例
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		// 上传字符串
		InputStream inputStream = new FileInputStream("C:/Users/ChenQiang/Pictures/cy.jpg");
		Object result = ossClient.putObject("chen-qiang", UUID.randomUUID().toString() + ".jpg", inputStream);
		System.out.println(result);
		// 关闭client
		ossClient.shutdown();
	}
}