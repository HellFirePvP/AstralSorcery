/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXMotionController;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.base.TreeType;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeTreeBeaconColor;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.base.TileAreaOfInfluence;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverTreeBeacon;
import hellfirepvp.astralsorcery.common.util.CalendarUtils;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTreeBeacon
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:57
 */
public class TileTreeBeacon extends TileReceiverBase<StarlightReceiverTreeBeacon> implements TileAreaOfInfluence {

    private final Map<BlockPos, Integer> treeComponents = new HashMap<>();
    private UUID playerUUID = null;

    private float starlight = 0F;

    public TileTreeBeacon() {
        super(TileEntityTypesAS.TREE_BEACON);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isRemote()) {
            playEffects();
        } else {
            doHarvestCycle();
        }
    }

    private void doHarvestCycle() {
        boolean changed = this.starlight > 0 || !this.treeComponents.isEmpty();

        int cycles = Math.max(1, MathHelper.ceil(this.starlight * 0.8F));
        this.starlight = 0;
        for (int i = 0; i < cycles; i++) {
            float filled = this.treeComponents.size() / Config.CONFIG.maxCount.get().floatValue();
            if (rand.nextFloat() >= filled * 0.25F) {
                continue;
            }

            BlockPos pos = MiscUtils.getWeightedRandomEntry(this.treeComponents.keySet(), rand, this.treeComponents::get);
            if (pos != null) {
                TileTreeBeaconComponent component = MiscUtils.getTileAt(this.getWorld(), pos, TileTreeBeaconComponent.class, false);
                if (component != null && harvestTree(component)) {

                    int breakChance = Config.CONFIG.breakChance.get();
                    if (breakChance > 0 && rand.nextInt(breakChance) == 0) {
                        if (component.removeSelf()) {
                            this.treeComponents.remove(pos);
                        }
                    }

                    PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.BLOCK_HARVEST_DRAW)
                            .addData(buf -> {
                                ByteBufUtils.writeVector(buf, new Vector3(pos).add(0.5, 0.5, 0.5));
                                ByteBufUtils.writeVector(buf, new Vector3(this.getPos()).add(0.5, 0.5, 0.5));
                                buf.writeInt(this.getColor(LogicalSide.SERVER).getRGB());
                            });
                    PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(this.getWorld(), this.getPos(), 32));
                }
            }
        }

        if (changed) {
            this.markForUpdate();
        }
    }

    private boolean harvestTree(TileTreeBeaconComponent harvest) {
        if (rand.nextFloat() > Config.CONFIG.dropChance.get()) {
            return true;
        }
        World world = this.getWorld();
        if (!(world instanceof ServerWorld)) {
            return false;
        }
        if (!MiscUtils.canEntityTickAt(world, harvest.getPos())) {
            return false;
        }
        List<ItemStack> drops = BlockUtils.getDrops((ServerWorld) world, harvest.getPos(), harvest.getFakedState(), 2, rand, ItemStack.EMPTY);
        drops.forEach(drop -> {
            if (drop.isEmpty()) {
                return;
            }
            Vector3 offset = new Vector3(0.5, 0.5, 0.5);
            MiscUtils.applyRandomOffset(offset, rand, 2F);
            offset.setY(Math.abs(offset.getY()));
            Vector3 at = new Vector3(this.getPos()).add(offset);
            ItemUtils.dropItemNaturally(world, at.getX(), at.getY(), at.getZ(), drop);
        });
        return false;
    }

    public void receiveStarlight(double amount, IWeakConstellation type) {
        float mul = 1F;
        if (type.equals(ConstellationsAS.aevitas)) {
            mul = 1.4F;
        }
        this.starlight += Math.sqrt(amount) * mul;
    }

    private void captureTree(Supplier<List<BlockPos>> treeGenerator) {
        List<BlockPos> tree = treeGenerator.get();
        tree.stream()
                .sorted(Comparator.comparing(pos -> pos.distanceSq(this.getPos())))
                .filter(pos -> !this.addComponent(pos))
                .forEach(pos -> {
                    //Update blocks that didn't get a client notification
                    world.markAndNotifyBlock(pos, world.getChunkAt(pos), Blocks.AIR.getDefaultState(), world.getBlockState(pos), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                });
    }

    private boolean addComponent(BlockPos pos) {
        if (this.treeComponents.size() >= Config.CONFIG.maxCount.get()) {
            return false;
        }

        World world = this.getWorld();
        BlockState state = world.getBlockState(pos);
        if (!state.isAir(world, pos)) {
            if (this.getWorld().setBlockState(pos, BlocksAS.TREE_BEACON_COMPONENT.getDefaultState(), Constants.BlockFlags.DEFAULT)) {
                TileTreeBeaconComponent tfs = MiscUtils.getTileAt(world, pos, TileTreeBeaconComponent.class, true);
                if (tfs == null) {
                    this.getWorld().setBlockState(pos, state, Constants.BlockFlags.DEFAULT);
                    return false;
                }

                boolean isLog = state.getBlock().isIn(BlockTags.LOGS);
                tfs.setFakedState(state);
                tfs.setTreeBeaconPos(this.getPos());
                tfs.setOverlayColor(this.getColor(LogicalSide.SERVER));
                return this.treeComponents.put(pos, isLog ? Config.CONFIG.logWeight.get() : Config.CONFIG.leafWeight.get()) == null;
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void playEffects() {
        Color color = this.getColor(LogicalSide.CLIENT);
        VFXColorFunction<?> colorFn = VFXColorFunction.constant(color);

        float radius = Config.CONFIG.range.get().floatValue();
        Vector3 thisPos = new Vector3(this.getPos()).add(0.5, 0.5, 0.5);
        int amt = MathHelper.floor( radius * Math.PI / 8);
        for (int i = 0; i < amt; i++) {
            Vector3 at = MiscUtils.getRandomCirclePosition(thisPos, Vector3.RotAxis.Y_AXIS, radius);
            MiscUtils.applyRandomOffset(at, rand, 0.35F);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(colorFn)
                    .setGravityStrength(-0.0015F + rand.nextFloat() * -0.001F)
                    .setScaleMultiplier(0.3F + rand.nextFloat() * 0.1F)
                    .setMaxAge(30 + rand.nextInt(20));
        }

        for (int i = 0; i < Math.ceil(amt * 1.5F); i++) {
            Vector3 offset = new Vector3(0.5, 0.5, 0.5);
            MiscUtils.applyRandomCircularOffset(offset, rand, radius);
            offset.setY(offset.getY() * 0.75F);
            Vector3 at = new Vector3(this.getPos()).add(offset);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(colorFn)
                    .setGravityStrength(rand.nextBoolean() ? -0.0015F : 0)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .setMaxAge(25 + rand.nextInt(10));
        }

        if (rand.nextInt(20) == 0) {
            float alphaDaytime = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
            alphaDaytime *= 0.8F;

            Vector3 at = new Vector3(this).add(0.5, 0.05, 0.5);
            MiscUtils.applyRandomOffset(at, rand, 0.05F);

            EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                    .setOwner(this.playerUUID)
                    .spawn(at)
                    .setup(at.clone().addY(7), 1.5F, 1.5F)
                    .color(colorFn)
                    .setAlphaMultiplier(0.5F + (0.5F * alphaDaytime))
                    .setMaxAge(64);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playDrawParticles(PktPlayEffect pkt) {
        Vector3 from = ByteBufUtils.readVector(pkt.getExtraData());
        Vector3 to = ByteBufUtils.readVector(pkt.getExtraData());
        Color c = new Color(pkt.getExtraData().readInt());

        VFXColorFunction<?> colorFn = VFXColorFunction.constant(c);
        for (int i = 0; i < 10; i++) {
            Vector3 at = new Vector3(from.toBlockPos()).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .motion(VFXMotionController.target(to::clone, 0.04F + rand.nextFloat() * 0.05F))
                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.05F)
                    .color(rand.nextFloat() > 0.8F ? VFXColorFunction.WHITE : colorFn)
                    .setMaxAge(30 + rand.nextInt(20));
        }
    }

    @Nullable
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.markForUpdate();
    }

    public Color getColor(LogicalSide side) {
        return Optional.ofNullable(this.playerUUID)
                .flatMap(uuid -> PatreonEffectHelper.getPatreonEffects(side, this.playerUUID).stream()
                        .filter(effect -> effect instanceof TypeTreeBeaconColor)
                        .map(effect -> (TypeTreeBeaconColor) effect)
                        .findFirst())
                .map(TypeTreeBeaconColor::getTreeBeaconColor)
                .orElse(CalendarUtils.isAprilFirst() ? Color.getHSBColor(rand.nextFloat(), 1F, 1F) :
                        ConstellationsAS.aevitas.getConstellationColor());
    }

    @Nonnull
    @Override
    public StarlightReceiverTreeBeacon provideEndpoint(BlockPos at) {
        return new StarlightReceiverTreeBeacon(at);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public Color getEffectColor() {
        return this.getColor(LogicalSide.CLIENT);
    }

    @Nonnull
    @Override
    public Vector3 getEffectPosition() {
        return new Vector3(this).add(0.5, 0.5, 0.5);
    }

    @Override
    public float getRadius() {
        return Config.CONFIG.range.get().floatValue();
    }

    @Nonnull
    @Override
    public BlockPos getEffectOriginPosition() {
        return this.getPos();
    }

    @Nonnull
    @Override
    public DimensionType getDimensionType() {
        return this.getWorld().getDimension().getType();
    }

    @Override
    public boolean providesEffect() {
        return !this.isRemoved();
    }

    @Override
    public void validate() {
        super.validate();

        TreeWatcher.WATCHERS.computeIfAbsent(this.getDimensionType(), type -> new HashSet<>())
                .add(this.getPos());
    }

    @Override
    public void remove() {
        super.remove();

        TreeWatcher.WATCHERS.computeIfAbsent(this.getDimensionType(), type -> new HashSet<>())
                .remove(this.getPos());
    }

    @Override
    public void onBreak() {
        super.onBreak();

        this.treeComponents.keySet().forEach(pos -> {
            TileTreeBeaconComponent component = MiscUtils.getTileAt(this.getWorld(), pos, TileTreeBeaconComponent.class, true);
            if (component != null) {
                component.revert();
            }
        });
        this.treeComponents.clear();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.treeComponents.clear();
        ListNBT componentList = compound.getList("components", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < componentList.size(); i++) {
            CompoundNBT tag = componentList.getCompound(i);
            this.treeComponents.put(NBTHelper.readBlockPosFromNBT(tag), tag.getInt("weight"));
        }

        this.starlight = compound.getFloat("starlight");

        if (compound.hasUniqueId("playerUUID")) {
            this.playerUUID = compound.getUniqueId("playerUUID");
        } else {
            this.playerUUID = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        ListNBT componentList = new ListNBT();
        MapStream.forEach(this.treeComponents, (pos, weight) -> {
            CompoundNBT tag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(pos, tag);
            tag.putInt("weight", weight);
            componentList.add(tag);
        });
        compound.put("components", componentList);

        compound.putFloat("starlight", this.starlight);

        if (this.playerUUID != null) {
            compound.putUniqueId("playerUUID", this.playerUUID);
        }
    }

    public static class Config extends ConfigEntry {

        public static final Config CONFIG = new Config();

        private static final float  defaultRange        = 12;
        private static final int    defaultMaxCount     = 450;
        private static final float  defaultDropChance   = 0.15F;
        private static final int    defaultBreakChance  = 1000;
        private static final int    defaultLogWeight    = 2;
        private static final int    defaultLeafWeight   = 1;

        public ForgeConfigSpec.DoubleValue range;
        public ForgeConfigSpec.IntValue    maxCount;
        public ForgeConfigSpec.DoubleValue dropChance;
        public ForgeConfigSpec.IntValue    breakChance;
        public ForgeConfigSpec.IntValue    logWeight;
        public ForgeConfigSpec.IntValue    leafWeight;

        private Config() {
            super("tree_beacon");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.range = cfgBuilder
                    .comment("Set the radius of the tree beacon.")
                    .translation(translationKey("range"))
                    .defineInRange("range", defaultRange, 3, 32);
            this.maxCount = cfgBuilder
                    .comment("Set the maximum amount of tree-components the tree beacon may allocate.")
                    .translation(translationKey("maxCount"))
                    .defineInRange("maxCount", defaultMaxCount, 50, 1500);
            this.dropChance = cfgBuilder
                    .comment("Set the chance per harvest-tick for drops to get created.")
                    .translation(translationKey("dropChance"))
                    .defineInRange("dropChance", defaultDropChance, 0.001, 1);
            this.breakChance = cfgBuilder
                    .comment("Set the chance per harvest-tick for the block to get broken (1 in <configured chance>). 0 = blocks never break.")
                    .translation(translationKey("breakChance"))
                    .defineInRange("breakChance", defaultBreakChance, 0, Integer.MAX_VALUE);
            this.logWeight = cfgBuilder
                    .comment("Set the weight to pick a log-block to harvest instead of a leaf-block, compared to 'leafWeight'.")
                    .translation(translationKey("logWeight"))
                    .defineInRange("logWeight", defaultLogWeight, 1, 200);
            this.leafWeight = cfgBuilder
                    .comment("Set the weight to pick a leaf-block (strictly speaking, any non-log block) to harvest instead of a log-block, compared to 'logWeight'.")
                    .translation(translationKey("leafWeight"))
                    .defineInRange("leafWeight", defaultLeafWeight, 1, 200);
        }
    }

    public static class TreeWatcher {

        private static final Map<DimensionType, Set<BlockPos>> WATCHERS = new HashMap<>();

        public static void clearServerCache() {
            WATCHERS.clear();
        }

        public static void onGrow(SaplingGrowTreeEvent event) {
            if (event.getWorld().isRemote() || !(event.getWorld() instanceof ServerWorld)) {
                return;
            }

            ServerWorld world = (ServerWorld) event.getWorld();
            BlockPos treePos = event.getPos();
            TreeType type = TreeType.isTree(world, treePos);
            if (type == null) {
                return;
            }
            double rangeSq = Config.CONFIG.range.get() * Config.CONFIG.range.get();
            Set<BlockPos> watchers = WATCHERS.getOrDefault(world.getDimension().getType(), Collections.emptySet())
                    .stream()
                    .filter(pos -> pos.distanceSq(treePos) < rangeSq)
                    .collect(Collectors.toSet());
            BlockPos closestBeacon = MiscUtils.getMinEntry(watchers, pos -> pos.distanceSq(treePos));
            TileTreeBeacon ttb = MiscUtils.getTileAt(world, closestBeacon, TileTreeBeacon.class, false);
            if (ttb == null) {
                return;
            }

            event.setResult(Event.Result.DENY);

            Supplier<List<BlockPos>> generator = type.getTreeGenerator(world, treePos, event.getRand());
            ttb.captureTree(generator);
        }
    }
}
