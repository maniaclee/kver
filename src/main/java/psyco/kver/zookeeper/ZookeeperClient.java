package psyco.kver.zookeeper;

import org.apache.commons.lang3.Validate;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import psyco.kver.util.EncodeUtils;

/**
 * Created by peng on 16/2/1.
 */
public class ZookeeperClient {
    CuratorFramework client;
    /**
     * localhost:2181
     */
    private final String zkHost;
    /**
     * psyco
     */
    private final String namespace;
    /**
     * app type
     */
    private final String rootPath;

    private volatile boolean started = false;
    private ZookeeperOperation zookeeperOperation;

    /***
     * @param zkHost
     * @param namespace root
     * @param directory path to use , can be /a/b/c
     */
    public ZookeeperClient(String zkHost, String namespace, String directory) {
        this.zkHost = zkHost;
        this.namespace = namespace;
        Validate.notBlank(zkHost, "zkhost can't be empty");
        Validate.notBlank(namespace, "namespace can't be empty");
        Validate.notBlank(directory, "directory can't be empty");
        this.rootPath = path(directory);
    }


    /***
     * thread safe
     */
    public void start() throws Exception {
        if (started)
            return;
        synchronized (this) {
            if (!started) {
                client = CuratorFrameworkFactory.builder()
                        .connectString(zkHost)
                        .namespace(namespace)
                        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                        .connectionTimeoutMs(5000)
                        .build();
                client.start();
                this.zookeeperOperation = new ZookeeperOperation(client);
                this.started = true;
            }
        }
    }


    public boolean setProperty(String key, String value) {
        try {
            zookeeperOperation.setData(getKeyPath(key), EncodeUtils.toBytes(value));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setProperty(String key, byte[] value) {
        try {
            zookeeperOperation.setData(getKeyPath(key), value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getString(String key) {
        try {
            return EncodeUtils.getString(zookeeperOperation.getData(getKeyPath(key)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getProperty(String key) {
        try {
            return zookeeperOperation.getData(getKeyPath(key));
        } catch (Exception e) {
            return null;
        }
    }

    public static String path(String path, String... subs) {
        Validate.notBlank(path);
        path = fixPath(path);
        StringBuilder stringBuilder = new StringBuilder(path);
        if (subs != null && subs.length > 0)
            for (String s : subs)
                stringBuilder.append(fixPath(s));
        return stringBuilder.toString();
    }

    public void deleteProperty(String key) throws Exception {
        zookeeperOperation.delete(getKeyPath(key));
    }

    public String getRootPath() {
        return rootPath;
    }

    public CuratorFramework getClient() {
        return client;
    }

    private String getKeyPath(String s) {
        return path(rootPath, s);
    }

    private static String fixPath(String s) {
        String re = s.startsWith("/") ? s : ("/" + s);
        return re.endsWith("/") ? re.substring(0, re.length() - 1) : re;
    }

    private boolean exist(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void close() {
        this.client.close();
    }

}
