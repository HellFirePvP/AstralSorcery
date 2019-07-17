/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkableTileEntity;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualLink
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:07
 */
public class TileRitualLink extends TileEntityTick implements LinkableTileEntity {

    private BlockPos linkedTo = null;

    public TileRitualLink() {
        super(TileEntityTypesAS.RITUAL_LINK);
    }

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isRemote()) {
            playClientEffects();
        } else {
            if (linkedTo != null) {
                if (MiscUtils.isChunkLoaded(getWorld(), new ChunkPos(linkedTo))) {
                    TileRitualLink link = MiscUtils.getTileAt(getWorld(), linkedTo, TileRitualLink.class, true);
                    if (link == null) {
                        linkedTo = null;
                        markForUpdate();
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playClientEffects() {
        /*
        TODO particles
        if(this.linkedTo != null && Minecraft.getMinecraft().player.getDistanceSq(getPos()) < 1024) { //32 Squared
            if(ticksExisted % 4 == 0) {
                Collection<Vector3> positions = MiscUtils.getCirclePositions(
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        Vector3.RotAxis.Y_AXIS, 0.4F - rand.nextFloat() * 0.1F, 10 + rand.nextInt(10));
                for (Vector3 v : positions) {
                    EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    particle.gravity(0.004).scale(0.15F);
                    particle.motion(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.01, 0);
                    if(rand.nextBoolean()) {
                        particle.setColor(Color.WHITE);
                    }
                }
            }
            Vector3 v = new Vector3(this).add(0.5, 0.5, 0.5);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
            particle.gravity(0.004).scale(0.3F);
            particle.motion(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.015, 0);
            particle.setColor(Color.getHSBColor(rand.nextFloat() * 360F, 1F, 1F));

        }
        */
    }

    @Override
    protected void onFirstTick() {}

    @Nullable
    public BlockPos getLinkedTo() {
        return linkedTo;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        if (compound.contains("posLink")) {
            this.linkedTo = NBTHelper.readBlockPosFromNBT(compound.getCompound("posLink"));
        } else {
            this.linkedTo = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if(this.linkedTo != null) {
            NBTHelper.setAsSubTag(compound, "posLink", nbt -> NBTHelper.writeBlockPosToNBT(this.linkedTo, nbt));
        }
    }

    @Override
    public void onLinkCreate(PlayerEntity player, BlockPos other) {
        this.linkedTo = other;
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        if(otherLink != null) {
            otherLink.linkedTo = getPos();
            otherLink.markForUpdate();
        }

        markForUpdate();
    }

    @Override
    public boolean tryLink(PlayerEntity player, BlockPos other) {
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        return otherLink != null && otherLink.linkedTo == null && !other.equals(getPos());
    }

    @Override
    public boolean tryUnlink(PlayerEntity player, BlockPos other) {
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
