/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAttunedCrystalBase
 * Created by HellFirePvP
 * Date: 21.07.2019 / 13:39
 */
public abstract class ItemAttunedCrystalBase extends ItemCrystalBase implements IConstellationFocus, ConstellationItem {

    public ItemAttunedCrystalBase(Properties prop) {
        super(prop);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> toolTip, ITooltipFlag flag) {
        Optional<Boolean> out = this.appendPropertyTooltip(stack, toolTip);
        if (Screen.hasShiftDown() && out.isPresent()) {
            ProgressionTier tier = ResearchHelper.getClientProgress().getTierReached();

            IWeakConstellation c = getAttunedConstellation(stack);
            if (c != null) {
                if (GatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
                    toolTip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.attuned", TextFormatting.BLUE + I18n.format(c.getUnlocalizedName()))));
                } else if (!out.get()) {
                    toolTip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("progress.missing.knowledge")));
                    out = Optional.of(true);
                }
            }

            IMinorConstellation tr = getTraitConstellation(stack);
            if (tr != null) {
                if (GatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchHelper.getClientProgress().hasConstellationDiscovered(tr)) {
                    toolTip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.trait", TextFormatting.BLUE + I18n.format(tr.getUnlocalizedName()))));
                } else if (!out.get()) {
                    toolTip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("progress.missing.knowledge")));
                }
            }
        }
    }

    @Nullable
    @Override
    public IConstellation getFocusConstellation(ItemStack stack) {
        return getAttunedConstellation(stack);
    }

    @Override
    public IWeakConstellation getAttunedConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    @Override
    public boolean setAttunedConstellation(ItemStack stack, IWeakConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack));
        } else {
            NBTHelper.getPersistentData(stack).remove(IConstellation.getDefaultSaveKey());
        }
        return true;
    }

    @Override
    public IMinorConstellation getTraitConstellation(ItemStack stack) {
        return (IMinorConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "constellationTrait");
    }

    @Override
    public boolean setTraitConstellation(ItemStack stack, IMinorConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "constellationTrait");
        } else {
            NBTHelper.getPersistentData(stack).remove("constellationTrait");
        }
        return true;
    }

}
