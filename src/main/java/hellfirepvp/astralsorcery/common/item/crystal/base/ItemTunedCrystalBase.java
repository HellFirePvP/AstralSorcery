/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal.base;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCrystalBase
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:47
 */
public abstract class ItemTunedCrystalBase extends ItemRockCrystalBase implements ItemConstellationFocus {

    protected ItemTunedCrystalBase() {
        setCreativeTab(RegistryItems.creativeTabAstralSorceryTunedCrystals);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Optional<Boolean> out = addCrystalPropertyToolTip(stack, tooltip);
        boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        if(shift && out.isPresent()) {
            ProgressionTier tier = ResearchManager.clientProgress.getTierReached();

            IWeakConstellation c = getMainConstellation(stack);
            if(c != null) {
                if(EnumGatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("crystal.attuned", TextFormatting.BLUE + I18n.format(c.getUnlocalizedName())));
                } else if(!out.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                    out = Optional.of(true);
                }
            }

            IMinorConstellation tr = getTrait(stack);
            if(tr != null) {
                if(EnumGatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchManager.clientProgress.hasConstellationDiscovered(tr.getUnlocalizedName())) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("crystal.trait", TextFormatting.BLUE + I18n.format(tr.getUnlocalizedName())));
                } else if(!out.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
            }
        }
    }

    @Nullable
    @Override
    public IConstellation getFocusConstellation(ItemStack stack) {
        return getMainConstellation(stack);
    }

    public static void applyTrait(ItemStack stack, IMinorConstellation trait) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("trait", trait.getUnlocalizedName());
    }

    @Nullable
    public static IMinorConstellation getTrait(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("trait");
        return (IMinorConstellation) ConstellationRegistry.getConstellationByName(strCName);
    }

    public static void applyMainConstellation(ItemStack stack, IWeakConstellation constellation) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    @Nullable
    public static IWeakConstellation getMainConstellation(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

}
