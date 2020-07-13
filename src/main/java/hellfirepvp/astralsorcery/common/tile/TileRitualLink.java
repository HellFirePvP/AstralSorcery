/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkableTileEntity;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
                MiscUtils.executeWithChunk(getWorld(), linkedTo, () -> {
                    TileRitualLink link = MiscUtils.getTileAt(getWorld(), linkedTo, TileRitualLink.class, true);
                    if (link == null) {
                        linkedTo = null;
                        markForUpdate();
                    }
                });
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playClientEffects() {
        if (this.linkedTo != null) {
            if (ticksExisted % 4 == 0) {
                Collection<Vector3> positions = MiscUtils.getCirclePositions(
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        Vector3.RotAxis.Y_AXIS, 0.4F - rand.nextFloat() * 0.1F, 10 + rand.nextInt(10));
                for (Vector3 v : positions) {
                    FXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(v)
                            .setScaleMultiplier(0.15F)
                            .setMotion(new Vector3(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.01, 0));
                    if (rand.nextBoolean()) {
                        particle.color(VFXColorFunction.WHITE);
                    }
                }
            }

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this).add(0.5, 0.5, 0.5))
                    .setScaleMultiplier(0.3F)
                    .setMotion(new Vector3(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.015, 0))
                    .color(VFXColorFunction.random());

        }
    }

    @Nullable
    public BlockPos getLinkedTo() {
        return linkedTo;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.linkedTo = NBTHelper.readFromSubTag(compound, "posLink", NBTHelper::readBlockPosFromNBT);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.linkedTo != null) {
            NBTHelper.setAsSubTag(compound, "posLink", nbt -> NBTHelper.writeBlockPosToNBT(this.linkedTo, nbt));
        }
    }

    @Override
    public void onLinkCreate(PlayerEntity player, BlockPos other) {
        this.linkedTo = other;
        TileRitualLink otherLink = MiscUtils.getTileAt(player.getEntityWorld(), other, TileRitualLink.class, true);
        if (otherLink != null) {
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
        if (otherLink == null || otherLink.linkedTo == null) return false;
        if (otherLink.linkedTo.equals(getPos())) {
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
