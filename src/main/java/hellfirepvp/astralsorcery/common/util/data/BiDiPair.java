/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BiDiPair
 * Created by HellFirePvP
 * Date: 01.01.2021 / 13:06
 */
public class BiDiPair<K, V> {

    private final K left;
    private final V right;

    public BiDiPair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public K getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiDiPair<?, ?> biDiPair = (BiDiPair<?, ?>) o;
        return (Objects.equals(left, biDiPair.left) && Objects.equals(right, biDiPair.right)) ||
                (Objects.equals(left, biDiPair.right) && Objects.equals(right, biDiPair.left));
    }

    @Override
    public int hashCode() {
        return left.hashCode() ^ right.hashCode();
    }
}
