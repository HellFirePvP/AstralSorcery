/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.socket;

import hellfirepvp.astralsorcery.common.data.research.PerkAllocationType;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.PerkNamesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeGem;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSocketMajorPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:22
 */
public class GemSocketMajorPerk extends MajorPerk implements GemSocketPerk {

    public GemSocketMajorPerk(ResourceLocation name, float x, float y) {
        super(name, x, y);
        this.setName(PerkNamesAS.name("gem_socket"));
        this.disableTooltipCaching();
    }

    @Override
    protected PerkTreePoint<? extends GemSocketMajorPerk> initPerkTreePoint() {
        return new PerkTreeGem<>(this, getOffset());
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        Collection<PerkAttributeModifier> mods = super.getModifiers(player, side, ignoreRequirements);
        ItemStack contained = getContainedItem(player, side);
        if (!contained.isEmpty() && contained.getItem() instanceof GemSocketItem) {
            mods.addAll(((GemSocketItem) contained.getItem()).getModifiers(contained, this, player, side));
        }
        return mods;
    }

    @Override
    public void onRemovePerkServer(PlayerEntity player, PerkAllocationType allocationType, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onRemovePerkServer(player, allocationType, progress, dataStorage);

        // Will be removed?
        if (progress.getPerkData().getAllocationTypes(this).size() <= 1) {
            dropItemToPlayer(player, dataStorage);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<IFormattableTextComponent> tooltip) {
        if (super.addLocalizedTooltip(tooltip)) {
            tooltip.add(new StringTextComponent(""));
        }
        if (canSeeClient()) {
            this.addTooltipInfo(tooltip);
        }
        return true;
    }
}
