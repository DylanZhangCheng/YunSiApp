package com.lehua.tablet0306.okhttp;

public class URLConstant {

    // 注册信息Url
    public static final String URL_REGISTER = "one/user/register.template";
    public static final String URL_REGISTER_QUESTION = "one/user/detailMsg.template";
    //登陆验证表
    public static final String URL_LOGIN = "one/user/login.template";
    //获取用户调查表
    public static final String URL_INFO = "one/user/personalMsg.template";
    //获取用户头像、上传头像
    public static final String URL_USER_IMG = "one/user/userPhoto.template\n";

    public static final String URL_PUSH = "one/push/push.template";    //查看后台有没有新的推送消息
    public static final String URL_PUSH_DETAIL = "one/push/detail.template";   // 查看某条推送消息详情
    public static final String URL_PUSH_HISTORY = "one/push/history.template";  //给某用户推送过的历史消息
    public static final String URL_FAVORITE = "one/push/favorite.template"; // 用户id + 收藏列表Id
    public static final String URL_FAVORITE_HISTORY = "one/push/favoritelist.template"; //用户收藏过的所有信息
    public static final String URL_DELETE_HISTORY = "one/push/deletehistory.template";  //用户删除过的信息
}
