package hellfirepvp.astralsorcery.common.item.crystal.base;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCrystalBase
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:47
 */
public abstract class ItemTunedCrystalBase extends ItemRockCrystalBase {

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        Optional<Boolean> out = addCrystalPropertyToolTip(stack, tooltip);
        boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        if(shift && out.isPresent()) {
            ProgressionTier tier = ResearchManager.clientProgress.getTierReached();

            Constellation c = getConstellation(stack);
            if(c != null) {
                if(EnumGatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchManager.clientProgress.hasConstellationDiscovered(c.getName())) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.attuned") + " " + TextFormatting.BLUE + I18n.translateToLocal(c.getName()));
                } else if(!out.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("progress.missing.knowledge"));
                    out = Optional.of(true);
                }
            }

            Constellation tr = getTrait(stack);
            if(tr != null) {
                if(EnumGatedKnowledge.CRYSTAL_TUNE.canSee(tier) && ResearchManager.clientProgress.hasConstellationDiscovered(tr.getName())) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.trait") + " " + TextFormatting.BLUE + I18n.translateToLocal(tr.getName()));
                } else if(!out.get()) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("progress.missing.knowledge"));
                }
            }
        }
    }

    public static void applyTrait(ItemStack stack, Constellation trait) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("trait", trait.getName());
    }

    public static Constellation getTrait(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("trait");
        return ConstellationRegistry.getConstellationByName(strCName);
    }

    public static void applyConstellation(ItemStack stack, Constellation constellation) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static Constellation getConstellation(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        return Constellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

}
