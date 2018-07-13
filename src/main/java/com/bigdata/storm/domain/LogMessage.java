package com.bigdata.storm.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IDEA by User1071324110@qq.com
 *
 * @author 10713
 * @date 2018/7/12 21:01
 */
@Data
public class LogMessage implements Serializable {
    private int type;//1：浏览日志、2：点击日志、3：搜索日志、4：购买日志
    private String hrefTag;//标签标识
    private String hrefContent;//标签对应的标识，主要针对a标签之后的内容
    private String referrerUrl;//来源网址
    private String requestUrl;//来源网址
    private String clickTime;//点击时间
    private String appName;//浏览器类型
    private String appVersion;//浏览器版本
    private String language;//浏览器语言
    private String platform;//操作系统
    private String screen;//屏幕尺寸
    private String coordinate;//鼠标点击时的坐标
    private String systemId; //产生点击流的系统编号
    private String userName;//用户名称

    public LogMessage(int type, String referrerUrl, String requestUrl, String userName) {
        this.type = type;
        this.referrerUrl = referrerUrl;
        this.requestUrl = requestUrl;
        this.userName = userName;
    }

    public String getCompareFieldValue(String field) {
        if ("hrefTag".equalsIgnoreCase(field)) {
            return hrefTag;
        } else if ("referrerUrl".equalsIgnoreCase(field)) {
            return referrerUrl;
        } else if ("requestUrl".equalsIgnoreCase(field)) {
            return requestUrl;
        } else if ("appName".equalsIgnoreCase(field)) {
            return appName;
        } else if ("platform".equalsIgnoreCase(field)) {
            return platform;
        }
        return "";
    }

}
