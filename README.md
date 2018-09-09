### 「语录」一款基于Bmob后端云的简单社区APP
---
#### 一 创作背景
无论是生活中，还是在书籍、电影、戏剧等艺术作品中，往往存在一闪而过却触动人心的文字段落。永远记得《挪威的森林》里有这样一句话：“死并非生的对立面，而作为生的一部分永存”，它一直警戒着我要以一种平和、不以己悲的心态去度过这一生，不要汲汲于生或汲汲于死。由此可见文字的力量是巨大的。
中学时候，我喜欢准备一本精致的笔记，将那些扣人心弦的文字语录郑重摘抄下来。上了大学便随意了很多，往往是将文字复制在手机上的备忘录或是直接截图保存，这导致有时候想起某个句子，左翻右翻却不得其踪。
恰巧上学期在选修课上接触到了安卓开发，于是这个寒假便寻思着独自开发一款简单的文字社区APP，用户能在此发布、分享、整理自己所感触的那些语录。由于个人技术有限，所以通常是一边学习一边进行开发测试。现在该产品已完成初期阶段的功能目标。因此我将开发过程与功能简介整理成这篇文档。

#### 二 项目简介

##### - 开发介绍及声明
> * 操作系统：Windows 10
> * 开发工具：Android Studio 2.3 & SDK 25
> * 测试设备：Android 7.0 及以上版本的手机
> * 开发时间：2018年2月
> * 本项目创意完全由开发人员原创。素材版权归于原作者。

##### - 技术简单说明
> 这是一款简单的针对文字分享的社区APP。框架基于传统的MVC，界面采用Material Design设计。后台则使用了Bmob后端云进行数据存储。（数据库设置如下）

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/database.jpg)

##### 主要功能
> * 发布并分享语录，可对自己的语录进行重新编辑，对他人的语录进行收藏。
> * 创建特定主题的笔记本，将自己或他人相关的语录添至其中，从而进行归类整理。
> * 「关注」- 随时看到关注的用户的最新语录动态。
> * 「发现」- 参与最新活动，查看热门板块，还有开发者的文章推荐。
> * 「个人」- 编辑信息、查看我的各类内容，以及消息提示。

#### 三 界面功能介绍
##### - 启动界面
Bmob提供了一个专门的用户类——BmobUser来自动处理用户账户管理所需的功能。每当用户注册成功或是第一次登录成功，都会在本地磁盘中有一个缓存的用户对象。
因此，在每次打开APP时，先是欢迎界面，然后通过判断是否存在currentUser对象来决定是直接进入主界面还是登录界面。逻辑流程图如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/logic_1.png)

欢迎界面、注册界面、登录界面的设计如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/1.jpg)

若忘记或更改密码界面，则进入以下界面进行密码重置。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/2.png)

通过邮箱进行重置密码，确保账户的安全性。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/3.png)

##### - 主界面
主界面布局采用TabLayout+ViewPager实现了底部导航，利用setCustomView填充了自定义的tab图标文字样式，同时重写FragmentStatePagerAdapter已达到点击滑动页面的效果。
逻辑图如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/logic_2.png)

界面设计如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/4.jpg)

接下来，我们分别从「关注 发现 个人」这三个模块进行功能展示。先从「个人」谈起。

##### - 「个人」模块

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/5.png)

可以看到，「个人」界面中，上部分是个人信息展示，点击「关注 2」「粉丝 1」进入各自对应的列表，点击item可进入相应的用户主页。

接着是信息列表，点击第一行「账号资料」可进入个人信息详细界面，可选择进行修改。其中的头像和封面设置则是选取手机相册里的图片进行裁剪，将裁剪后的数据流填入图片文件，获得其uri再上传至Bmob后端云。部分界面展示如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/6.jpg)

先谈谈「个人」界面里右下方的悬浮按钮，点击即可进行语录编辑并发布。界面如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/7.jpg)

回到信息列表，点击第二行「我的语录」即可看到自己发表的所有语录，这里提供了两种观看模式，便于用户快速找到某一条特定语录。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/8.jpg)

回到信息列表，点击第三行「我的笔记」即可看到自己创建的所有笔记本。以下是新建笔记本的流程，创建完毕后，你可以在某条相关语录的页面右上方点击“+”号，将其添加进这本笔记，从而起到归类整理的作用。部分页面展示如下：

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/9.jpg)

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/10.jpg)

PS：点击进某条语录或某本笔记的详情页面里，如果系统判断是自己发布/创建的，则显示如下图1，点击右边符号弹出菜单抽屉，可选择重新编辑或者删除；如果是属于别的用户，则显示如下图2、3，其中数字表示被喜欢的数量，点击“爱心”符号表示喜欢。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/11.png)

