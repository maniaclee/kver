package maniac.lee.kver.test;

import maniac.lee.kver.zookeeper.ZookeeperClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by peng on 16/2/1.
 */
public class ZookeeperClientTest {
    ZookeeperClient zookeeperClient;

    @Before
    public void init() throws Exception {
        String zkHost = "localhost:2181";
        String namespace = "psyco";
        zookeeperClient = new ZookeeperClient(zkHost, namespace);
        zookeeperClient.start();
    }

    @After
    public void close() {
        if (zookeeperClient != null)
            zookeeperClient.close();
    }

    @Test
    public void test() throws Exception {
        String key = "/key";
//        zookeeperClient.setProperty(key, "fuckyousdfsdf");
        System.out.println("get->" + zookeeperClient.getString(key));
    }

    @Test
    public void children() throws Exception {
        String key = "/";
        zookeeperClient.list(key, s -> System.out.println(s));
    }


}
