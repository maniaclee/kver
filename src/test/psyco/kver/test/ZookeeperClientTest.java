package psyco.kver.test;

import org.junit.Test;
import psyco.kver.zookeeper.ZookeeperClient;

/**
 * Created by peng on 16/2/1.
 */
public class ZookeeperClientTest {

    @Test
    public void test() throws Exception {
        String zkHost = "localhost:2181";
        String namespace = "psyco";
        String test = "test";
        ZookeeperClient zookeeperClient = new ZookeeperClient(zkHost, namespace, test);
        String key = "key";
        zookeeperClient.set(key, "valuetest");
        System.out.println(zookeeperClient.get(key));
    }

}
