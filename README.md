# DaemonService
Android端心跳服务与进程保活

### 使用

继承AbsHeartBeatService抽象心跳服务，在onHeartBeat()中处理自己的任务，具体保活策略不需要关心

```java
public class HeartBeatService extends AbsHeartBeatService {

    @Override
    public void onStartService() {
    }

    @Override
    public void onStopService() {
    }

    @Override
    public long getHeartBeatMillis() {
        return 30 * 1000;
    }

    @Override
    public void onHeartBeat() {
    }
}
```

在Manifest中注册服务

```xml
<service android:name=".HeartBeatService"/>
```

初始化并启动服务

```java
DaemonHolder.init(this, HeartBeatService.class);
```

### 实现思想

实现进程保活，暂时实现了双进程守护、JobService检测与拉起、进程死亡AlarmManager定时拉起、
广播监听（网络变化、开机等），同时通过Timer和TimerTask实现心跳服务。

#### 1、双进程守护

双进程即本地进程和远程进程，看两个类：
AbsHeartBeatService：本地进程，抽象的心跳服务
DaemonService：远程进程，即守护进程
启动本地服务后会启动远程进程的服务并绑定远程服务，同时远程服务也会绑定本地进程的服务，
任何一个服务停止都会得到另一个进程的Binder通知，即刻被拉起，实现进程保活的一种方式

#### 2、JobService检测与拉起

Android5.0以上可以使用JobService来做定时任务，定时检测本地进程的服务是否在运行，参考JobSchedulerService，
但是个别深度定制的ROM厂商屏蔽了JobService，比如小米手机。

#### 3、进程死亡AlarmManager定时拉起

AlarmManager是提供一种访问系统闹钟服务的方式，允许你去设置在将来的某个时间点去执行你的应用程序。
当你的闹钟时间到时，在它上面注册的一个意图(Intent)将会被系统以广播发出，然后自动启动目标程序，如果它没有正在运行。
所以，不管是我们的本地进程还是我们的远程进程，如果他们死了，就在死的时候定一个被拉活闹钟，等待复活。

#### 4、广播监听

动态广播监听：网络变化、开屏、锁屏、解锁、点击Home键
静态广播监听：开机、连接电源、断开电源、安装应用、卸载应用