package psyco.kver.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException.NoNodeException;


/**
 * Created by peng on 16/2/1.
 */
public class ZookeeperOperation {

    private CuratorFramework curatorClient;

    public ZookeeperOperation() {
    }

    public ZookeeperOperation(CuratorFramework curatorClient) {
        this.curatorClient = curatorClient;
    }

    public void setCuratorClient(CuratorFramework curatorClient) {
        this.curatorClient = curatorClient;
    }

    public void watch(String path) throws Exception {
        curatorClient.checkExists().watched().forPath(path);
    }

    public boolean exists(String path) throws Exception {
        return curatorClient.checkExists().forPath(path) != null;
    }

    public boolean existsWatched(String path) throws Exception {
        return curatorClient.checkExists().watched().forPath(path) != null;
    }

    public byte[] getData(String path) throws Exception {
        try {
            return curatorClient.getData().forPath(path);
        } catch (NoNodeException e) {
            return null;
        }
    }

    public byte[] getDataWatched(String path) throws Exception {
        try {
            return curatorClient.getData().watched().forPath(path);
        } catch (NoNodeException e) {
            curatorClient.checkExists().watched().forPath(path);
            return null;
        }
    }

    public void setData(String path, byte[] data) throws Exception {
        try {
            curatorClient.setData().forPath(path, data);
        } catch (NoNodeException e) {
            curatorClient.create().creatingParentsIfNeeded().forPath(path, data);
        }
    }

    public void delete(String path) throws Exception {
        curatorClient.delete().deletingChildrenIfNeeded().forPath(path);
    }

}
