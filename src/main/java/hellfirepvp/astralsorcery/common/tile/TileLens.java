/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockLens;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.item.lens.LensColorType;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.base.network.TileTransmissionBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightTransmissionLens;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileLens
 * Created by HellFirePvP
 * Date: 24.08.2019 / 21:19
 */
public class TileLens extends TileTransmissionBase<IPrismTransmissionNode> implements CrystalAttributeTile {

    private CrystalAttributes attributes = null;
    private LensColorType colorType = null;

    private int lensEffectTimeout = 0;

    //So we can tell the client to render beams eventhough the actual connection doesn't exist.
    private List<BlockPos> occupiedConnections = new LinkedList<>();

    protected TileLens(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public TileLens() {
        super(TileEntityTypesAS.LENS);
    }

    @Override
    public void tick() {
        super.tick();

        if (colorType != null) {
            if (world.isRemote()) {
                playColorEffects();
            } else {
                doColorEffects();
            }
        }
    }

    public void transmissionTick() {
        this.lensEffectTimeout = 5;
    }

    private void doColorEffects() {
        if (getTicksExisted() % 4 != 0) {
            return;
        }


        if (!this.occupiedConnections.isEmpty()) {
            this.occupiedConnections.clear();
            markForUpdate();
        }

        if (lensEffectTimeout > 0) {
            lensEffectTimeout--;
        } else {
            return;
        }

        Vector3 thisVec = new Vector3(this).add(0.5, 0.5, 0.5);
        List<BlockPos> linked = getLinkedPositions();
        float str = (1F / ((float) linked.size())) * 0.7F;

        for (BlockPos linkedTo : linked) {
            Vector3 to = new Vector3(linkedTo).add(0.5, 0.5, 0.5);
            RaytraceAssist rta = new RaytraceAssist(thisVec, to).includeEndPoint();
            if (colorType.getType() == LensColorType.TargetType.BLOCK || colorType.getType() == LensColorType.TargetType.ANY) {
                boolean clear = rta.isClear(world);
                if (!clear && rta.positionHit() != null) {
                    BlockPos posHit = rta.positionHit();

                    BlockState stateHit = world.getBlockState(posHit);
                    colorType.blockInBeam(world, posHit, stateHit, str);

                    this.occupiedConnections.add(posHit);
                } else {
                    this.occupiedConnections.add(linkedTo);
                }
            }
            if (colorType.getType() == LensColorType.TargetType.ENTITY || colorType.getType() == LensColorType.TargetType.ANY) {
                rta.setCollectEntities(0.5);
                rta.isClear(world);
                List<Entity> found = rta.collectedEntities(world);
                found.forEach(e -> colorType.entityInBeam(thisVec, to, e, str));
                for (Entity entity : found) {
                    colorType.entityInBeam(thisVec, to, entity, str);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playColorEffects() {
        Vector3 at = new Vector3(this).add(0.5, 0.5, 0.5);
        Color lensColor = this.colorType.getColor();

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this)
                        .add(0.2, 0.2, 0.2)
                        .add(rand.nextFloat() * 0.6, rand.nextFloat() * 0.6, rand.nextFloat() * 0.6))
                .color(VFXColorFunction.constant(lensColor))
                .setScaleMultiplier(0.1F + rand.nextFloat() * 0.15F);

        if (getTicksExisted() % 40 == 0) {
            for (BlockPos connected : this.occupiedConnections) {
                Vector3 to = new Vector3(connected).add(0.5, 0.5, 0.5);
                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(at)
                        .setup(to, 0.6, 0.6)
                        .color(VFXColorFunction.constant(lensColor));
            }
        }
    }

    public LensColorType setColorType(@Nullable LensColorType colorType) {
        if (this.getColorType() == colorType) {
            return colorType;
        }
        LensColorType prev = this.getColorType();
        this.colorType = colorType;
        this.markForUpdate();
        return prev;
    }

    @Nullable
    public LensColorType getColorType() {
        return colorType;
    }

    public Direction getPlacedAgainst() {
        BlockState state = world.getBlockState(getPos());
        if (!(state.getBlock() instanceof BlockLens)) {
            return Direction.DOWN;
        }
        return state.get(BlockLens.PLACED_AGAINST);
    }

    @Override
    public boolean isSingleLink() {
        return true;
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(@Nullable CrystalAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.attributes = CrystalAttributes.getCrystalAttributes(compound);
        if (compound.contains("colorType")) {
            this.colorType = LensColorType.byName(new ResourceLocation(compound.getString("colorType")));
        } else {
            this.colorType = null;
        }
        this.occupiedConnections = NBTHelper.readList(compound, "occupiedConnections", Constants.NBT.TAG_COMPOUND,
                nbt -> NBTHelper.readBlockPosFromNBT((CompoundNBT) nbt));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.attributes != null) {
            this.attributes.store(compound);
        }
        if (this.colorType != null) {
            compound.putString("colorType", this.colorType.getName().toString());
        }
        NBTHelper.writeList(compound, "occupiedConnections", this.occupiedConnections,
                pos -> NBTHelper.writeBlockPosToNBT(pos, new CompoundNBT()));
    }

    @Nonnull
    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new StarlightTransmissionLens(at, this.attributes);
    }
}
