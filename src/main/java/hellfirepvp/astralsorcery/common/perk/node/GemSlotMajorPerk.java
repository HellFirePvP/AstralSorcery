/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.item.gem.ItemPerkGem;
import hellfirepvp.astralsorcery.common.lib.PerkNamesAS;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeGem;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSlotMajorPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:22
 */
public class GemSlotMajorPerk extends MajorPerk implements GemSlotPerk {

    public GemSlotMajorPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.setName(PerkNamesAS.name("gem_socket"));
        this.disableTooltipCaching();
    }

    @Override
    protected PerkTreePoint<? extends GemSlotMajorPerk> initPerkTreePoint() {
        return new PerkTreeGem<>(this, getOffset());
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        Collection<PerkAttributeModifier> mods = super.getModifiers(player, side, ignoreRequirements);
        if (!modifiersDisabled(player, side)) {
            ItemStack contained = getContainedItem(player, side);
            if (!contained.isEmpty() && contained.getItem() instanceof ItemPerkGem) {
                mods.addAll(DynamicModifierHelper.getStaticModifiers(contained));
            }
        }
        return mods;
    }

    @Override
    public void onRemovePerkServer(PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onRemovePerkServer(player, progress, dataStorage);
        dropItemToPlayer(player, dataStorage);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<ITextComponent> tooltip) {
        if (super.addLocalizedTooltip(tooltip)) {
            tooltip.add(new StringTextComponent(""));
        }
        if (canSeeClient()) {
            this.addTooltipInfo(tooltip);
        }
        return true;
    }
}
