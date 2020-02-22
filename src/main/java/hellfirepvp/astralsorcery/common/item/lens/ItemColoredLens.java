/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.lens;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLens
 * Created by HellFirePvP
 * Date: 24.08.2019 / 21:23
 */
public abstract class ItemColoredLens extends Item implements ItemDynamicColor {

    private final LensColorType lensColorType;

    protected ItemColoredLens(LensColorType colorType) {
        this(colorType, new Properties().group(CommonProxy.ITEM_GROUP_AS));
    }

    protected ItemColoredLens(LensColorType colorType, Properties properties) {
        super(properties);
        this.lensColorType = colorType;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        World world = ctx.getWorld();
        if (!world.isRemote() && player != null) {
            TileLens lens = MiscUtils.getTileAt(world, ctx.getPos(), TileLens.class, false);
            if (lens != null) {
                ItemStack held = ctx.getItem();
                LensColorType oldType = lens.setColorType(this.lensColorType);

                if (!player.isCreative()) {
                    held.setCount(held.getCount() - 1);
                    if (held.getCount() <= 0) {
                        player.setHeldItem(ctx.getHand(), ItemStack.EMPTY);
                    }
                }

                SoundHelper.playSoundAround(SoundsAS.BLOCK_COLOREDLENS_ATTACH, world, ctx.getPos(), 0.8F, 1.5F);
                if (oldType != null) {
                    player.inventory.placeItemBackInInventory(world, oldType.getStack());
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        return this.lensColorType.getColor().getRGB();
    }
}
