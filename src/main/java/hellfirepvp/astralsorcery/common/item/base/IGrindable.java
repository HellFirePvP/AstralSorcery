/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IGrindable
 * Created by HellFirePvP
 * Date: 13.09.2016 / 13:10
 */
public interface IGrindable {

    public boolean canGrind(TileGrindstone grindstone, ItemStack stack);

    @Nonnull
    public GrindResult grind(TileGrindstone grindstone, ItemStack stack, Random rand);

    @SideOnly(Side.CLIENT)
    default public void applyClientGrindstoneTransforms() {
        applyDefaultGrindstoneTransforms();
    }

    @SideOnly(Side.CLIENT)
    public static void applyDefaultGrindstoneTransforms() {
        GL11.glTranslated(0.55, 0.75, 0.6);
        GL11.glRotated(125, 1, 0, 0);
        GL11.glRotated(180, 0, 0, 1);
        /*GL11.glRotated(180, 0, 1, 0);
        GL11.glRotated(35, 1, 0, 0);
        GL11.glRotated(15, 0, 0, 1);*/
    }

    public static class GrindResult {

        private final ResultType type;
        private final ItemStack stack;

        private GrindResult(ResultType type, ItemStack stack) {
            this.type = type;
            this.stack = stack;
        }

        public ResultType getType() {
            return type;
        }

        @Nonnull
        public ItemStack getStack() {
            return stack;
        }

        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, ItemStack.EMPTY);
        }

        public static GrindResult itemChange(@Nonnull ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, ItemStack.EMPTY);
        }

        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, ItemStack.EMPTY);
        }

    }

    public static enum ResultType {

        SUCCESS, //Successfully grinded something
        ITEMCHANGE, //Successfully grinded something, other item now on the grindstone
        FAIL_SILENT, //Did nothing, but nothing went wrong. just.. uuuh.. nothing.
        FAIL_BREAK_ITEM //The item broke while grinding.

    }

}
