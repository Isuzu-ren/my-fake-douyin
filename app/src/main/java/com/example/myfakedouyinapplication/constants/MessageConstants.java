package com.example.myfakedouyinapplication.constants;

public class MessageConstants {
    // ========== 用户数据加载消息 ==========
    public static final int MSG_LOAD_USERS_SUCCESS = 1001;      // 加载用户成功
    public static final int MSG_LOAD_USERS_FAILED = 1002;       // 加载用户失败
    public static final int MSG_LOAD_MORE_USERS = 1003;         // 加载更多用户
    public static final int MSG_REFRESH_USERS = 1004;           // 刷新用户数据
    public static final int MSG_USERS_PAGE_LOADED = 1005;       // 分页数据加载完成

    // ========== 用户操作消息 ==========
    public static final int MSG_FOLLOW_USER = 2001;             // 关注用户
    public static final int MSG_UNFOLLOW_USER = 2002;           // 取消关注用户
    public static final int MSG_UPDATE_USER = 2003;             // 更新用户信息
    public static final int MSG_SET_USER_NOTE = 2004;            // 设置用户备注
    public static final int MSG_TOGGLE_SPECIAL = 2005;          // 切换特别关注状态

    // ========== 操作结果消息 ==========
    public static final int MSG_FOLLOW_USER_SUCCESS = 2101;     // 关注成功
    public static final int MSG_UNFOLLOW_USER_SUCCESS = 2102;    // 取消关注成功
    public static final int MSG_UPDATE_USER_SUCCESS = 2103;     // 更新成功
    public static final int MSG_UPDATE_FOLLOW_COUNT = 2104;     // 更新关注数成功
    public static final int MSG_SET_USER_NOTE_SUCCESS = 2105;      // 设置备注成功
    public static final int MSG_TOGGLE_SPECIAL_SUCCESS = 2106;  // 切换特别关注成功
    public static final int MSG_OPERATION_SUCCESS = 2107;   // 通用操作成功

    // ========== 分页控制消息 ==========
    public static final int MSG_HAS_MORE_DATA = 3001;           // 还有更多数据
    public static final int MSG_NO_MORE_DATA = 3002;             // 没有更多数据
    public static final int MSG_RESET_PAGINATION = 3003;        // 重置分页状态

    // ========== 错误处理消息 ==========
    public static final int MSG_NETWORK_ERROR = 4001;            // 网络错误
    public static final int MSG_DATA_ERROR = 4002;               // 数据错误
    public static final int MSG_OPERATION_FAILED = 4003;        // 操作失败

    // ========== UI状态消息 ==========
    public static final int MSG_SHOW_LOADING = 5001;             // 显示加载中
    public static final int MSG_HIDE_LOADING = 5002;             // 隐藏加载中
    public static final int MSG_UPDATE_UI = 5003;                // 更新UI

    // ========== 分页参数 ==========
    public static final int PAGE_SIZE = 10;                      // 每页数据量

    // 私有构造函数，防止实例化
    private MessageConstants() {
        throw new UnsupportedOperationException("这是一个常量类，不能被实例化");
    }
}
