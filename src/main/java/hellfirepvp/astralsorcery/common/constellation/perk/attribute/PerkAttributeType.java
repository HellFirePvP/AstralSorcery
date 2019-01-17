/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.AttributeReader;
import hellfirepvp.astralsorcery.common.constellation.perk.reader.AttributeReaderRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeType
 * Created by HellFirePvP
 * Date: 08.07.2018 / 12:22
 */
public class PerkAttributeType {

    protected static final Random rand = new Random();

    //May be used by subclasses to more efficiently track who's got a perk applied
    private Map<Side, List<UUID>> applicationCache = Maps.newHashMap();

    private final String type;

    public PerkAttributeType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }

    public String getUnlocalizedName() {
        return String.format("perk.attribute.%s.name", getTypeString());
    }

    protected void init() {}

    @Nullable
    public AttributeReader getReader() {
        return AttributeReaderRegistry.getReader(this.getTypeString());
    }

    @Nonnull
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new PerkAttributeModifier(getTypeString(), mode, modifier);
    }

    public void onApply(EntityPlayer player, Side side) {
        List<UUID> applied = applicationCache.computeIfAbsent(side, s -> Lists.newArrayList());
        if (!applied.contains(player.getUniqueID())) {
            applied.add(player.getUniqueID());
        }
    }

    public void onRemove(EntityPlayer player, Side side, boolean removedCompletely) {
        if (removedCompletely) {
            applicationCache.computeIfAbsent(side, s -> Lists.newArrayList()).remove(player.getUniqueID());
        }
    }

    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {}

    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side, boolean removedCompletely) {}

    protected boolean hasTypeApplied(EntityPlayer player, Side side) {
        return applicationCache.computeIfAbsent(side, s -> Lists.newArrayList()).contains(player.getUniqueID());
    }

    public final void clear(Side side) {
        applicationCache.remove(side);
    }

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
