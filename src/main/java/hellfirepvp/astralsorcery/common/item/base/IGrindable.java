package hellfirepvp.astralsorcery.common.item.base;

import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        GL11.glTranslated(0.5, 0.72, -0.06);
        GL11.glRotated(35, 1, 0, 0);
        GL11.glRotated(15, 0, 0, 1);
        GL11.glScaled(0.6, 0.6, 0.6);
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

        @Nullable
        public ItemStack getStack() {
            return stack;
        }

        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, null);
        }

        public static GrindResult itemChange(ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, null);
        }

        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, null);
        }

    }

    public static enum ResultType {

        SUCCESS, //Successfully grinded something
        ITEMCHANGE, //Successfully grinded something, other item now on the grindstone
        FAIL_SILENT, //Did nothing, but nothing went wrong. just.. uuuh.. nothing.
        FAIL_BREAK_ITEM //The item broke while grinding.

    }

}
