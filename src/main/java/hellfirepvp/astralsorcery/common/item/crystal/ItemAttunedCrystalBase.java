/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.constellation.ConstellationItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

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
        CrystalAttributes.TooltipResult result = addCrystalPropertyToolTip(stack, toolTip);
        if (result != null) {
            ProgressionTier tier = ResearchHelper.getClientProgress().getTierReached();

            boolean addedMissing = result != CrystalAttributes.TooltipResult.ADDED_ALL;
            IWeakConstellation c = getAttunedConstellation(stack);
            if (c != null) {
                if (GatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchHelper.getClientProgress().hasConstellationDiscovered(c)) {
                    toolTip.add(new TranslationTextComponent("crystal.info.astralsorcery.attuned",
                            c.getConstellationName().applyTextStyle(TextFormatting.BLUE))
                            .applyTextStyle(TextFormatting.GRAY));
                } else if (!addedMissing) {
                    toolTip.add(new TranslationTextComponent("astralsorcery.progress.missing.knowledge").applyTextStyle(TextFormatting.GRAY));
                    addedMissing = true;
                }
            }

            IMinorConstellation tr = getTraitConstellation(stack);
            if (tr != null) {
                if (GatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchHelper.getClientProgress().hasConstellationDiscovered(tr)) {
                    toolTip.add(new TranslationTextComponent("crystal.info.astralsorcery.trait",
                            tr.getConstellationName().applyTextStyle(TextFormatting.BLUE))
                            .applyTextStyle(TextFormatting.GRAY));
                } else if (!addedMissing) {
                    toolTip.add(new TranslationTextComponent("astralsorcery.progress.missing.knowledge").applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IWeakConstellation cst = this.getAttunedConstellation(stack);
        if (cst != null) {
            return new TranslationTextComponent(super.getTranslationKey(stack) + ".typed", cst.getConstellationName());
        }
        return super.getDisplayName(stack);
    }

    @Nullable
    @Override
    public IConstellation getFocusConstellation(ItemStack stack) {
        return getAttunedConstellation(stack);
    }

    @Override
    @Nullable
    public IWeakConstellation getAttunedConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    @Override
    public boolean setAttunedConstellation(ItemStack stack, @Nullable IWeakConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack));
        } else {
            NBTHelper.getPersistentData(stack).remove(IConstellation.getDefaultSaveKey());
        }
        return true;
    }

    @Override
    @Nullable
    public IMinorConstellation getTraitConstellation(ItemStack stack) {
        return (IMinorConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack), "constellationTrait");
    }

    @Override
    public boolean setTraitConstellation(ItemStack stack, @Nullable IMinorConstellation cst) {
        if (cst != null) {
            cst.writeToNBT(NBTHelper.getPersistentData(stack), "constellationTrait");
        } else {
            NBTHelper.getPersistentData(stack).remove("constellationTrait");
        }
        return true;
    }

}
