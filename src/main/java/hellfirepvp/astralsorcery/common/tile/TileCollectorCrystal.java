/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.network.TileSourceBase;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
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
public class TileCollectorCrystal extends TileSourceBase<SimpleTransmissionSourceNode> {

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
    private CrystalProperties crystalProperties;
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

    public CrystalProperties getCrystalProperties() {
        return crystalProperties;
    }

    public IWeakConstellation getConstellationType() {
        return constellationType;
    }

    public IMinorConstellation getConstellationTrait() {
        return constellationTrait;
    }

    public CollectorCrystalType getCollectorType() {
        return collectorType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void updateData(IWeakConstellation constellationType, IMinorConstellation constellationTrait, CrystalProperties properties, UUID playerUUID, CollectorCrystalType collectorType) {
        this.constellationType = constellationType;
        this.constellationTrait = constellationTrait;
        this.crystalProperties = properties;
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
        this.collectorType = NBTHelper.readOptional(compound, "collectorType", (nbt) -> CollectorCrystalType.values()[nbt.getInt("collectorType")]);
        this.crystalProperties = NBTHelper.readOptional(compound, "crystalProperties", CrystalProperties::readFromNBT);
        this.playerUUID = NBTHelper.readOptional(compound, "playerUUID", (nbt) -> nbt.getUniqueId("playerUUID"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        NBTHelper.writeOptional(compound, "constellationType", this.constellationType, (nbt, cst) -> cst.writeToNBT(nbt));
        NBTHelper.writeOptional(compound, "constellationTrait", this.constellationTrait, (nbt, cst) -> cst.writeToNBT(nbt));
        NBTHelper.writeOptional(compound, "collectorType", this.collectorType, (nbt, type) -> nbt.putInt("collectorType", type.ordinal()));
        NBTHelper.writeOptional(compound, "crystalProperties", this.crystalProperties, (nbt, prop) -> prop.writeToNBT(nbt));
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
