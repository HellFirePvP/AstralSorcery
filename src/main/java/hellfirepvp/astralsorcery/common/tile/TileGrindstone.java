/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileGrindstone
 * Created by HellFirePvP
 * Date: 10.11.2016 / 22:28
 */
public class TileGrindstone extends TileEntitySynchronized implements ITickable {

    public static final int TICKS_WHEEL_ROTATION = 20;

    private ItemStack grindingItem = null;
    public int tickWheelAnimation = 0, prevTickWheelAnimation = 0;
    private boolean repeat = false; //Used for repeat after effect went off..~

    @Override
    public void update() {
        if(world.isRemote) {
            if(tickWheelAnimation > 0) {
                prevTickWheelAnimation = tickWheelAnimation;
                tickWheelAnimation--;
                if(tickWheelAnimation <= 0 && repeat) {
                    tickWheelAnimation = TICKS_WHEEL_ROTATION;
                    prevTickWheelAnimation = TICKS_WHEEL_ROTATION + 1;
                    repeat = false;
                }
            } else {
                prevTickWheelAnimation = 0;
                tickWheelAnimation = 0;
            }
        }
    }

    public void playWheelEffect() {
        PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.EffectType.GRINDSTONE_WHEEL, getPos());
        if(world.isRemote) {
            playWheelAnimation(effect);
        } else {
            PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(world, getPos(), 32));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playWheelAnimation(PktPlayEffect pktPlayEffect) {
        TileGrindstone tgr = MiscUtils.getTileAt(Minecraft.getMinecraft().world, pktPlayEffect.pos, TileGrindstone.class, false);
        if(tgr != null) {
            if(tgr.tickWheelAnimation == 0) {
                tgr.tickWheelAnimation = TICKS_WHEEL_ROTATION;
            } else if(tgr.tickWheelAnimation * 2 <= TICKS_WHEEL_ROTATION) {
                tgr.repeat = true;
            }
        }
    }

    public void setGrindingItem(@Nullable ItemStack stack) {
        this.grindingItem = stack;
        markForUpdate();
    }

    @Nullable
    public ItemStack getGrindingItem() {
        return grindingItem;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        grindingItem = ItemStack.loadItemStackFromNBT(compound);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if(grindingItem != null) {
            grindingItem.writeToNBT(compound);
        }
    }

}
