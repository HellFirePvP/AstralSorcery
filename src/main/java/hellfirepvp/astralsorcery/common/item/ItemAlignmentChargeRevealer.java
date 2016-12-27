package hellfirepvp.astralsorcery.common.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemAlignmentChargeRevealer
 * Created by HellFirePvP
 * Date: 27.12.2016 / 13:36
 */
public interface ItemAlignmentChargeRevealer {

    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ItemStack stack);

}
