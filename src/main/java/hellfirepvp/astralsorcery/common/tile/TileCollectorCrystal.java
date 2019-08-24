/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

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
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 19:55
 */
public class TileCollectorCrystal extends TileSourceBase<SimpleTransmissionSourceNode> implements CrystalAttributeTile, ConstellationTile {

    private static final BlockPos[] OFFSETS_LIQUID_STARLIGHT = new BlockPos[] {
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
    private CollectorCrystalType collectorType;
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
            playEffects();
        }
    }

    private void playEffects() {
        //TODO effects
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

        this.markForUpdate();
    }

    @Nullable
    @Override
    protected StructureType getRequiredStructureType() {
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
        this.crystalAttributes = CrystalAttributes.getCrystalAttributes(compound);
        this.collectorType = NBTHelper.readOptional(compound, "collectorType", (nbt) -> CollectorCrystalType.values()[nbt.getInt("collectorType")]);
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
        NBTHelper.writeOptional(compound, "collectorType", this.collectorType, (nbt, type) -> nbt.putInt("collectorType", type.ordinal()));
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