回到信息列表，点击第四行「我的喜欢」即可看到自己曾表示喜欢的语录和笔记本。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/12.jpg)

回到信息列表，点击第五行「消息提示」进入以下界面，针对每一项，我的实现逻辑是这样的。例如，我在数据表里储存了相应的Integer：“新粉丝-总数”和“新粉丝-已阅”，当A用户关注B用户时，B对应的“新粉丝-总数”都会自增1。当系统判断“新粉丝-总数”不等于“新粉丝-已阅”时，该项Item右边则显示为红点，提示用户点击查看。一旦点击查看，此时“新粉丝-已阅”则会设置为等于“新粉丝-总数”，返回则红点消失。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/13.png)

点击进入，界面展示如下。其中，点击列表的不同控件可进入不同的对应界面。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/14.jpg)

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/15.jpg)

##### - 「关注」模块

在「关注」界面里，可以下拉刷新，即时显示关注用户的语录动态。点击语录即可查看详情。也可通过点击用户头像或昵称，进入用户界面。（点击不同控件都能进入对应的界面）

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/16.jpg)

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/17.jpg)

##### - 「发现」模块

在「发现」界面里，从上而下分别是搜索栏，[热门活动]自动轮播图，热门内容板块，还有最新的三篇文章推送。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/18.png)

点击搜索栏，进入搜索界面，输入不同关键字，选择不同标签，即可查到你的目标。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/19.jpg)

系统在后端云里读取时间最新的三个活动数据，通过ViewPager以轮播图的形式进行呈现，建立子线程每三秒便自动滑动。点进某个活动，阅读活动规则进行#话题#参与。点击查看成功参与的语录，「热门」即是通过“被喜欢数”进行排序，「实时」即是按时间排序。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/20.jpg)

至于热门内容板块，分别为「用户、语录、笔记、文章」。其中「用户」通过粉丝数量进行排序，「语录」和「笔记」通过「被喜欢数量」进行排序，「文章」则显示所有往期文章。这样可方便用户进行查询、浏览。
下方的文章推送，则是我个人选取一些喜欢的短文储存在后端云里，系统读取最新的三篇进行呈现，闲暇时刻即可阅读。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/21.jpg)

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/22.jpg)

另外：APP里呈现的图片，例如语录配图、笔记封面、用户头像等等，都可点击放大，拖曳缩放，还可点击右上角，将图片下载到手机本地文件里并通知手机相册。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/23.png)

#### 四 开发体会与个人感想
首先夸一下Bmob，这是一个很优秀的云储存平台，提供了封装友好的SDK和各类对后台数据进行操作的API。[官网](https://docs.bmob.cn/data/Android/a_faststart/doc/index.html)附有详细的使用教程。真的十分容易上手，而且免费版的各项服务指标也能很好地满足个人开发者。
    
不过在使用过程中也遇到了几个麻烦，比如Bmob不支持多表查询、免费版不支持模糊查询、不可对非当前用户所在据表进行更改操作，最坑的是，Bmob的所有查询操作都属于异步执行，只能另想它法，多绕几步路。

作为一个接触Android开发不久的新手，在完成这个产品的过程中处处踩坑，时常遇到盲点。记得刚开始几天的时候，由于“发布语录”设置了可加或不加配图，所以一直在研究如何让「关注」界面的ListView流畅地加载网络图片，而且要满足其中某些Item是不显示图片的。后来通过巧妙地重写SimpleAdapter的setViewBinder方法以及使用imageLoader框架才解决了这个问题。在开发过程中也接触到了很多之前未使用的控件和布局，在实现导航切换和banner轮播图效果的时候真的成就感满满，但同时又得学会面对很多麻烦，比如：如何解决SwipeRefreshLayout和ViewPager的滑动冲突？如何让ScrollView嵌套ListView和RecyclerView的同时又使它们的高度自适应？诸如此类。现在回想，特别悔恨自己没有记录博客的习惯，不能把当初遇到的那些问题与解决方案详细地写下来。

但还是十分感激于此次短暂的独立开发经历，让我收获颇丰。其实最大的体会便是：思考规划比打代码更重要。当你设计好界面布局、UI交互、数据表的结构和关联性、以及实现思路和步骤时，再动手打代码时便能流畅许多，省去了很多发呆、陷入困惑的时间空隙。

PS：代码已完整上传，不过为了保障后端云数据库的私密性，我将文件中的Application ID删除。

![image](https://github.com/15331016/Bmob_Community_Application/raw/master/screenshots/24.png)



2018/3/9