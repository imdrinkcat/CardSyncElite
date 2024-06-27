# CardSyncElite
SD卡拷卡工具，支持多种备份方案、分类备份。

## 开发环境
开发环境：IntelliJ 2023.2 + JDK 17, SQLite 3.32

## 前言：为何编写该软件
我是一名摄影爱好者，平时会拍摄很多照片，也会进行视频的拍摄。每次拍摄结束后，对素材的管理是一个很麻烦的事情。拍摄完的文件很大，进行拷贝、备份等操作十分耗时。在文件的同步过程中很容易出错，例如硬盘掉线、网络波动导致的NAS网络存储离线等，均会导致复制过程出现问题。因此，我希望开发一套素材管理软件，对拍摄素材的前期同步处理、后期的归档备份等进行全流程的管理。

## 功能说明：
本程序是一个相机SD卡的拷卡软件，可以使照片、视频文件的拷贝流程化。可以将同一来源的文件经过分类，同步至多个目的地。能够简化照片、视频资料的同步流程。
软件可以自动校验完成同步的数据，并修复同步错误的部分。根据需要，可以自行设置同步时启用的线程数量、是否开启校验、拷贝的文件类型等。
![image](https://github.com/imdrinkcat/CardSyncElite/assets/139839534/ba215e8a-b1ef-44d2-af57-5de1b484a39d)

一项拷贝任务对应多种同步规则，同一来源的文件可以同步至多个目标目录中。主界面实现了任务和同步规则的增删查改操作，可以一键启动同步流程。
![image](https://github.com/imdrinkcat/CardSyncElite/assets/139839534/e15b7500-ce44-4375-9c31-7578713b493a)
![image](https://github.com/imdrinkcat/CardSyncElite/assets/139839534/cf20db55-2b52-4d22-8635-90e590202f2a)
![image](https://github.com/imdrinkcat/CardSyncElite/assets/139839534/87fbffea-468f-4f1f-8ea6-219fa4d7e6a7)

本软件具有文件校验功能，对于同步有误的文件，可以自动重新复制，进行修复。

## 面向对象设计
程序内引用了一个jar包：sqlite-jdbc-3.32.3.2.jar
本程序共设计了13个类，被划分至如下几个包中：
1. 核心功能类 	com.drinkcat.cardsyncelite.core
- CoreSyncEngine		文件搜索类
- CoreCheckEngine	文件校验类
- CoreCopyEngine		文件复制类
- CoreSyncEngine		文件同步类，负责调用上述三个类，进行文件同步操作
2. 控制器类	com.drinkcat.cardsyncelite.controller
- MainController		主窗体控制器类
- RuleCardController	同步规则控制器类
- TaskCardController	同步任务控制器类
3. 模型类		com.drinkcat.cardsyncelite.module
- SyncTask			同步任务类
- SyncRule			同步规则类
4. 工具类		com.drinkcat.cardsyncelite.util
- DataStoreUtil			数据库操作类，负责与数据库进行通信
- ExtensionUtil			拓展名类，存储不同类别文件的拓展名
以及MainApplication类，和入口类Main。

## 改进方向
本代码是作者写的第一个Java项目，不免漏洞百出。

1. 重构代码：很多功能的实现方式没有进行很深入的思考，导致面向对象设计不是十分合理。目前代码的耦合度过高，重新梳理不同功能之间的关系，然后重构代码是一个比较好的选择。
2. 增加功能：
- 视频转码功能：在进行视频剪辑时，一般不会直接剪辑源文件，而是将视频转换成画质更低的代理文件，这样可以使剪辑更加流畅。
- 通知功能：每次拍摄结束后，一般会有数百GB乃至数T的文件，拷贝需要很多的时间。通过添加短信 / 邮件通知功能，可以在拷贝完成后及时提醒用户。
- 断点续传功能：在程序意外退出时，能够记录当前的同步进度，并在重新打开软件时能够继续进行同步。这可以避免从头开始重新同步，节约大量时间。
3. 完善UI界面：目前程序的UI界面还比较粗糙，在后续的开发过程中可以进行优化。
