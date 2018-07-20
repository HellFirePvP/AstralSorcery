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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractPerk
 * Created by HellFirePvP
 * Date: 30.06.2018 / 11:40
 */
public abstract class AbstractPerk extends IForgeRegistryEntry.Impl<AbstractPerk> {

    protected static final Random rand = new Random();

    public static final PerkCategory CATEGORY_BASE = new PerkCategory("base");
    public static final PerkCategory CATEGORY_ROOT = new PerkCategory("root");
    public static final PerkCategory CATEGORY_MAJOR = new PerkCategory("major");
    public static final PerkCategory CATEGORY_KEY = new PerkCategory("key");

    protected final Point offset;
    private PerkCategory category = CATEGORY_BASE;
    private List<String> tooltipCache = null;
    private String ovrUnlocalizedNamePrefix = null;

    public AbstractPerk(String name, int x, int y) {
        this.setRegistryName(AstralSorcery.MODID, name.toLowerCase());
        this.offset = new Point(x, y);
    }

    public AbstractPerk(ResourceLocation name, int x, int y) {
        this.setRegistryName(name);
        this.offset = new Point(x, y);
    }

    public Point getOffset() {
        return offset;
    }

    public PerkTreePoint getPoint() {
        return new PerkTreePoint(this, this.getOffset());
    }

    public <T> T setCategory(PerkCategory category) {
        this.category = category;
        return (T) this;
    }

    //Reserving application/removal methods to delegate for later pre-application logic
    public final void applyPerk(EntityPlayer player, Side side) {
        this.applyPerkLogic(player, side);
    }

    public final void removePerk(EntityPlayer player, Side side) {
        this.removePerkLogic(player, side);
    }

    protected abstract void applyPerkLogic(EntityPlayer player, Side side);

    protected abstract void removePerkLogic(EntityPlayer player, Side side);

    /**
     * Called ONCE when the perk is unlocked
     * You may use the NBTTagCompound to save data to remove it again later
     * The player might be null for root perks on occasion.
     */
    public void onUnlockPerkServer(@Nullable EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {}

    /**
     * Clean up and remove the perk from that single player.
     * Data in the dataStorage is filled with the data set in onUnlockPerkServer
     */
    public void onRemovePerkServer(EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {}

    public <T> T setNameOverride(String namePrefix) {
        this.ovrUnlocalizedNamePrefix = namePrefix;
        return (T) this;
    }

    @Nonnull
    public PerkCategory getCategory() {
        return category;
    }

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

        return mayUnlockPerk(progress) ? PerkTreePoint.AllocationStatus.UNLOCKABLE : PerkTreePoint.AllocationStatus.UNALLOCATED;
    }

    public boolean mayUnlockPerk(PlayerProgress progress) {
        if (!progress.hasFreeAllocationPoint()) return false;

        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if (progress.hasPerkUnlocked(otherPerks)) {
                return true;
            }
        }
        return false;
    }

    public String getUnlocalizedName() {
        if (this.ovrUnlocalizedNamePrefix != null) {
            return this.ovrUnlocalizedNamePrefix;
        }
        return "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath();
    }

    @SideOnly(Side.CLIENT)
    public Collection<String> getLocalizedTooltip() {
        if (tooltipCache != null) {
            return tooltipCache;
        }

        tooltipCache = Lists.newArrayList();
        String key = this.ovrUnlocalizedNamePrefix;
        if (key == null) {
            key = "perk." + getRegistryName().getResourceDomain() + "." + getRegistryName().getResourcePath();
        }
        if (I18n.hasKey(key + ".desc.1")) { // Might have a indexed list there
            int count = 1;
            while (I18n.hasKey(key + ".desc." + count)) {
                tooltipCache.add(I18n.format(key + ".desc." + count));
                count++;
            }
        } else if (I18n.hasKey(key + ".desc")) {
            tooltipCache.add(I18n.format(key + ".desc"));
        }
        return tooltipCache;
    }

    //Should return a localized string of the mod (or part of a mod) that added this perk
    //Default: modname of added mod
    @Nullable
    @SideOnly(Side.CLIENT)
    public Collection<String> getSource() {
        String modid = getRegistryName().getResourceDomain();
        ModContainer mod = Loader.instance().getIndexedModList().get(modid);
        if (mod != null) {
            return Lists.newArrayList(mod.getName());
        }
        return null;
    }

    public void clearCaches(Side side) {}

    @SideOnly(Side.CLIENT)
    public void clearClientCaches() {
        this.tooltipCache = null;
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

    public static class PerkCategory {

        private final String unlocName;
        private String textFormatting;

        public PerkCategory(@Nonnull String unlocName) {
            this.unlocName = unlocName;
            this.textFormatting = TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString();
        }

        public String getUnlocalizedName() {
            return unlocName;
        }

        public String getTextFormatting() {
            return textFormatting;
        }

        @Nullable
        @SideOnly(Side.CLIENT)
        public String getLocalizedName() {
            String str = "perk.category." + unlocName + ".name";
            if (I18n.hasKey(str)) {
                return I18n.format("perk.category." + unlocName + ".name");
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PerkCategory that = (PerkCategory) o;
            return Objects.equals(unlocName, that.unlocName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(unlocName);
        }

    }

}
