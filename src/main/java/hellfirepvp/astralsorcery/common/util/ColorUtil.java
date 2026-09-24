/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.extensions.IFluidExtension;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColorUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ColorUtil {

    public static ColorWrapper blendColors(ColorWrapper color1, ColorWrapper color2, float color1Ratio) {
        return ColorWrapper.transparent(blendColors(color1.getColor(), color2.getColor(), color1Ratio));
    }

    public static int blendColors(int color1, int color2, float color1Ratio) {
        float ratio1 = Mth.clamp(color1Ratio, 0F, 1F);
        float ratio2 = 1F - ratio1;

        int a1 = (color1 & 0xFF000000) >> 24;
        int r1 = (color1 & 0x00FF0000) >> 16;
        int g1 = (color1 & 0x0000FF00) >>  8;
        int b1 = (color1 & 0x000000FF);

        int a2 = (color2 & 0xFF000000) >> 24;
        int r2 = (color2 & 0x00FF0000) >> 16;
        int g2 = (color2 & 0x0000FF00) >>  8;
        int b2 = (color2 & 0x000000FF);

        int a = Mth.clamp(Math.round(a1 * ratio1 + a2 * ratio2), 0, 255);
        int r = Mth.clamp(Math.round(r1 * ratio1 + r2 * ratio2), 0, 255);
        int g = Mth.clamp(Math.round(g1 * ratio1 + g2 * ratio2), 0, 255);
        int b = Mth.clamp(Math.round(b1 * ratio1 + b2 * ratio2), 0, 255);

        return a << 24 | r << 16 | g << 8 | b;
    }

    public static double colorDistance(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;

        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    public static ColorWrapper overlayColor(ColorWrapper base, ColorWrapper overlay) {
        return ColorWrapper.transparent(overlayColor(base.getColor(), overlay.getColor()));
    }

    public static int overlayColor(int base, int overlay) {
        int alpha = (base & 0xFF000000) >> 24;

        int baseR = (base & 0x00FF0000) >> 16;
        int baseG = (base & 0x0000FF00) >>  8;
        int baseB = (base & 0x000000FF);

        int overlayR = (overlay & 0x00FF0000) >> 16;
        int overlayG = (overlay & 0x0000FF00) >>  8;
        int overlayB = (overlay & 0x000000FF);

        int r = Math.round(baseR * (overlayR / 255F)) & 0xFF;
        int g = Math.round(baseG * (overlayG / 255F)) & 0xFF;
        int b = Math.round(baseB * (overlayB / 255F)) & 0xFF;

        return alpha << 24 | r << 16 | g << 8 | b;
    }

    @OnlyIn(Dist.CLIENT)
    public static ColorWrapper getOverlayColor(FluidStack stack) {
        if (stack.isEmpty()) return ColorWrapper.WHITE;
        return ColorWrapper.transparent(IClientFluidTypeExtensions.of(stack.getFluidType()).getTintColor(stack));
    }

    @OnlyIn(Dist.CLIENT)
    public static ColorWrapper getOverlayColor(ItemStack stack) {
        if (stack.isEmpty()) return ColorWrapper.WHITE;
        if (stack.getItem() instanceof BlockItem) {
            BlockState state = ItemUtil.createBlockState(Minecraft.getInstance().level, stack);
            if (state == null || state.isAir()) return ColorWrapper.WHITE;
            return ColorWrapper.transparent(Minecraft.getInstance().getBlockColors().getColor(state, Minecraft.getInstance().level, BlockPos.ZERO, 0));
        } else {
            return ColorWrapper.transparent(Minecraft.getInstance().getItemColors().getColor(stack, 0));
        }
    }

    public static MutableComponent getColorName(DyeColor color) {
        return Component.translatable(String.format("color.minecraft.%s", color.getSerializedName()));
    }
}
