package com.wcy.woj.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Description: netty工具类
 */
public class NettyUtil {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    public static AttributeKey<String> COOKIE = AttributeKey.valueOf("cookie");
    public static AttributeKey<Integer> UID = AttributeKey.valueOf("id");

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        Attribute<T> attr = channel.attr(attributeKey);
        attr.set(data);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        return channel.attr(key).get();
    }
}
