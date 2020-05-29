/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.source.FXSource;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXOrbitalCollector;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.ConstellationTile;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.network.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 19:55
 */
public class TileCollectorCrystal extends TileSourceBase<SimpleTransmissionSourceNode> implements CrystalAttributeTile, ConstellationTile {

    public static final BlockPos[] OFFSETS_LIQUID_STARLIGHT = new BlockPos[] {
            new BlockPos(-1, -4, -1),
            new BlockPos( 0, -4, -1),
            new BlockPos( 1, -4, -1),
            new BlockPos( 1, -4,  0),
            new BlockPos( 1, -4,  1),
            new BlockPos( 0, -4,  1),
            new BlockPos(-1, -4,  1),
            new BlockPos(-1, -4,  0),
    };

    private UUID playerUUID = null;
    private CrystalAttributes crystalAttributes;
    private CollectorCrystalType collectorType = CollectorCrystalType.ROCK_CRYSTAL;
    private IWeakConstellation constellationType;
    private IMinorConstellation constellationTrait;

    private Object[] effectOrbitals = new Object[4];

    public TileCollectorCrystal() {
        super(TileEntityTypesAS.COLLECTOR_CRYSTAL);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote()) {
            this.doesSeeSky();
            this.hasMultiblock();
        } else {
            this.playEffects();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        Vector3 thisPos = new Vector3(this).add(0.5F, 0.5F, 0.5F);
        Vector3 particlePos = thisPos.clone();
        MiscUtils.applyRandomOffset(particlePos, rand, 0.75F);

        if (this.isEnhanced() &&
                this.doesSeeSky() &&
                this.getCollectorType() == CollectorCrystalType.CELESTIAL_CRYSTAL &&
                this.getAttunedConstellation() != null) {

            Color c = this.getAttunedConstellation().getConstellationColor();

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(particlePos)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .setAlphaMultiplier(0.8F)
                    .color(VFXColorFunction.constant(c))
                    .setMaxAge(20 + rand.nextInt(10));

            for (int i = 0; i < this.effectOrbitals.length; i++) {
                FXOrbitalCollector fxSource = (FXOrbitalCollector) this.effectOrbitals[i];
                if (fxSource == null) {
                    FXSource<?, ?> src = new FXOrbitalCollector(new Vector3(this).add(0.5F, 0.5F, 0.5F), c)
                            .setOrbitAxis(Vector3.random())
                            .setOrbitRadius(0.8F + rand.nextFloat() * 0.5F)
                            .setTicksPerRotation(40 + rand.nextInt(30));
                    EffectHelper.spawnSource(src);
                    this.effectOrbitals[i] = src;
                } else {
                    if (fxSource.canRemove() && fxSource.isRemoved()) {
                        this.effectOrbitals[i] = null;
                    }
                }
            }

            BlockPos starlightSource = MiscUtils.getRandomEntry(OFFSETS_LIQUID_STARLIGHT, rand).add(this.getPos());

            Vector3 from = new Vector3(starlightSource).add(rand.nextFloat(), 0.85F, rand.nextFloat());
            Vector3 motion = thisPos.clone().subtract(from).normalize().multiply(0.08F);
            Color particleColor = MiscUtils.eitherOf(rand, Color.WHITE, c, c.brighter());

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(from)
                    .setMotion(motion)
                    .alpha(VFXAlphaFunction.proximity(thisPos::clone, 2F).andThen(VFXAlphaFunction.FADE_OUT))
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.constant(particleColor))
                    .setMaxAge(30 + rand.nextInt(10));

            if (rand.nextInt(80) == 0) {
                EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                        .spawn(thisPos)
                        .makeDefault(from)
                        .color(VFXColorFunction.constant(c));
            }
        } else {
            if (rand.nextBoolean()) {
                Color c = this.getCollectorType().getDisplayColor();

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(particlePos)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                        .setAlphaMultiplier(0.8F)
                        .color(VFXColorFunction.constant(c))
                        .setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    public boolean isEnhanced() {
        return this.getCollectorType() == CollectorCrystalType.CELESTIAL_CRYSTAL && this.hasMultiblock();
    }

    public boolean isPlayerMade() {
        return this.getPlayerUUID() != null;
    }

    @Override
    public IWeakConstellation getAttunedConstellation() {
        return this.constellationType;
    }

    @Override
    public boolean setAttunedConstellation(@Nullable IWeakConstellation cst) {
        if (cst != this.constellationType) {
            markForUpdate();
        }
        this.constellationType = cst;
        return true;
    }

    @Override
    public IMinorConstellation getTraitConstellation() {
        return this.constellationTrait;
    }

    @Override
    public boolean setTraitConstellation(@Nullable IMinorConstellation cst) {
        if (cst != this.constellationTrait) {
            markForUpdate();
        }
        this.constellationTrait = cst;
        return true;
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes() {
        return crystalAttributes;
    }

    @Override
    public void setAttributes(@Nullable CrystalAttributes attributes) {
        if (this.crystalAttributes == null && attributes == null) {
            return;
        }

        // this.crystalAttributes null check is covered in .equals
        if (attributes == null ||
                !attributes.equals(this.crystalAttributes)) {
            markForUpdate();
        }
        this.crystalAttributes = attributes;
    }

    public CollectorCrystalType getCollectorType() {
        return collectorType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void updateData(UUID playerUUID, CollectorCrystalType collectorType) {
        this.playerUUID = playerUUID;
        this.collectorType = collectorType;
        if (this.collectorType == null) {
            this.collectorType = CollectorCrystalType.ROCK_CRYSTAL;
        }

        this.markForUpdate();
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_ENHANCED_COLLECTOR_CRYSTAL;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.constellationType = NBTHelper.readOptional(compound, "constellationType", (nbt) -> {
            IConstellation cst = IConstellation.readFromNBT(nbt);
            if (cst instanceof IWeakConstellation) {
                return (IWeakConstellation) cst;
            }
            return null;
        });
        this.constellationTrait = NBTHelper.readOptional(compound, "constellationTrait", (nbt) -> {
            IConstellation cst = IConstellation.readFromNBT(nbt);
            if (cst instanceof IMinorConstellation) {
                return (IMinorConstellation) cst;
            }
            return null;
        });
        setAttributes(CrystalAttributes.getCrystalAttributes(compound));
        this.crystalAttributes = CrystalAttributes.getCrystalAttributes(compound);;
        this.collectorType = NBTHelper.readEnum(compound, "collectorType", CollectorCrystalType.class);
        this.playerUUID = NBTHelper.readOptional(compound, "playerUUID", (nbt) -> nbt.getUniqueId("playerUUID"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (getAttributes() != null) {
            getAttributes().store(compound);
        }
        NBTHelper.writeOptional(compound, "constellationType", this.constellationType, (nbt, cst) -> cst.writeToNBT(nbt));
        NBTHelper.writeOptional(compound, "constellationTrait", this.constellationTrait, (nbt, cst) -> cst.writeToNBT(nbt));
        NBTHelper.writeEnum(compound, "collectorType", this.collectorType);
        NBTHelper.writeOptional(compound, "playerUUID", this.playerUUID, (nbt, uuid) -> nbt.putUniqueId("playerUUID", uuid));
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return BOX.grow(1).offset(getPos());
    }

    @Nonnull
    @Override
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource();
    }

    @Nonnull
    @Override
    public SimpleTransmissionSourceNode provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }
}
