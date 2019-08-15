package com.cgh.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZookeeperClientDemo {
   /* 1.构建客户端
    2.创建节点
    3.更新节点
    4.删除节点1
    5.查询节点*/

    ZooKeeper zk = null;

    //监听
    @Before
    public void init() throws Exception{
        zk = new ZooKeeper("cgh-01:2181,cgh-02:2181,cgh-03:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected && watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    System.out.println(watchedEvent.getPath()); // 收到的事件所发生的节点路径
                    System.out.println(watchedEvent.getType()); // 收到的事件的类型

                    try {
                        zk.getData("/javaOperate",true,null);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if (watchedEvent.getState() == Event.KeeperState.SyncConnected && watchedEvent.getType() == Event.EventType.NodeChildrenChanged){
                    System.out.println("子节点变化了......");
                }
            }
        });
    }

    @Test
    public void testCreate() throws Exception{
        String string = zk.create("/javaOperate", "使用java创建节点".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(string);
        zk.close();
    }


    @Test
    public void testUpdate() throws Exception{
        zk.setData("/javaOperate", "修改节点后的数据".getBytes(), -1);
        zk.close();
    }

    @Test
    public void testGet() throws Exception {
        // 参数1：节点路径    参数2：是否要监听    参数3：所要获取的数据的版本,null表示最新版本
        byte[] data = zk.getData("/eclipse", false, null);
        System.out.println(new String(data,"UTF-8"));

        zk.close();
    }



    @Test
    public void testListChildren() throws Exception {
        // 参数1：节点路径    参数2：是否要监听
        // 注意：返回的结果中只有子节点名字，不带全路径
        List<String> children = zk.getChildren("/cc", false);

        for (String child : children) {
            System.out.println(child);
        }

        zk.close();
    }


    @Test
    public void testRm() throws InterruptedException, KeeperException {

        zk.delete("/eclipse", -1);

        zk.close();
    }

    @Test
    public void testGetWatch() throws Exception{
        byte[] data = zk.getData("/javaOperate", true, null);
        List<String> children = zk.getChildren("/javaOperate", true);
        System.out.println(new String(data,"UTF-8"));
        Thread.sleep(Long.MAX_VALUE);
    }
}
