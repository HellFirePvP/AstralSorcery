/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 30.06.2018 / 11:40
 */
public abstract class AbstractPerk extends IForgeRegistryEntry.Impl<AbstractPerk> {

    public AbstractPerk(String name) {
        this.setRegistryName(AstralSorcery.MODID, name.toLowerCase());
    }

    public abstract PerkTreePoint getPoint();

    public abstract void applyPerk(EntityPlayer player, Side side);

    public abstract void removePerk(EntityPlayer player, Side side);

    public PerkTreePoint.AllocationStatus getPerkStatus(@Nullable EntityPlayer player, Side side) {
        if (player == null) {
            return PerkTreePoint.AllocationStatus.UNALLOCATED;
        }
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress == null) {
            return PerkTreePoint.AllocationStatus.UNALLOCATED;
        }
        if (progress.hasPerkUnlocked(this)) {
            return PerkTreePoint.AllocationStatus.ALLOCATED;
        }
        if (progress.hasFreeAlignmentLevel()) {
            boolean hasNextNode = false;
            for (AbstractPerk otherPerk : PerkTree.INSTANCE.getConnectedPerks(this)) {
                if (progress.hasPerkUnlocked(otherPerk)) {
                    hasNextNode = true;
                    break;
                }
            }
            if (hasNextNode) {
                return PerkTreePoint.AllocationStatus.UNLOCKABLE;
            }
        }
        return PerkTreePoint.AllocationStatus.UNALLOCATED;
    }

    public String getUnlocalizedName() {
        return "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath() + ".name";
    }

    @SideOnly(Side.CLIENT)
    public Collection<String> getLocalizedTooltip() {
        String key = "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath() + ".";
        if (I18n.hasKey(key + ".desc.1")) { // Might have a indexed list there
            List<String> tooltip = Lists.newArrayList();
            int count = 1;
            while (I18n.hasKey(key + ".desc." + count)) {
                tooltip.add(I18n.format(key + ".desc." + count));
                count++;
            }
            return tooltip;
        } else if (I18n.hasKey(key + ".desc")) {
            return Lists.newArrayList(I18n.format(key + ".desc"));
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPerk that = (AbstractPerk) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }
}
