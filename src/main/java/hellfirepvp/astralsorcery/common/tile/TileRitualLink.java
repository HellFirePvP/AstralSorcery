/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingSprite;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualLink
 * Created by HellFirePvP
 * Date: 05.01.2017 / 16:53
 */
public class TileRitualLink extends TileEntityTick implements ILinkableTile {

    private BlockPos linkedTo = null;

    private Object clientStarSprite = null;

    @Override
    public void update() {
        super.update();

        if(world.isRemote) {
            playClientEffects();
        } else {
            if(linkedTo != null) {
                if(MiscUtils.isChunkLoaded(world, new ChunkPos(linkedTo))) {
                    TileRitualLink link = MiscUtils.getTileAt(world, linkedTo, TileRitualLink.class, true);
                    if(link == null) {
                        linkedTo = null;
                        markForUpdate();
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playClientEffects() {
        if(this.linkedTo != null) {
            if(clientStarSprite == null || ((EntityFXFacingSprite) clientStarSprite).isRemoved()) {
                EntityFXFacingSprite sprite = EntityFXFacingSprite.fromSpriteSheet(SpriteLibrary.spriteStar1, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.6F, 2);
                EffectHandler.getInstance().registerFX(sprite);
                this.clientStarSprite = sprite;
                sprite.setRefreshFunc(() -> clientStarSprite == sprite && !isInvalid());
            }
        } else {
            clientStarSprite = null;
        }
    }

    public BlockPos getLinkedTo() {
        return linkedTo;
    }

    public void updateLink(@Nullable BlockPos link) {
        this.linkedTo = link;
        markForUpdate();
    }

    @Override
    protected void onFirstTick() {}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if(compound.hasKey("posLink")) {
            this.linkedTo = NBTUtils.readBlockPosFromNBT(compound.getCompoundTag("posLink"));
        } else {
            this.linkedTo = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if(this.linkedTo != null) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(this.linkedTo, tag);
            compound.setTag("posLink", tag);
        }
    }

    @Override
    public World getLinkWorld() {
        return getWorld();
    }

    @Override
    public BlockPos getLinkPos() {
        return getPos();
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockrituallink.name";
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        this.linkedTo = other;
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        if(otherLink != null) {
            otherLink.linkedTo = getPos();
            otherLink.markForUpdate();
        }

        markForUpdate();
    }

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        return otherLink != null && otherLink.linkedTo == null && !other.equals(getPos());
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        if(otherLink == null || otherLink.linkedTo == null) return false;
        if(otherLink.linkedTo.equals(getPos())) {
            this.linkedTo = null;
            otherLink.linkedTo = null;
            otherLink.markForUpdate();
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return linkedTo != null ? Lists.newArrayList(linkedTo) : Lists.newArrayList();
    }
}
