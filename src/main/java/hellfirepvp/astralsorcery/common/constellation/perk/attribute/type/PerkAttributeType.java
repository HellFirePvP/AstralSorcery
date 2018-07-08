/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeType
 * Created by HellFirePvP
 * Date: 08.07.2018 / 12:22
 */
public class PerkAttributeType {

    private final String type;

    public PerkAttributeType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }

    protected void init() {}

    public void onApply(EntityPlayer player, Side side) {}

    public void onRemove(EntityPlayer player, Side side) {}

    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {}

    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeType that = (PerkAttributeType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
