package maniac.lee.kver.zookeeper;

/**
 * Created by lipeng on 16/2/2.
 */
public interface ZkListener {
    void onChange(ZkEvent zkEvent);
}
