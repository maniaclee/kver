package psyco.kver.zookeeper;

import org.apache.commons.lang3.Validate;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

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
                setProperty(rootPath, new byte[0]);
                this.started = true;
            }
        }
    }


    public boolean setProperty(String key, String value) {
        return setProperty(key, toBytes(value));
    }

    public boolean setProperty(String key, byte[] value) {
        try {
            String keyPath = getKeyPath(key);
            if (!exist(keyPath))
                client.create().forPath(keyPath, value);
            else
                client.setData().forPath(keyPath, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getProperty(String key) {
        try {
            return new String(client.getData().forPath(getKeyPath(key)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] toBytes(String s) {
        return s == null ? new byte[0] : s.getBytes();
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
