package hellfirepvp.astralsorcery.common.util.data;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Tuple
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:14
 */
public class Tuple<K, V> {

    public final K key;
    public final V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

}
