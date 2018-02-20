/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualLink
 * Created by HellFirePvP
 * Date: 05.01.2017 / 16:53
 */
public class TileRitualLink extends TileEntityTick implements ILinkableTile, IMultiblockDependantTile {

    private BlockPos linkedTo = null;

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

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternRitualPedestalWithLink;
    }

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
