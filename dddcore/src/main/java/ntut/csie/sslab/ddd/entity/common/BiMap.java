package ntut.csie.sslab.ddd.entity.common;

import java.util.HashMap;
import java.util.Map;

public class BiMap<K, V> extends HashMap<K, V> {

    private Map<V,K> reverseMap = new HashMap<V,K>();

    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return super.put(key, value);
    }

    public K getKey(V value){
        return reverseMap.get(value);
    }

}
