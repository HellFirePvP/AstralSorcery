/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.CrystalCalculations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:47
 */
public class TileRitualPedestal extends TileReceiverBaseInventory implements IMultiblockDependantTile {

    public static final int MAX_EFFECT_TICK = 63;

    private TransmissionReceiverRitualPedestal cachePedestal = null;

    private Object spritePlane = null;

    private List<BlockPos> offsetMirrorPositions = new LinkedList<>();

    private boolean dirty = false;
    private boolean doesSeeSky = false, hasMultiblock = false;
    private BlockPos ritualLink = null;

    private int effectWorkTick = 0; //up to 63
    private boolean working = false;
    private UUID ownerUUID = null;

    public TileRitualPedestal() {
        super(1, EnumFacing.UP);
    }

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if((ticksExisted & 15) == 0) {
                updateSkyState(world.canSeeSky(getPos()));

                updateLinkTile();
            }

            if((ticksExisted & 31) == 0) {
                updateMultiblockState();
            }

            if(dirty) {
                dirty = false;
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateSkyState(doesSeeSky);
                    recNode.updateMultiblockState(hasMultiblock);
                    recNode.updateLink(world, ritualLink);

                    recNode.markDirty(world);
                }
                markForUpdate();
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

        if(world.isRemote && working) {
            float alphaDaytime = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
            alphaDaytime *= 0.8F;
            boolean isDay = alphaDaytime <= 1E-4;

            int tick = getEffectWorkTick();
            float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);
            int chance = 15 + (int) ((1F - percRunning) * 50);
            if(rand.nextInt(chance) == 0) {
                Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 0.05F);
                EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(6), from, 1.5F);
                lightbeam.setAlphaMultiplier(0.5F + (0.5F * alphaDaytime));
                lightbeam.setMaxAge(64);
            }
            if(ritualLink != null) {
                if(rand.nextBoolean()) {
                    Vector3 at = new Vector3(this).add(0, 0.1, 0);
                    at.add(rand.nextFloat() * 0.5 + 0.25, 0, rand.nextFloat() * 0.5 + 0.25);
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                    p.setAlphaMultiplier(0.7F).setColor(Color.WHITE);
                    p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                    p.gravity(0.09).scale(0.3F + rand.nextFloat() * 0.1F);
                }
            }
            if(shouldDoAdditionalEffects() && !isDay) {
                if(rand.nextInt(chance * 2) == 0) {
                    Vector3 from = new Vector3(this).add(0.5, 0.1, 0.5);
                    MiscUtils.applyRandomOffset(from, rand, 2F);
                    from.setY(getPos().getY() - 0.6 + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
                    EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(5 + rand.nextInt(3)), from, 1.3F);
                    lightbeam.setAlphaMultiplier(alphaDaytime);
                    if(this.getDisplayConstellation() != null) {
                        lightbeam.setColorOverlay(getDisplayConstellation().getConstellationColor());
                    }
                    lightbeam.setMaxAge(64);
                }
                if(this.getDisplayConstellation() != null) {
                    if(rand.nextInt(chance * 8) == 0) {
                        List<Vector3> positions = MiscUtils.getCirclePositions(new Vector3(pos).add(0.5, -0.3, 0.5),
                                Vector3.RotAxis.Y_AXIS, 1 + rand.nextFloat() * 0.5, 40);
                        Color c = getDisplayConstellation().getConstellationColor();
                        for (Vector3 v : positions) {
                            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                            Color col = c;
                            if(rand.nextBoolean()) {
                                col = col.darker();
                                if(rand.nextBoolean()) {
                                    col = col.darker();
                                }
                            }
                            p.gravity(0.004).scale(0.3F + rand.nextFloat() * 0.2F).setMaxAge(50 + rand.nextInt(40));
                            p.setColor(col).motion(-0.02 + rand.nextFloat() * 0.04, rand.nextFloat() * 0.07, -0.02 + rand.nextFloat() * 0.04);
                        }
                    }
                    if(rand.nextInt(chance * 7) == 0) {
                        if(!offsetMirrorPositions.isEmpty()) {
                            BlockPos to = offsetMirrorPositions.get(rand.nextInt(offsetMirrorPositions.size()));
                            IWeakConstellation c = getRitualConstellation();
                            Color col = null;
                            if(c != null && c.getConstellationColor() != null) {
                                col = c.getConstellationColor();
                            }
                            AstralSorcery.proxy.fireLightning(getWorld(),
                                    new Vector3(this).add(0.5, 0.8, 0.5),
                                    new Vector3(to).add(getPos()).add(0.5, 0.5, 0.5),
                                    col);
                        }
                    }
                }
            }
            ItemStack crystal = getInventoryHandler().getStackInSlot(0);
            if(!crystal.isEmpty() && crystal.getItem() instanceof ItemTunedCrystalBase) {
                IWeakConstellation ch = ItemTunedCrystalBase.getMainConstellation(crystal);
                if(ch != null) {
                    ConstellationEffect ce = ConstellationEffectRegistry.clientRenderInstance(ch);
                    if(ce != null) {
                        if(ritualLink != null) {
                            ce.playClientEffect(world, ritualLink, this, percRunning, shouldDoAdditionalEffects());
                        }
                        ce.playClientEffect(world, getPos(), this, percRunning, shouldDoAdditionalEffects());
                    }
                }
            }
            for (BlockPos expMirror : offsetMirrorPositions) {
                if(ticksExisted % 32 == 0) {
                    Vector3 source = new Vector3(this).add(0.5, 0.75, 0.5);
                    Vector3 to = new Vector3(this).add(expMirror).add(0.5, 0.5, 0.5);
                    EffectHandler.getInstance().lightbeam(to, source, 0.8);
                    if(ritualLink != null) {
                        source = new Vector3(this).add(0.5, 5.5, 0.5);
                        EffectLightbeam beam = EffectHandler.getInstance().lightbeam(to, source, 0.8);
                        beam.setColorOverlay(Color.getHSBColor(rand.nextFloat() * 360F, 1F, 1F));
                    }
                }
            }
        }
    }

    private void updateLinkTile() {
        boolean hasLink = ritualLink != null;
        BlockPos link = getPos().add(0, 5, 0);
        TileRitualLink linkTile = MiscUtils.getTileAt(world, link, TileRitualLink.class, true);
        boolean hasLinkNow;
        if(linkTile != null) {
            this.ritualLink = linkTile.getLinkedTo();
            hasLinkNow = this.ritualLink != null;
        } else {
            hasLinkNow = false;
            this.ritualLink = null;
        }
        if(hasLink != hasLinkNow) {
            markForUpdate();
            flagDirty();
        }
    }

    public boolean isWorking() {
        return working;
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternRitualPedestal;
    }

    private void updateMultiblockState() {
        boolean found = MultiBlockArrays.patternRitualPedestal.matches(world, getPos());
        if(found && !checkAirBox(-2, 0, -2, 2, 2, 2)) found = false;
        if(found && !checkAirBox(-3, 0, -1, 3, 2, 1)) found = false;
        if(found && !checkAirBox(-1, 0, -3, 1, 2, 3)) found = false;

        boolean update = hasMultiblock != found;
        this.hasMultiblock = found;
        if(update) {
            markForUpdate();
            flagDirty();
        }
    }

    private boolean checkAirBox(int ox, int oy, int oz, int tx, int ty, int tz) {
        int lx, ly, lz;
        int hx, hy, hz;
        if(ox < tx) {
            lx = ox;
            hx = tx;
        } else {
            lx = tx;
            hx = ox;
        }
        if(oy < ty) {
            ly = oy;
            hy = ty;
        } else {
            ly = ty;
            hy = oy;
        }
        if(oz < tz) {
            lz = oz;
            hz = tz;
        } else {
            lz = tz;
            hz = oz;
        }

        for (int xx = lx; xx <= hx; xx++) {
            for (int zz = lz; zz <= hz; zz++) {
                for (int yy = ly; yy <= hy; yy++) {
                    if(xx == 0 && yy == 0 && zz == 0) continue;
                    BlockPos at = new BlockPos(xx, yy, zz).add(getPos());
                    if(!getWorld().isAirBlock(at)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Affects only client, i'll keep the method here for misc reasons tho.
    public int getEffectWorkTick() {
        return effectWorkTick;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public IWeakConstellation getDisplayConstellation() {
        if(offsetMirrorPositions.size() != TransmissionReceiverRitualPedestal.MAX_MIRROR_COUNT)
            return null;
        return getRitualConstellation();
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldDoAdditionalEffects() {
        return working && offsetMirrorPositions.size() > 0;
    }

    @Nullable
    public IWeakConstellation getRitualConstellation() {
        ItemStack crystal = getInventoryHandler().getStackInSlot(0);
        if(!crystal.isEmpty() && crystal.getItem() instanceof ItemTunedCrystalBase) {
            return ItemTunedCrystalBase.getMainConstellation(crystal);
        }
        return null;
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
            flagDirty();
        }
    }

    @Override
    public void onLoad() {
        if(!world.isRemote) {
            TransmissionReceiverRitualPedestal ped = getUpdateCache();
            if(ped != null) {
                offsetMirrorPositions.clear();
                offsetMirrorPositions.addAll(ped.offsetMirrors.keySet());
                flagDirty();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureSpritePlane getHaloEffectSprite() {
        TextureSpritePlane spr = (TextureSpritePlane) spritePlane;
        if(spr == null || spr.canRemove() || spr.isRemoved()) { //Refresh.
            spr = EffectHandler.getInstance().textureSpritePlane(SpriteLibrary.spriteHalo1, Vector3.RotAxis.Y_AXIS.clone());
            spr.setPosition(new Vector3(this).add(0.5, 0.06, 0.5));
            spr.setAlphaOverDistance(true);
            spr.setNoRotation(45);
            spr.setRefreshFunc(() -> {
                if(isInvalid() || !working) {
                    return false;
                }
                if(this.getWorld().provider == null || Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().world.provider == null) {
                    return false;
                }
                return this.getWorld().provider.getDimension() == Minecraft.getMinecraft().world.provider.getDimension();
            });
            spr.setScale(6.5F);
            spritePlane = spr;
        }
        return spr;
    }

    @Override
    protected void onInventoryChanged(int slotChanged) {
        if(!world.isRemote) {
            ItemStack in = getInventoryHandler().getStackInSlot(0);
            if(!in.isEmpty() && in.getItem() instanceof ItemTunedCrystalBase) {
                CrystalProperties properties = CrystalProperties.getCrystalProperties(in);
                IWeakConstellation tuned = ItemTunedCrystalBase.getMainConstellation(in);
                IMinorConstellation trait = ItemTunedCrystalBase.getTrait(in);
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateCrystalProperties(world, properties, tuned, trait);
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Updated inventory and tried to update pedestal state.");
                    AstralSorcery.log.warn("[AstralSorcery] Tried to find receiver node at dimId=" + world.provider.getDimension() + " pos=" + getPos() + " - couldn't find it.");
                }
            } else {
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateCrystalProperties(world, null, null, null);
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Updated inventory and tried to update pedestal state.");
                    AstralSorcery.log.warn("[AstralSorcery] Tried to find receiver node at dimId=" + world.provider.getDimension() + " pos=" + getPos() + " - couldn't find it.");
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
        if(compound.hasKey("ownerMost")) {
            this.ownerUUID = compound.getUniqueId("owner");
        } else {
            this.ownerUUID = UUID.randomUUID();
        }
        this.doesSeeSky = compound.getBoolean("seesSky");
        this.hasMultiblock = compound.getBoolean("hasMultiblock");

        if(compound.hasKey("ritualLinkPos")) {
            this.ritualLink = NBTUtils.readBlockPosFromNBT(compound.getCompoundTag("ritualLinkPos"));
        } else {
            this.ritualLink = null;
        }

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
        if(ownerUUID != null) {
            compound.setUniqueId("owner", ownerUUID);
        }
        compound.setBoolean("hasMultiblock", hasMultiblock);
        compound.setBoolean("seesSky", doesSeeSky);

        if(ritualLink != null) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(ritualLink, tag);
            compound.setTag("ritualLinkPos", tag);
        }

        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : offsetMirrorPositions) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, cmp);
            listPositions.appendTag(cmp);
        }
        compound.setTag("positions", listPositions);
    }

    public void flagDirty() {
        this.dirty = true;
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockritualpedestal.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverRitualPedestal(at, doesSeeSky);
    }

    public void setOwner(UUID uniqueID) {
        this.ownerUUID = uniqueID;
        markForUpdate();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world) {
        UUID uuid = getOwnerUUID();
        return uuid == null ? null : world.getPlayerEntityByUUID(uuid);
    }

    public static class TransmissionReceiverRitualPedestal extends SimpleTransmissionReceiver {

        private static final int MAX_MIRROR_COUNT = 5;

        //TODO change to higher numbers for release?...
        //Steps between trials: 10 minutes, 25 minutes, 50 minutes, 2 hours, 5 hours
        //private static final int[] secToNext =    new int[] { 12_000, 30_000, 60_000, 144_000, 360_000 };
        private static final int[] secToNext =    new int[] { 10, 10, 6, 10, 10 };
        //private static final int[] chanceToNext = new int[] { 50,     200,    500,    1000,    2000 };
        private static final int[] chanceToNext = new int[] { 2,     2,    2,    2,    2 };

        private static final BlockPos[] possibleOffsets = new BlockPos[] {
                new BlockPos( 4, 2,  0),
                new BlockPos( 4, 2,  1),
                new BlockPos( 3, 2,  2),
                new BlockPos( 2, 2,  3),
                new BlockPos( 1, 2,  4),
                new BlockPos( 0, 2,  4),
                new BlockPos(-1, 2,  4),
                new BlockPos(-2, 2,  3),
                new BlockPos(-3, 2,  2),
                new BlockPos(-4, 2,  1),
                new BlockPos(-4, 2,  0),
                new BlockPos(-4, 2, -1),
                new BlockPos(-3, 2, -2),
                new BlockPos(-2, 2, -3),
                new BlockPos(-1, 2, -4),
                new BlockPos( 0, 2, -4),
                new BlockPos( 1, 2, -4),
                new BlockPos( 2, 2, -3),
                new BlockPos( 3, 2, -2),
                new BlockPos( 4, 2, -1)
        };

        private int ticksTicking = 0;

        private boolean doesSeeSky, hasMultiblock;
        private BlockPos ritualLinkTo = null;
        private IWeakConstellation channeling;
        private IMinorConstellation trait;
        private CrystalProperties properties;
        private int channeled = 0;

        //private TreeCaptureHelper.TreeWatcher tw = null;
        private ConstellationEffect ce;
        private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

        private double collectionChannelBuffer = 0D, collectionTraitBuffer = 0D;
        private boolean doesWorkBuffer = false;

        private int idleBuffer = 0;

        public TransmissionReceiverRitualPedestal(BlockPos thisPos, boolean doesSeeSky) {
            super(thisPos);
            this.doesSeeSky = doesSeeSky;
        }

        @Override
        public void update(World world) {
            ticksTicking++;

            TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
            if(ped != null) {
                ItemStack focus = ped.getInventoryHandler().getStackInSlot(0);
                if(!focus.isEmpty() && focus.getItem() instanceof ItemTunedCrystalBase) {
                    CrystalProperties properties = CrystalProperties.getCrystalProperties(focus);
                    IWeakConstellation tuned = ItemTunedCrystalBase.getMainConstellation(focus);
                    IMinorConstellation trait = ItemTunedCrystalBase.getTrait(focus);
                    updateCrystalProperties(world, properties, tuned, trait);
                } else {
                    updateCrystalProperties(world, null, null, null);
                }
            }

            if(channeling != null && properties != null && hasMultiblock) {
                if(ce == null) {
                    ce = channeling.getRitualEffect(this);
                    /*if(channeling.equals(Constellations.ara)) {
                        tw = new TreeCaptureHelper.TreeWatcher(world.provider.getDimension(), getPos(), CEffectAra.treeRange);
                        if(CEffectAra.enabled) {
                            TreeCaptureHelper.offerWeakWatcher(tw);
                            ((CEffectAra) ce).refTreeWatcher = new WeakReference<>(tw);
                        }
                    }*/
                }
                /*if(channeling != Constellations.ara) {
                    tw = null;
                }*/

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

                if(ticksTicking % 60 == 0) {
                    TileRitualPedestal pedestal = getTileAtPos(world, TileRitualPedestal.class);
                    if(pedestal != null) {
                        if(pedestal.offsetMirrorPositions.size() != offsetMirrors.size()) {
                            updateMirrorPositions(world);
                        }
                    }
                }

                if(doesSeeSky) {
                    double perc = 0.2D + (0.8D * ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world));
                    WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(world);
                    double collect = 0;
                    if(handle != null) {
                        collect = perc * CrystalCalculations.getCollectionAmt(properties, handle.getCurrentDistribution(channeling, (in) -> 0.2F + (0.8F * in)));
                    }
                    collectionChannelBuffer += collect / 2D;
                }
                if(collectionChannelBuffer > 0) {
                    idleBuffer = 0;

                    doMainEffect(world, ce, trait, trait != null && collectionTraitBuffer > 0);

                    if(tryIncrementChannelingTimer())
                        channeled++;

                    flagAsWorking(world);

                    if(trait != null && collectionTraitBuffer > 0) {
                        doTraitEffect(world, ce);
                    }
                } else {
                    if(idleBuffer > 2) {
                        flagAsInactive(world);
                        ce = null;
                    } else {
                        idleBuffer++;
                    }
                }
            } else {
                if(idleBuffer > 2) {
                    flagAsInactive(world);
                    ce = null;
                } else {
                    idleBuffer++;
                }
            }
        }

        private void doTraitEffect(World world, ConstellationEffect ce) {
            if(ce instanceof ConstellationEffectStatus) {
                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(((ConstellationEffectStatus) ce).runTraitEffect(world, to, getCollectedBackmirrors(), trait)) markDirty(world);
                return;
            }

            double maxDrain = 7D;
            maxDrain /= CrystalCalculations.getMaxRitualReduction(properties);
            maxDrain /= Math.max(1, getCollectedBackmirrors() - 1);
            int executeTimes = MathHelper.floor(collectionChannelBuffer / maxDrain);
            boolean consumeCompletely = executeTimes == 0;

            if(ce != null && !consumeCompletely && ce.mayExecuteMultipleTrait()) {
                collectionTraitBuffer = Math.max(0, collectionTraitBuffer - (executeTimes * maxDrain));
                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(ce.playTraitEffectMultiple(world, to, trait, executeTimes)) markDirty(world);
            } else {
                for (int i = 0; i <= executeTimes; i++) {
                    float perc;
                    if(collectionTraitBuffer >= maxDrain) {
                        collectionTraitBuffer -= maxDrain;
                        perc = 1F;
                    } else if(consumeCompletely) {
                        collectionTraitBuffer = 0;
                        perc = (float) ((collectionTraitBuffer) / maxDrain);
                    } else {
                        continue;
                    }

                    if(ce != null) {
                        BlockPos to = getPos();
                        if(ritualLinkTo != null) to = ritualLinkTo;
                        if(ce.playTraitEffect(world, to, trait, perc)) markDirty(world);
                    }
                }
            }
        }

        //TODO occasionally returns with <0?
        private void doMainEffect(World world, ConstellationEffect ce, @Nullable IMinorConstellation trait, boolean mayDoTrait) {
            if(ce instanceof ConstellationEffectStatus) {
                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(((ConstellationEffectStatus) ce).runEffect(world, to, getCollectedBackmirrors(), mayDoTrait, trait)) markDirty(world);
                return;
            }

            double maxDrain = 7D;
            maxDrain /= CrystalCalculations.getMaxRitualReduction(properties);
            maxDrain /= Math.max(1, getCollectedBackmirrors() - 1);
            int executeTimes = MathHelper.floor(collectionChannelBuffer / maxDrain);
            boolean consumeCompletely = executeTimes == 0;

            if(ce != null && !consumeCompletely && ce.mayExecuteMultipleMain()) {
                collectionChannelBuffer = Math.max(0, collectionChannelBuffer - (executeTimes * maxDrain));
                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(ce.playMainEffectMultiple(world, to, executeTimes, mayDoTrait, trait)) markDirty(world);
            } else {
                for (int i = 0; i <= executeTimes; i++) {
                    float perc;
                    if(collectionChannelBuffer >= maxDrain) {
                        collectionChannelBuffer -= maxDrain;
                        perc = 1F;
                    } else if(consumeCompletely) {
                        perc = (float) ((collectionChannelBuffer) / maxDrain);
                        collectionChannelBuffer = 0;
                    } else {
                        continue;
                    }

                    if(ce != null) {
                        BlockPos to = getPos();
                        if(ritualLinkTo != null) to = ritualLinkTo;
                        if(ce.playMainEffect(world, to, perc, mayDoTrait, trait)) markDirty(world);
                    }
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
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if(ped != null) {
                    doesWorkBuffer = false;
                    channeled = 0;

                    ped.working = false;
                    ped.markForUpdate();

                    clearAllMirrorPositions(world);
                }
            }
        }

        private void flagAsWorking(World world) {
            if(!doesWorkBuffer) {
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if(ped != null) {

                    doesWorkBuffer = true;
                    ped.working = true;
                    ped.markForUpdate();
                }
            }
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if(channeling != null && hasMultiblock) {
                if(channeling == type) {
                    collectionChannelBuffer += amount;
                    /*if(collectionChannelBuffer <= 0) {
                        AstralSorcery.log.info("Ended up with < 0 starlight back receive: amount: " + amount);
                    }*/
                    tryGainMirrorPos(world);
                    return;
                }
                if(trait != null && trait == type) {
                    collectionTraitBuffer += amount;
                }
            }
        }

        private boolean tryIncrementChannelingTimer() {
            if(offsetMirrors.size() < 0 || offsetMirrors.size() >= 5) return false;
            if((getCollectedBackmirrors() - 1) < offsetMirrors.size()) return false;
            int step = secToNext[offsetMirrors.size()];
            return channeled <= step;
        }

        private void tryGainMirrorPos(World world) {
            //AstralSorcery.log.info("size: " + offsetMirrors.size());
            //AstralSorcery.log.info("collected: " + (getCollectedBackmirrors() - 1));
            if(offsetMirrors.size() < 0 || offsetMirrors.size() >= 5) return;
            int mirrors = offsetMirrors.size();
            if((getCollectedBackmirrors() - 1) < mirrors) return;
            int step = secToNext[mirrors];
            //AstralSorcery.log.info("step: " + step + ", channeling: " + channeled);
            if(channeled > step) {
                //AstralSorcery.log.info("try find new.");
                if(world.rand.nextInt(chanceToNext[mirrors]) == 0) {
                    findPossibleMirror(world);
                }
            }
        }

        private void findPossibleMirror(World world) {
            BlockPos offset = possibleOffsets[world.rand.nextInt(possibleOffsets.length)];
            RaytraceAssist ray = new RaytraceAssist(getPos(), getPos().add(offset));
            Vector3 from = new Vector3(0.5, 0.7, 0.5);
            Vector3 newDir = new Vector3(offset).add(0.5, 0.5, 0.5).subtract(from);
            for (BlockPos p : offsetMirrors.keySet()) {
                Vector3 toDir = new Vector3(p).add(0.5, 0.5, 0.5).subtract(from);
                if(Math.toDegrees(toDir.angle(newDir)) <= 30) return;
                if(offset.distanceSq(p) <= 3) return;
            }
            if(ray.isClear(world)) {
                addMirrorPosition(world, offset);
            }
        }

        public void addMirrorPosition(World world, BlockPos offset) {
            this.offsetMirrors.put(offset, false);
            updateMirrorPositions(world);

            markDirty(world);
        }

        public void clearAllMirrorPositions(World world) {
            this.offsetMirrors.clear();
            updateMirrorPositions(world);

            markDirty(world);
        }

        @Override
        public boolean needsUpdate() {
            return true;
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
            hasMultiblock = compound.getBoolean("hasMultiblock");
            channeled = compound.getInteger("channeled");
            properties = CrystalProperties.readFromNBT(compound);
            IConstellation c = IConstellation.readFromNBT(compound, IConstellation.getDefaultSaveKey() + "Normal");
            if(c != null && !(c instanceof IWeakConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] Tried to load RitualPedestal from NBT with a non-Major constellation as effect. Ignoring constellation...");
                AstralSorcery.log.warn("[AstralSorcery] Block affected is at " + getPos());
            } else if(c == null) {
                channeling = null;
            } else {
                channeling = (IWeakConstellation) c;
            }
            c = IConstellation.readFromNBT(compound, IConstellation.getDefaultSaveKey() + "Trait");
            if(c != null && !(c instanceof IMinorConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] Tried to load RitualPedestal from NBT with a non-Minor constellation as trait. Ignoring constellation...");
                AstralSorcery.log.warn("[AstralSorcery] Block affected is at " + getPos());
            } else if(c == null) {
                trait = null;
            } else {
                trait = (IMinorConstellation) c;
            }

            offsetMirrors.clear();
            NBTTagList listPos = compound.getTagList("positions", 10);
            for (int i = 0; i < listPos.tagCount(); i++) {
                offsetMirrors.put(NBTUtils.readBlockPosFromNBT(listPos.getCompoundTagAt(i)), false);
            }

            if(compound.hasKey("ritualLinkPos")) {
                this.ritualLinkTo = NBTUtils.readBlockPosFromNBT(compound.getCompoundTag("ritualLinkPos"));
            } else {
                this.ritualLinkTo = null;
            }

            if(channeling != null) {
                ce = channeling.getRitualEffect(this);
                if(compound.hasKey("effect") && ce != null) {
                    NBTTagCompound cmp = compound.getCompoundTag("effect");
                    ce.readFromNBT(cmp);
                }
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);

            compound.setBoolean("doesSeeSky", doesSeeSky);
            compound.setBoolean("hasMultiblock", hasMultiblock);
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
                channeling.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "Normal");
            }
            if(trait != null) {
                trait.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "Trait");
            }
            if(ritualLinkTo != null) {
                NBTTagCompound tag = new NBTTagCompound();
                NBTUtils.writeBlockPosToNBT(ritualLinkTo, tag);
                compound.setTag("ritualLinkPos", tag);
            }
            if(ce != null) {
                NBTTagCompound tag = new NBTTagCompound();
                ce.writeToNBT(tag);
                compound.setTag("effect", tag);
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new PedestalReceiverProvider();
        }

        /*public void update(boolean doesSeeSky, Constellation bufferChanneling, Constellation trait) {
            this.doesSeeSky = doesSeeSky;
            this.channeling = bufferChanneling;
            this.trait = trait;
        }*/

        public void updateSkyState(boolean doesSeeSky) {
            this.doesSeeSky = doesSeeSky;
        }

        public void updateMultiblockState(boolean hasMultiblock) {
            this.hasMultiblock = hasMultiblock;
        }

        public void updateCrystalProperties(World world, CrystalProperties properties, IWeakConstellation channeling, IMinorConstellation trait) {
            IWeakConstellation prev = this.channeling;
            this.properties = properties;
            this.channeling = channeling;
            this.trait = trait;
            if(this.channeling == null || this.channeling != prev) {
                this.clearAllMirrorPositions(world);
            }

            markDirty(world);
        }

        public void updateLink(@Nonnull World world, @Nullable BlockPos ritualLink) {
            BlockPos prev = this.ritualLinkTo;
            this.ritualLinkTo = ritualLink;
            if(prev == null && this.ritualLinkTo == null) return; //Wtf.
            if((prev == null || !prev.equals(this.ritualLinkTo)) && this.ce != null) {
                this.ce.clearCache();
                markDirty(world);
            }
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
