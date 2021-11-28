package com.nowcoder.community.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 登录凭证的默认记录时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 登录凭证的长期记录时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
