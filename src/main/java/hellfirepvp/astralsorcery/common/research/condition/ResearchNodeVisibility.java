/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.condition;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeVisibility
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchNodeVisibility {

    private final Type type;
    private final List<Component> reasons = new ArrayList<>();

    public ResearchNodeVisibility(Type type, List<Component> reasons) {
        this.type = type;
        this.reasons.addAll(reasons);
    }

    public static ResearchNodeVisibility hidden() {
        return new ResearchNodeVisibility(Type.HIDDEN, Collections.emptyList());
    }

    public Type getType() {
        return this.type;
    }

    public List<Component> getReasons() {
        return Collections.unmodifiableList(this.reasons);
    }

    public enum Type {

        VISIBLE,
        LOCKED,
        HIDDEN;

        public boolean shouldRender() {
            return this == VISIBLE || this == LOCKED;
        }
    }
}
