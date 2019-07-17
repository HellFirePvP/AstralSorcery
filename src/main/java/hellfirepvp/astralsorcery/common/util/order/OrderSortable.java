/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.order;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrderSortable
 * Created by HellFirePvP
 * Date: 17.07.2019 / 20:04
 */
public abstract class OrderSortable {

    private final List<Object> before = new ArrayList<>();
    private final List<Object> after = new ArrayList<>();

    public <E extends OrderSortable> E setBefore(Object other) {
        this.before.add(other);
        return (E) this;
    }

    public <E extends OrderSortable> E setAfter(Object other) {
        this.after.add(other);
        return (E) this;
    }

    public boolean isBefore(Object other) {
        return this.before.contains(other);
    }

    public boolean isAfter(Object other) {
        return this.after.contains(other);
    }

}
