package psyco.kver.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import psyco.kver.zookeeper.ZookeeperClient;

/**
 * Created by peng on 16/2/1.
 */
public class ZookeeperClientTest {
    ZookeeperClient zookeeperClient;

    @Before
    public void init() throws Exception {
        String zkHost = "localhost:2181";
        String namespace = "psyco";
        String test = "test";
        zookeeperClient = new ZookeeperClient(zkHost, namespace, test);
        zookeeperClient.start();
    }

    @After
    public void close() {
        if (zookeeperClient != null)
            zookeeperClient.close();
    }

    @Test
    public void test() throws Exception {
        String key = "key";
        zookeeperClient.setProperty(key, "fuckyousdfsdf");
        System.out.println("get->"+zookeeperClient.getString(key));
    }


}
