package psyco.kver.zookeeper;

import org.apache.commons.lang3.Validate;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import psyco.kver.util.ByteUtil;
import psyco.kver.util.EncodeUtils;
import psyco.kver.util.Paths;

import java.util.List;
import java.util.function.Consumer;

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

    private volatile boolean started = false;
    private ZookeeperOperation zookeeperOperation;
    private final boolean fixPath;


    /***
     * @param zkHost
     * @param namespace root
     */
    public ZookeeperClient(String zkHost, String namespace) {
        this(zkHost, namespace, false);
    }

    public ZookeeperClient(String zkHost, String namespace, boolean fixPath) {
        this.zkHost = zkHost;
        this.namespace = namespace;
        Validate.notBlank(zkHost, "zkhost can't be empty");
        Validate.notBlank(namespace, "namespace can't be empty");
        this.fixPath = fixPath;
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


    public String getString(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToString(property);
    }

    public Double getDouble(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToDouble(property);
    }

    public Long getLong(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToLong(property);
    }

    public Integer getInteger(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToInt(property);
    }

    public Short getShort(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToShort(property);
    }

    public Float getFloat(String key) {
        byte[] property = getProperty(key);
        return property == null ? null : ByteUtil.byteToFloat(property);
    }

    public byte[] getProperty(String key) {
        try {
            return zookeeperOperation.getData(getKeyPath(key));
        } catch (Exception e) {
            return null;
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

    public void deleteProperty(String key) throws Exception {
        zookeeperOperation.delete(getKeyPath(key));
    }

    public void list(String path, Consumer<String> consumer) throws Exception {
        if (!path.equals("/"))
            consumer.accept(getString(path));
        List<String> strings = client.getChildren().forPath(getKeyPath(path));
        if (strings != null && !strings.isEmpty())
            for (String s : strings)
                list(Paths.path(path, s), consumer);
    }


    public CuratorFramework getClient() {
        return client;
    }

    /***
     * ignore the namespace , path must start with '/', so make sure of that.
     */
    private String getKeyPath(String s) {
        return fixPath ? Paths.path(s) : s;
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
