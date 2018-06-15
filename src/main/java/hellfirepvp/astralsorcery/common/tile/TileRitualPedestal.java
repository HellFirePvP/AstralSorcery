/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
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
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.NodeConnection;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
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
        super(1, new EnumFacing[0]);
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

            if(dirty || !getInventoryHandler().getStackInSlot(0).isEmpty()) {
                dirty = false;
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if(recNode != null) {
                    recNode.updateSkyState(doesSeeSky);
                    recNode.updateMultiblockState(hasMultiblock);
                    recNode.updateLink(world, ritualLink);

                    recNode.markDirty(world);

                    if(!getInventoryHandler().getStackInSlot(0).isEmpty()) {
                        recNode.setChannelingCrystal(getInventoryHandler().getStackInSlot(0), this.world);
                        getInventoryHandler().setStackInSlot(0, ItemStack.EMPTY);
                    }
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
                    CrystalProperties prop = CrystalProperties.getCrystalProperties(crystal);
                    if(prop != null && prop.getFracturation() > 0) {
                        if(rand.nextFloat() < (prop.getFracturation() / 100F)) {
                            for (int i = 0; i < 3; i++) {
                                Vector3 at = new Vector3(this).add(0.5, 1.35, 0.5);
                                at.add(
                                        rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1),
                                        rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1),
                                        rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1)
                                );
                                Vector3 mot = new Vector3(
                                        rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                        rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                        rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1)
                                );
                                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                                p.motion(mot.getX(), mot.getY(), mot.getZ());
                                p.setAlphaMultiplier(1F).setColor(ch.getConstellationColor()).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                                p.setMaxAge((int) (16 + rand.nextFloat() * 15));
                                p.gravity(0.004).scale(0.15F + rand.nextFloat() * 0.05F);
                            }

                            if(rand.nextInt(3) == 0) {
                                IWeakConstellation c = getRitualConstellation();
                                Color col = null;
                                if(c != null && c.getConstellationColor() != null) {
                                    col = c.getConstellationColor();
                                }
                                if(!offsetMirrorPositions.isEmpty()) {
                                    BlockPos to = offsetMirrorPositions.get(rand.nextInt(offsetMirrorPositions.size()));
                                    AstralSorcery.proxy.fireLightning(getWorld(),
                                            new Vector3(this).add(0.5, 1.25, 0.5),
                                            new Vector3(to).add(getPos()).add(0.5, 0.5, 0.5),
                                            col);
                                } else {
                                    AstralSorcery.proxy.fireLightning(getWorld(),
                                            new Vector3(this).add(0.5, 1.25, 0.5),
                                            new Vector3(this).add(0.5, 3.5 + rand.nextFloat() * 2.5, 0.5),
                                            col);
                                }
                            }
                        }
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

    @Override
    public void onBreak() {
        super.onBreak();

        if (!world.isRemote && getUpdateCache() != null) {
            ItemStack crystal = getUpdateCache().getCrystal();
            ItemUtils.dropItemNaturally(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, crystal);
            getUpdateCache().setChannelingCrystal(ItemStack.EMPTY, this.world);
        }
    }

    public boolean isWorking() {
        return working;
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    public ItemStack placeCrystalIntoPedestal(ItemStack crystal) {
        TransmissionReceiverRitualPedestal recNode = getUpdateCache();
        if(recNode != null) {
            if(recNode.getCrystal().isEmpty()) {
                markForUpdate();
                return recNode.setChannelingCrystal(crystal, this.world);
            }
        }
        return crystal;
    }

    public ItemStack getCurrentPedestalCrystal() {
        TransmissionReceiverRitualPedestal recNode = getUpdateCache();
        if(recNode != null) {
            return recNode.getCrystal();
        }
        return ItemStack.EMPTY;
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

    /*@Override
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
    }*/

    private void updatePositions(Collection<BlockPos> offsetMirrors) {
        offsetMirrorPositions.clear();
        offsetMirrorPositions.addAll(offsetMirrors);
        markForUpdate();
    }

    @Override
    public void readNetNBT(NBTTagCompound compound) {
        super.readNetNBT(compound);

        if(compound.hasKey("crystalSlot")) {
            ItemStack crystal = new ItemStack(compound.getCompoundTag("crystalSlot"));
            if(!crystal.isEmpty()) {
                getInventoryHandler().setStackInSlot(0, crystal);
            } else {
                getInventoryHandler().setStackInSlot(0, ItemStack.EMPTY);
            }
        } else {
            getInventoryHandler().setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    @Override
    public void writeNetNBT(NBTTagCompound compound) {
        super.writeNetNBT(compound);

        TransmissionReceiverRitualPedestal recNode = getUpdateCache();
        if(recNode != null) {
            ItemStack crystal = recNode.getCrystal();
            if(!crystal.isEmpty()) {
                NBTTagCompound serialized = new NBTTagCompound();
                crystal.writeToNBT(serialized);
                compound.setTag("crystalSlot", serialized);
            }
        }
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

        private ItemStack crystal = ItemStack.EMPTY;

        //private TreeCaptureHelper.TreeWatcher tw = null;
        private ConstellationEffect ce;
        private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

        private double collectionChannelBuffer = 0D;
        private boolean doesWorkBuffer = false;
        private float posDistribution = -1;

        private int idleBuffer = 0;

        public TransmissionReceiverRitualPedestal(BlockPos thisPos, boolean doesSeeSky) {
            super(thisPos);
            this.doesSeeSky = doesSeeSky;
        }

        @Override
        public void update(World world) {
            ticksTicking++;

            if(!this.crystal.isEmpty() && this.crystal.getItem() instanceof ItemTunedCrystalBase) {
                CrystalProperties properties = CrystalProperties.getCrystalProperties(this.crystal);
                IWeakConstellation tuned = ItemTunedCrystalBase.getMainConstellation(this.crystal);
                IMinorConstellation trait = ItemTunedCrystalBase.getTrait(this.crystal);
                updateCrystalProperties(world, properties, tuned, trait);
            } else {
                updateCrystalProperties(world, null, null, null);
            }

            if(channeling != null && properties != null && hasMultiblock) {
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if(ped != null) {
                    ped.markForUpdate();
                }
                if(ce == null) {
                    ce = channeling.getRitualEffect(getRitualOrigin());
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

                    if(posDistribution == -1) {
                        posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, getPos());
                    }

                    if(handle != null) {
                        perc *= CrystalCalculations.getCollectionAmt(properties, handle.getCurrentDistribution(channeling, (in) -> 0.2F + (0.8F * in)));
                        perc *= 1 + (0.5 * posDistribution);
                    }
                    collectionChannelBuffer += perc / 2D;
                }
                if(collectionChannelBuffer > 0) {
                    idleBuffer = 0;

                    doMainEffect(world, ce, trait);

                    if(tryIncrementChannelingTimer())
                        channeled++;

                    flagAsWorking(world);
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

        //TODO occasionally returns with <0?
        private void doMainEffect(World world, ConstellationEffect ce, @Nullable IMinorConstellation trait) {
            ConstellationEffectProperties prop = ce.provideProperties(getCollectedBackmirrors());
            if(trait != null) {
                prop = prop.modify(trait);
            }

            double maxDrain = 7D;
            maxDrain /= CrystalCalculations.getMaxRitualReduction(this.properties);
            maxDrain /= Math.max(1, getCollectedBackmirrors() - 1);
            collectionChannelBuffer *= prop.getPotency();
            int executeTimes = MathHelper.floor(collectionChannelBuffer / maxDrain);

            int freeCap = MathHelper.floor(CrystalCalculations.getChannelingCapacity(this.properties) * prop.getFracturationLowerBoundaryMultiplier());
            double addFractureChance = CrystalCalculations.getFractureChance(executeTimes, freeCap) * prop.getFracturationRate();
            int part = Math.max(1, executeTimes - freeCap);

            if(ce instanceof ConstellationEffectStatus && executeTimes > 0) {
                collectionChannelBuffer = 0;
                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(((ConstellationEffectStatus) ce).runEffect(world, to, getCollectedBackmirrors(), prop, trait)) {
                    for (int i = 0; i < part; i++) {
                        if(rand.nextFloat() < (addFractureChance / part)) {
                            fractureCrystal(world);
                        }
                    }
                    markDirty(world);
                }
                return;
            }

            executeTimes = MathHelper.floor(executeTimes * prop.getEffectAmplifier());
            for (int i = 0; i <= executeTimes; i++) {
                float perc;
                if(collectionChannelBuffer >= maxDrain) {
                    collectionChannelBuffer -= maxDrain;
                    perc = 1F;
                } else {
                    continue;
                }

                BlockPos to = getPos();
                if(ritualLinkTo != null) to = ritualLinkTo;
                if(ce.playEffect(world, to, perc, prop, trait)) {
                    if(rand.nextFloat() < (addFractureChance / prop.getEffectAmplifier() / part)) {
                        fractureCrystal(world);
                    }
                    markDirty(world);
                }
            }
        }

        private void fractureCrystal(World world) {
            if(!this.crystal.isEmpty()) {
                CrystalProperties prop = CrystalProperties.getCrystalProperties(this.crystal);
                if(prop != null) {
                    prop = new CrystalProperties(prop.getSize(), prop.getPurity(), prop.getCollectiveCapability(), prop.getFracturation() + 1, prop.getSizeOverride());
                    if(prop.getFracturation() >= 100) {
                        SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, world, getPos(), 7.5F, 1.4F);
                        Vector3 at = new Vector3(getPos()).add(0.5, 1.5, 0.5);
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_BURST, at);
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, getPos(), 32));
                        this.crystal = ItemStack.EMPTY;
                    } else {
                        CrystalProperties.applyCrystalProperties(this.crystal, prop);
                    }
                    markDirty(world);
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
                    tryGainMirrorPos(world);
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

        private ILocatable getRitualOrigin() {
            if(this.ritualLinkTo == null) {
                return this;
            }
            return ILocatable.fromPos(this.ritualLinkTo);
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

            if(compound.hasKey("crystal")) {
                this.crystal = new ItemStack(compound.getCompoundTag("crystal"));
            } else {
                this.crystal = ItemStack.EMPTY;
            }

            if(compound.hasKey("ritualLinkPos")) {
                this.ritualLinkTo = NBTUtils.readBlockPosFromNBT(compound.getCompoundTag("ritualLinkPos"));
            } else {
                this.ritualLinkTo = null;
            }

            if(channeling != null) {
                ce = channeling.getRitualEffect(getRitualOrigin());
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
            if(!crystal.isEmpty()) {
                NBTTagCompound cmp = new NBTTagCompound();
                this.crystal.writeToNBT(cmp);
                compound.setTag("crystal", cmp);
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
            if(prev == null || !prev.equals(this.ritualLinkTo)) {
                if (channeling != null) {
                    this.ce = channeling.getRitualEffect(getRitualOrigin());
                }
                markDirty(world);
            }
        }

        public ItemStack setChannelingCrystal(ItemStack crystal, World world) {
            this.crystal = ItemUtils.copyStackWithSize(crystal, 1);
            crystal = ItemUtils.copyStackWithSize(crystal, crystal.getCount() - 1);
            markDirty(world);
            return crystal;
        }

        public ItemStack getCrystal() {
            return crystal;
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
