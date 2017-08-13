/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileVanishing
 * Created by HellFirePvP
 * Date: 30.07.2017 / 17:35
 */
@Optional.Interface(modid = "albedo", iface = "elucent.albedo.lighting.ILightProvider")
public class TileVanishing extends TileEntityTick implements ILightProvider {

    private static final AxisAlignedBB topBox = new AxisAlignedBB(-0.9,0, -0.9, 0.9, 0.9, 0.9);

    @Override
    public void update() {
        super.update();

        if(!world.isRemote && ticksExisted % 10 == 0) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, topBox.offset(pos));
            for (EntityPlayer player : players) {
                ItemStack held = player.getHeldItemMainhand();
                if(!held.isEmpty() && held.getItem() instanceof ItemWand && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    return;
                }
                held = player.getHeldItemOffhand();
                if(!held.isEmpty() && held.getItem() instanceof ItemWand && WandAugment.AEVITAS == ItemWand.getAugment(held)) {
                    return;
                }
            }
            getWorld().setBlockToAir(getPos());
        }
    }

    @Override
    protected void onFirstTick() {}

    @Override
    @SideOnly(Side.CLIENT)
    public Light provideLight() {
        return new Light.Builder()
                .pos(getPos())
                .radius(6F)
                .color(0xFF1324EE, true)
                .build();
    }
}
