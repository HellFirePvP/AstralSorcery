package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TexturePlane;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.CrystalCalculations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:47
 */
public class TileRitualPedestal extends TileReceiverBaseInventory {

    public static final int MAX_EFFECT_TICK = 63;

    private TransmissionReceiverRitualPedestal cachePedestal = null;

    @SideOnly(Side.CLIENT)
    private TextureSpritePlane spritePlane = null;

    private List<BlockPos> offsetMirrorPositions = new LinkedList<>();

    private boolean dirty = false;
    private boolean doesSeeSky = false;

    private int effectWorkTick = 0; //up to 63
    private boolean working = false;
    private UUID ownerUUID = null;

    public TileRitualPedestal() {
        super(1);
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(worldObj.canSeeSky(getPos()));
        }

        if(dirty) {
            dirty = false;
            TransmissionReceiverRitualPedestal recNode = getUpdateCache();
            if(recNode != null) {
                recNode.updateSkyState(doesSeeSky);
            }
        }

        if(working) {
            if(effectWorkTick < 63) {
                effectWorkTick++;
            }
        } else {
            if(effectWorkTick > 0) {
                effectWorkTick--;
            }
        }

        if(worldObj.isRemote && working) {
            int tick = getEffectWorkTick();
            float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);
            int chance = 15 + (int) ((1F - percRunning) * 50);
            if(EffectHandler.STATIC_EFFECT_RAND.nextInt(chance) == 0) {
                Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
                MiscUtils.applyRandomOffset(from, EffectHandler.STATIC_EFFECT_RAND, 0.05F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(7), from, 1.5F);
                lightbeam.setMaxAge(64);
            }
            if(EffectHandler.STATIC_EFFECT_RAND.nextInt(chance * 2) == 0) {
                Vector3 from = new Vector3(this).add(0.5, 0.1, 0.5);
                MiscUtils.applyRandomOffset(from, EffectHandler.STATIC_EFFECT_RAND, 2F);
                from.setY(getPos().getY() - 0.6 + 1 * EffectHandler.STATIC_EFFECT_RAND.nextFloat() * (EffectHandler.STATIC_EFFECT_RAND.nextBoolean() ? 1 : -1));
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(5 + EffectHandler.STATIC_EFFECT_RAND.nextInt(3)), from, 1.3F);
                lightbeam.setMaxAge(64);
            }
            for (BlockPos expMirror : offsetMirrorPositions) {
                if(ticksExisted % 32 == 0) {
                    Vector3 source = new Vector3(this).add(0.5, 0.75, 0.5);
                    Vector3 to = new Vector3(this).add(expMirror).add(0.5, 0.5, 0.5);
                    EffectHandler.getInstance().lightbeam(to, source, 0.8);
                }
            }
        }
    }

    //Affects only client, i'll keep the method here for misc reasons tho.
    public int getEffectWorkTick() {
        return effectWorkTick;
    }

    @Nullable
    public TransmissionReceiverRitualPedestal getUpdateCache() {
        if(cachePedestal == null) {
            cachePedestal = tryGetNode();
        }
        if(cachePedestal != null) {
            if(!cachePedestal.getPos().equals(getPos())) {
                cachePedestal = null;
            }
        }
        return cachePedestal;
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
            markDirty();
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureSpritePlane getHaloEffectSprite() {
        if(spritePlane == null || spritePlane.canRemove()) { //Refresh.
            spritePlane = EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteHalo, Axis.Y_AXIS);
            spritePlane.setPosition(new Vector3(this).add(0.5, 0.1, 0.5));
            spritePlane.setAlphaOverDistance(true);
            spritePlane.setNoRotation(0);
            spritePlane.setRefreshFunc(() -> !isInvalid() && working);
            spritePlane.setScale(7F);
        }
        return spritePlane;
    }

    @Override
    protected void onInventoryChanged() {
        if(!worldObj.isRemote) {
            ItemStack in = getStackInSlot(0);
            if(in != null && in.getItem() != null &&
                    in.getItem() instanceof ItemTunedCrystalBase) {
                CrystalProperties properties = CrystalProperties.getCrystalProperties(in);
                Constellation tuned = ItemTunedCrystalBase.getConstellation(in);
                Constellation trait = ItemTunedCrystalBase.getTrait(in);
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateCrystalProperties(properties, tuned, trait);
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Updated inventory and tried to update pedestal state.");
                    AstralSorcery.log.warn("[AstralSorcery] Tried to find receiver node at dimId=" + worldObj.provider.getDimension() + " pos=" + getPos() + " - couldn't find it.");
                }
            } else {
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateCrystalProperties(null, null, null);
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Updated inventory and tried to update pedestal state.");
                    AstralSorcery.log.warn("[AstralSorcery] Tried to find receiver node at dimId=" + worldObj.provider.getDimension() + " pos=" + getPos() + " - couldn't find it.");
                }
            }
            markForUpdate();
        }
    }

    private void updatePositions(Collection<BlockPos> offsetMirrors) {
        offsetMirrorPositions.clear();
        offsetMirrorPositions.addAll(offsetMirrors);
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.working = compound.getBoolean("working");
        this.ownerUUID = compound.getUniqueId("owner");

        offsetMirrorPositions.clear();
    }

    @Override
    public void readNetNBT(NBTTagCompound compound) {
        offsetMirrorPositions.clear();
        NBTTagList listPos = compound.getTagList("positions", 10);
        for (int i = 0; i < listPos.tagCount(); i++) {
            offsetMirrorPositions.add(NBTUtils.readBlockPosFromNBT(listPos.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("working", working);
        compound.setUniqueId("owner", ownerUUID);
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : offsetMirrorPositions) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, cmp);
            listPositions.appendTag(cmp);
        }
        compound.setTag("positions", listPositions);
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public String getInventoryName() {
        return getUnLocalizedDisplayName();
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockRitualPedestal.name";
    }

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverRitualPedestal(at, doesSeeSky);
    }

    public void setOwner(UUID uniqueID) {
        this.ownerUUID = uniqueID;
        markForUpdate();
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world) {
        return world.getPlayerEntityByUUID(getOwnerUUID());
    }

    public static class TransmissionReceiverRitualPedestal extends SimpleTransmissionReceiver {

        private int ticksTicking = 0;

        private boolean doesSeeSky;
        private Constellation channeling, trait;
        private CrystalProperties properties;
        private int channeled = 0;

        private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

        private double collectionChannelBuffer = 0D, collectionTraitBuffer = 0D;
        private boolean doesWorkBuffer = false;

        public TransmissionReceiverRitualPedestal(@Nonnull BlockPos thisPos, boolean doesSeeSky) {
            super(thisPos);
            this.doesSeeSky = doesSeeSky;
        }

        //TODO add mirror things.

        @Override
        public void update(World world) {
            ticksTicking++;

            if(channeling != null && properties != null) {
                if(ticksTicking % 20 == 0) {
                    WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
                    List<BlockPos> toNodes = getSources();
                    for (BlockPos pos : new LinkedList<>(offsetMirrors.keySet())) {
                        BlockPos act = pos.add(getPos());
                        if(!toNodes.contains(act)) {
                            offsetMirrors.put(pos, false);
                            continue;
                        }

                        IPrismTransmissionNode node = handle.getTransmissionNode(act);
                        if(node == null) continue;

                        boolean found = false;
                        for (NodeConnection<IPrismTransmissionNode> n : node.queryNext(handle)) {
                            if(n.getTo().equals(getPos())) {
                                offsetMirrors.put(pos, n.canConnect());
                                found = true;
                            }
                        }
                        if(!found) {
                            offsetMirrors.put(pos, false);
                        }
                    }
                }

                if(doesSeeSky) {
                    double perc = 0.2D + (0.8D * CelestialHandler.calcDaytimeDistribution(world));
                    double collect = perc * CrystalCalculations.getCollectionAmt(properties, CelestialHandler.getCurrentDistribution(channeling));
                    collectionChannelBuffer += collect / 2D;
                }
                if(collectionChannelBuffer > 0) {
                    ConstellationEffect ce = channeling.queryEffect();
                    doMainEffect(world, ce, trait, trait != null && collectionTraitBuffer > 0);

                    flagAsWorking(world);

                    if(trait != null && collectionTraitBuffer > 0) {
                        doTraitEffect(world, ce);
                    }
                } else {
                    flagAsInactive(world);
                }
            } else {
                flagAsInactive(world);
            }
        }

        private void doTraitEffect(World world, ConstellationEffect ce) {
            double maxDrain = CrystalCalculations.getMaxRitualEffect(properties);
            maxDrain /= getCollectedBackmirrors();

            int times = MathHelper.floor_double(collectionChannelBuffer / maxDrain);
            boolean consumeCompletely = times == 0;

            for (int i = 0; i <= times; i++) {
                float perc;
                if(collectionTraitBuffer >= maxDrain) {
                    collectionTraitBuffer -= maxDrain;
                    perc = ((float) maxDrain) / CrystalCalculations.MAX_RITUAL_EFFECT;
                } else if(consumeCompletely) {
                    collectionTraitBuffer = 0;
                    perc = ((float) collectionTraitBuffer) / CrystalCalculations.MAX_RITUAL_EFFECT;
                } else {
                    continue;
                }

                if(ce != null) {
                    ce.playTraitEffect(world, getPos(), trait, perc);
                }
            }
        }

        private void doMainEffect(World world, ConstellationEffect ce, @Nullable Constellation trait, boolean mayDoTrait) {
            double maxDrain = CrystalCalculations.getMaxRitualEffect(properties);
            maxDrain /= getCollectedBackmirrors();

            int times = MathHelper.floor_double(collectionChannelBuffer / maxDrain);
            boolean consumeCompletely = times == 0;

            for (int i = 0; i <= times; i++) {
                float perc;
                if(collectionChannelBuffer >= maxDrain) {
                    collectionChannelBuffer -= maxDrain;
                    perc = ((float) maxDrain) / CrystalCalculations.MAX_RITUAL_EFFECT;
                } else if(consumeCompletely) {
                    collectionChannelBuffer = 0;
                    perc = ((float) collectionChannelBuffer) / CrystalCalculations.MAX_RITUAL_EFFECT;
                } else {
                    continue;
                }

                if(ce != null) {
                    ce.playMainEffect(world, getPos(), perc, mayDoTrait, trait);
                }
            }
        }

        private int getCollectedBackmirrors() {
            int amt = 1;
            for (boolean f : offsetMirrors.values()) if(f) amt++;
            return amt;
        }

        private void flagAsInactive(World world) {
            if(doesWorkBuffer) {
                doesWorkBuffer = false;
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if(ped != null) {
                    ped.working = false;
                    ped.markForUpdate();
                }

                clearAllMirrorPositions(world);
            }
        }

        private void flagAsWorking(World world) {
            if(!doesWorkBuffer) {
                doesWorkBuffer = true;
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if(ped != null) {
                    ped.working = true;
                    ped.markForUpdate();
                }
            }
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount) {
            if(channeling != null) {
                if(channeling == type) {
                    collectionChannelBuffer += amount;
                    return;
                }
                if(trait != null && trait == type) {
                    collectionTraitBuffer += amount;
                }
            }
        }

        public void addMirrorPosition(World world, BlockPos offset) {
            this.offsetMirrors.put(offset, false);
            updateMirrorPositions(world);
        }

        public void clearAllMirrorPositions(World world) {
            this.offsetMirrors.clear();
            updateMirrorPositions(world);
        }

        @Override
        public boolean needsUpdate() {
            return true;
        }

        @Override
        public void postLoad(World world) {
            updateMirrorPositions(world);
        }

        public void updateMirrorPositions(World world) {
            TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
            if(ped != null) {
                ped.updatePositions(offsetMirrors.keySet());
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);

            doesSeeSky = compound.getBoolean("doesSeeSky");
            channeled = compound.getInteger("channeled");
            properties = CrystalProperties.readFromNBT(compound);
            channeling = Constellation.readFromNBT(compound, Constellation.getDefaultSaveKey() + "Normal");
            trait = Constellation.readFromNBT(compound, Constellation.getDefaultSaveKey() + "Trait");

            offsetMirrors.clear();
            NBTTagList listPos = compound.getTagList("positions", 10);
            for (int i = 0; i < listPos.tagCount(); i++) {
                offsetMirrors.put(NBTUtils.readBlockPosFromNBT(listPos.getCompoundTagAt(i)), false);
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);

            compound.setBoolean("doesSeeSky", doesSeeSky);
            compound.setInteger("channeled", channeled);

            NBTTagList listPositions = new NBTTagList();
            for (BlockPos pos : offsetMirrors.keySet()) {
                NBTTagCompound cmp = new NBTTagCompound();
                NBTUtils.writeBlockPosToNBT(pos, cmp);
                listPositions.appendTag(cmp);
            }
            compound.setTag("positions", listPositions);

            if(properties != null) {
                properties.writeToNBT(compound);
            }
            if(channeling != null) {
                channeling.writeToNBT(compound, Constellation.getDefaultSaveKey() + "Normal");
            }
            if(trait != null) {
                trait.writeToNBT(compound, Constellation.getDefaultSaveKey() + "Trait");
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new PedestalReceiverProvider();
        }

        public void update(boolean doesSeeSky, Constellation bufferChanneling, Constellation trait) {
            this.doesSeeSky = doesSeeSky;
            this.channeling = bufferChanneling;
            this.trait = trait;
        }

        public void updateSkyState(boolean doesSeeSky) {
            this.doesSeeSky = doesSeeSky;
        }

        public void updateCrystalProperties(CrystalProperties properties, Constellation channeling, Constellation trait) {
            this.properties = properties;
            this.channeling = channeling;
            this.trait = trait;
        }

    }

    public static class PedestalReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverRitualPedestal provideEmptyNode() {
            return new TransmissionReceiverRitualPedestal(null, false);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverRitualPedestal";
        }

    }

}
