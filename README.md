# 我的抖音应用 (My Fake Douyin Application)

一个仿抖音关注功能的 Android 客户端应用，实现了用户关注管理、滑动视图、本地数据存储等核心功能。

## ✨ 功能特性

### 核心功能
- **四标签滑动视图**：使用 ViewPager2 + TabLayout 实现流畅的页面切换
- **用户关注管理**：关注/取消关注功能
- **本地数据存储**：使用 SQLite 数据库持久化用户数据
- **下拉刷新**：支持下拉刷新关注列表

### 用户界面
- **圆形头像**：用户头像显示
- **底部弹窗**：用户操作界面
- **自定义指示器**：优化的 TabLayout 指示器样式

### 交互功能
- **特别关注**：滑动开关设置特别关注状态
- **用户备注**：支持为关注用户设置备注

## 🛠 技术栈

### 开发环境
- **开发工具**: Android Studio
- **编程语言**: Java

### 主要技术
- **页面管理**: ViewPager2 + Fragment
- **数据存储**: SQLite
- **列表显示**: RecyclerView + Custom Adapter
- **异步处理**: AsyncTask

## 📁 项目结构

```
app/
├── src/main/java/com/example/myfakedouyinapplication/
│   ├── MainActivity.java           # 主活动，管理标签页
│   ├── fragments/                  # 页面类
│   │   ├── Fragment1.java          # 关注页面
│   │   ├── Fragment2.java          # 推荐页面
│   │   ├── Fragment3.java          # 发现页面
│   │   └── Fragment4.java          # 我的页面
│   ├── adapters/                   # 适配器类
│   │   ├── ViewPagerAdapter.java   # 页面适配器
│   │   └── UserAdapter.java        # 用户列表适配器
│   ├── models/                     # 数据模型
│   │   ├── User.java               # 用户实体类
│   │   └── UserDataManager.java    # 数据管理器
│   ├── database/                   # 数据库相关
│   │   ├── UserDatabaseHelper.java # 数据库帮助类
│   │   └── UserDao.java            # 数据访问对象
│   ├── repository/                 # 数据仓库
│   │   └── UserRepository.java     # 用户数据仓库
│   ├── dialogs/                    # 对话框类
│   │   └── UserActionsDialog.java  # 用户操作弹窗
├── res/
│   ├── layout/                     # 布局文件
│   │   ├── activity_main.xml       # 主活动布局
│   │   ├── fragment_follow.xml     # 关注页面布局
│   │   ├── fragment_layout.xml     # 其他页面布局
│   │   ├── item_user.xml           # 用户列表项布局
│   │   └── dialog_user_actions.xml # 操作弹窗布局
│   ├── drawable/                   # 图片和形状资源
│   ├── values/                     # 数值资源
│   └── ...
└── ...
```

## 🚀 安装与运行

### 环境要求
- Android Studio
- Android SDK 24 或更高版本
- Java 8 或更高版本

### 构建步骤
1. **克隆项目**
   ```bash
   git clone [项目地址]
   cd myfakedouyinapplication
   ```

2. **打开项目**
   - 使用 Android Studio 打开项目文件夹
   - 等待 Gradle 同步完成

3. **配置设备**
   - 连接 Android 设备或启动模拟器
   - 确保设备 API 级别 >= 24

4. **构建运行**
   - 点击 Android Studio 的 "Run" 按钮
   - 或使用命令行: `./gradlew installDebug`

## 📖 使用说明

### 主界面操作
- **标签切换**: 点击顶部标签或左右滑动切换页面
- **关注页面**: 查看已关注的用户列表
- **返回功能**: 点击左上角箭头返回（暂不支持）

### 用户管理
1. **查看用户**: 点击用户项查看详情
2. **取消关注**: 点击"已关注"按钮切换关注状态
3. **更多操作**: 点击右侧更多按钮打开操作面板

### 操作弹窗功能
- **特别关注**: 滑动开关设置特别关注
- **设置备注**: 点击按钮输入用户备注
- **取消关注**: 点击底部红色按钮取消关注

### 数据管理
- **下拉刷新**: 下拉列表刷新数据并清理已取消关注的用户
- **数据持久化**: 所有操作自动保存到本地数据库

## 🐛 常见问题

### Q: 头像显示异常
A: 清理项目 (Build → Clean Project) 并重新构建。

### Q: 数据库操作失败
A: 卸载应用重新安装，或检查存储权限。

### Q: 下拉刷新无效果
A: 确保网络连接正常，检查下拉刷新监听器是否正确设置。
