/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravedStarMap;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemInfusedGlass
 * Created by HellFirePvP
 * Date: 01.05.2020 / 07:29
 */
public class ItemInfusedGlass extends Item {


    public ItemInfusedGlass() {
        super(new Properties()
                .maxStackSize(1)
                .maxDamage(5)
                .group(CommonProxy.ITEM_GROUP_AS));

        this.addPropertyOverride(new ResourceLocation("engraved"),
                (stack, world, entity) -> getEngraving(stack) != null ? 1 : 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        EngravedStarMap map = getEngraving(stack);
        if (map != null) {
            for (IConstellation cst : map.getConstellations()) {
                String format = "item.astralsorcery.infused_glass.ttip";
                ITextComponent cstName = cst.getConstellationName().applyTextStyle(TextFormatting.BLUE);

                if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative()) {
                    String percent = String.valueOf(Math.round(map.getDistribution(cst) * 100F));
                    ITextComponent creativeHint = new TranslationTextComponent("item.astralsorcery.infused_glass.ttip.creative", percent)
                            .applyTextStyle(TextFormatting.LIGHT_PURPLE);

                    tooltip.add(new TranslationTextComponent(format, cstName, creativeHint).applyTextStyle(TextFormatting.GRAY));
                } else {
                    tooltip.add(new TranslationTextComponent(format, cstName, "").applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return super.hasEffect(stack) || getEngraving(stack) != null;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        EngravedStarMap map = getEngraving(stack);
        if (map != null) {
            return super.getTranslationKey(stack) + ".active";
        }
        return super.getTranslationKey(stack);
    }

    @Nullable
    public static EngravedStarMap getEngraving(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfusedGlass)) {
            return null;
        }

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (tag.contains("starmap", Constants.NBT.TAG_COMPOUND)) {
            return EngravedStarMap.deserialize(tag.getCompound("starmap"));
        }
        return null;
    }

    public static void setEngraving(@Nonnull ItemStack stack, @Nullable EngravedStarMap map) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemInfusedGlass)) {
            return;
        }

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (map == null) {
            tag.remove("starmap");
        } else {
            tag.put("starmap", map.serialize());
        }
    }

}
