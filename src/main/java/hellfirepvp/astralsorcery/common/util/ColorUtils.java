/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ColorUtils
 * Created by HellFirePvP
 * Date: 10.11.2019 / 09:52
 */
public class ColorUtils {

    public static Color blendColors(Color color1, Color color2, float color1Ratio) {
        return new Color(blendColors(color1.getRGB(), color2.getRGB(), color1Ratio), true);
    }

    public static int blendColors(int color1, int color2, float color1Ratio) {
        float ratio1 = MathHelper.clamp(color1Ratio, 0F, 1F);
        float ratio2 = 1F - ratio1;

        int a1 = (color1 & 0xFF000000) >> 24;
        int r1 = (color1 & 0x00FF0000) >> 16;
        int g1 = (color1 & 0x0000FF00) >>  8;
        int b1 = (color1 & 0x000000FF);

        int a2 = (color2 & 0xFF000000) >> 24;
        int r2 = (color2 & 0x00FF0000) >> 16;
        int g2 = (color2 & 0x0000FF00) >>  8;
        int b2 = (color2 & 0x000000FF);

        int a = MathHelper.clamp(Math.round(a1 * ratio1 + a2 * ratio2), 0, 255);
        int r = MathHelper.clamp(Math.round(r1 * ratio1 + r2 * ratio2), 0, 255);
        int g = MathHelper.clamp(Math.round(g1 * ratio1 + g2 * ratio2), 0, 255);
        int b = MathHelper.clamp(Math.round(b1 * ratio1 + b2 * ratio2), 0, 255);

        return a << 24 | r << 16 | g << 8 | b;
    }

    public static Color overlayColor(Color base, Color overlay) {
        return new Color(overlayColor(base.getRGB(), overlay.getRGB()), true);
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

    public static int getOverlayColor(FluidStack stack) {
        if (stack.isEmpty()) {
            return 0xFFFFFFFF;
        }
        return stack.getFluid().getAttributes().getColor(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getOverlayColor(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0xFFFFFFFF;
        }
        if (stack.getItem() instanceof BlockItem) {
            BlockState state = ItemUtils.createBlockState(stack);
            if (state == null) {
                return 0xFFFFFFFF;
            }
            return Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        } else {
            return Minecraft.getInstance().getItemColors().getColor(stack, 0);
        }
    }

    @Nonnull
    public static ITextComponent getTranslation(DyeColor color) {
        return new TranslationTextComponent(String.format("color.minecraft.%s", color.getTranslationKey()));
    }

    @Nonnull
    public static Color flareColorFromDye(DyeColor color) {
        return ColorsAS.DYE_COLOR_PARTICLES[color.getId()];
    }

    @Nonnull
    public static TextFormatting textFormattingForDye(DyeColor color) {
        switch (color) {
            case WHITE:
                return TextFormatting.WHITE;
            case ORANGE:
                return TextFormatting.GOLD;
            case MAGENTA:
                return TextFormatting.DARK_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.DARK_AQUA;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case LIGHT_GRAY:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.BLUE;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.DARK_BLUE;
            case BROWN:
                return TextFormatting.GOLD;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case RED:
                return TextFormatting.DARK_RED;
            case BLACK:
                return TextFormatting.DARK_GRAY; //Black is unreadable. fck that.
            default:
                return TextFormatting.WHITE;
        }
    }
}
