/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.quality;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemDazzlingGem
 * Created by HellFirePvP
 * Date: 07.12.2020 / 19:48
 */
public class ItemDazzlingGem extends Item {

    public ItemDazzlingGem() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        getQuality(stack).ifPresent(quality -> tooltip.add(quality.getDisplayName()));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    public static boolean setQuality(ItemStack stack, GemQuality quality) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemDazzlingGem)) {
            return false;
        }
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        tag.putInt("quality", quality.ordinal());
        return true;
    }

    public static Optional<GemQuality> getQuality(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemDazzlingGem)) {
            return Optional.empty();
        }
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (!tag.contains("quality", Constants.NBT.TAG_INT)) {
            return Optional.empty();
        }
        int qualityId = tag.getInt("quality");
        if (qualityId < 0 || qualityId >= GemQuality.values().length) {
            return Optional.empty();
        }
        return Optional.of(GemQuality.values()[qualityId]);
    }
}
