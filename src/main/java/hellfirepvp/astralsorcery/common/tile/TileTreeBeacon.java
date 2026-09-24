/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.visual.type.BlockHarvestDraw;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerViewFactory;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenStackList;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestHelper;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityLumenDisplay;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.ForwardingStarlightReceiverNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.ForwardingStarlightReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTreeBeacon
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileTreeBeacon extends TileEntityNetwork<ForwardingStarlightReceiverNode, TileTreeBeacon.Data> implements TileEntityLumenDisplay, ForwardingStarlightReceiverNode.ReceiverTile {

    public static final Config CONFIG = new Config();

    private double registeredGrowHandlerRadius = -1;

    public TileTreeBeacon(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.TREE_BEACON, pos, blockState);
    }

    protected TileTreeBeacon(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        double cfgRange = CONFIG.range.getAsDouble();
        if (this.registeredGrowHandlerRadius != cfgRange) {
            this.setupTreeGrowthHandler(level, cfgRange);
            this.registeredGrowHandlerRadius = cfgRange;
        }

        this.gatherCycle(level);
        this.doHavestCycle(level);
    }

    protected void setupTreeGrowthHandler(ServerLevel level, double radius) {
        TreeGrowUtil.addHandler(level, this.getBlockPos(), radius, (event, tree) -> {
            tree.simulateGrowTree(level, event.getRandom()).forEach(snapshot -> {
                LevelAccessor evLevel = event.getLevel();
                //Reset blocks we don't capture
                if (!this.canCaptureBlock(evLevel, snapshot.getPos(), snapshot.getState())) {
                    evLevel.setBlock(snapshot.getPos(), snapshot.getState(), Block.UPDATE_CLIENTS);
                    return;
                }
                evLevel.setBlock(snapshot.getPos(), BlocksAS.TRANSLUCENT_TREE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                MiscUtil.getTileAt(evLevel, snapshot.getPos(), TileTranslucentTree.class, true).ifPresent(tile -> {
                    TileTranslucentTree.Data data = tile.getTileData();
                    data.setStoredState(snapshot.getState());
                    data.setTreeBeaconPos(this.getBlockPos());
                    data.markForUpdate();

                    this.getTileData().addTreeComponent(snapshot.getPos(), this.isLog(snapshot.getState()) ? CONFIG.logWeight.getAsInt() : 1);
                });

                //Revert if something went wrong
                if (!MiscUtil.getTileExists(evLevel, snapshot.getPos(), TileTranslucentTree.class, true)) {
                    evLevel.setBlock(snapshot.getPos(), snapshot.getState(), Block.UPDATE_CLIENTS);
                }
            });
            return true;
        });
    }

    private void gatherCycle(ServerLevel level) {
        if (this.doesSeeSky()) {
            if (DayTimeHelper.isNight(level)) {
                float generated = DayTimeHelper.getCurrentDaytimeDistribution(level) * 0.4F;
                this.getTileData().addStoredStarlight(MiscUtil.roundChanced(generated, rand));
            }
        }

        if (this.getTileData().getTicksExisted() % 80 == 0) {
            Lumen lumen = LumenAS.AEVITAS.get();
            LumenStack drain = lumen.stack(500);
            int storedLumen = this.getTileData().getStoredLumen().getLumenStack(lumen).map(LumenStack::getAmount).orElse(0);
            if (storedLumen <= 500) {
                LumenRequestHelper.requestRelayed(level, this.getBlockPos(), drain).ifPresent(chain -> {
                    LumenStack stored = LumenUtil.tryChainTransfer(this.getTileData().getLumenHandler(), level, chain, drain, ILumenHandler.Action.EXECUTE);
                    if (!stored.isEmpty()) {
                        chain.playTransferEffect(level, stored.getLumen());
                    }
                });
            }
        }
    }

    protected void doHavestCycle(ServerLevel level) {
        int cycles = Math.max(1, MiscUtil.roundChanced(this.getTileData().getStoredStarlight() * 0.75F, rand));
        this.getTileData().setStoredStarlight(0);

        float pivotCount = CONFIG.productionPivot.getAsInt();
        for (int i = 0; i < cycles; i++) {
            int storedCount = this.getTileData().treeComponentWeights.size();
            float pivotChance = storedCount / pivotCount;
            if (pivotChance > 1F) pivotChance = 1F / pivotChance;
            if (rand.nextFloat() >= pivotChance) continue;

            Map<BlockPos, Integer> components = this.getTileData().getTreeComponents();
            MiscUtil.getWeightedRandomEntry(components.keySet(), rand, components::get).ifPresent(pos -> {
                MiscUtil.getTileAt(level, pos, TileTranslucentTree.class, false).ifPresent(tile -> {
                    boolean doEffect = false;
                    if (rand.nextInt(Math.max(1, CONFIG.harvestChance.getAsInt())) == 0) {
                        this.doHarvest(level, tile);
                        doEffect = true;
                    }

                    if (rand.nextInt(Math.max(1, CONFIG.breakChance.getAsInt())) == 0) {
                        if (tile.removeBlock(level) && this.getTileData().removeTreeComponent(pos)) {
                            this.getTileData().markForUpdate();
                            doEffect = true;
                        }
                    }

                    if (doEffect) {
                        BlockHarvestDraw.make(pos, Vector3.atCenter(this.getBlockPos()), ColorsAS.TREE_BEACON_GREEN)
                                .sendToNearby(level, this.getBlockPos());
                    }
                });
            });
        }
    }

    protected void doHarvest(ServerLevel level, TileTranslucentTree tile) {
        BlockState storedState = tile.getTileData().getStoredState();
        if (storedState.isAir()) return;

        BlockUtil.getDrops(level, tile.getTileData().getStoredState(), tile.getBlockPos(), 2).forEach(drop -> {
            BlockPos pos = this.getBlockPos().offset(
                    Mth.nextInt(rand, -2, 2),
                    Mth.nextInt(rand, -2, 2) + 2,
                    Mth.nextInt(rand, -2, 2)
            );
            Block.popResource(level, pos, drop);
        });
    }

    protected boolean canCaptureBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) != null) return false;
        return state.is(TagsAS.Blocks.VALID_TREE_BEACON_BLOCK);
    }

    protected boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        boolean hasLumen = this.getTileData().getStoredLumen().getLumenStack(LumenAS.AEVITAS.get()).map(LumenStack::getAmount).orElse(0) > 0;
        FXColorFunction<?> colorFn = FXColorFunction.constant(ColorsAS.TREE_BEACON_GREEN);
        float daytimeMultiplier = DayTimeHelper.getCurrentDaytimeDistribution(level);
        float alpha = 0.1F + (daytimeMultiplier * 0.9F);
        float markerAlpha = 0.7F + alpha * 0.3F;

        Vector3 thisPos = Vector3.atBottomCenter(this);
        double radius = CONFIG.range.getAsDouble();
        int count = MiscUtil.roundChanced(radius * 0.15F, rand);
        for (int i = 0; i < count; i++) {
            Vector3 offset = Vector3.random(rand).setY(0).normalize().multiply(radius);
            Vector3 effectPos = VectorUtil.withRandomOffset(thisPos, rand, 0.3F).add(offset);


            float scale = 0.35F + rand.nextFloat() * 0.2F;
            float gravity = 0.00015F + rand.nextFloat() * 0.0001F;
            int maxAge = 70 + rand.nextInt(40);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(effectPos)
                    .setAlpha(markerAlpha)
                    .color(colorFn)
                    .setScale(scale)
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(maxAge);
            if (hasLumen) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(effectPos)
                        .setAlpha(alpha)
                        .color(FXColorFunction.WHITE)
                        .setScale(scale * 0.4F)
                        .setGravity(Vector3.y(gravity))
                        .setMaxAge(maxAge);
            }
        }

        int randomCount = MiscUtil.roundChanced(count * 0.4F, rand);
        for (int i = 0; i < randomCount; i++) {
            Vector3 offset = Vector3.random(rand).setY(0).normalize().multiply(radius * rand.nextFloat()).add(this.getBlockPos());

            float scale = 0.35F + rand.nextFloat() * 0.2F;
            float gravity = 0.00015F + rand.nextFloat() * 0.0001F;
            int maxAge = 70 + rand.nextInt(40);
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setAlpha(alpha)
                    .color(colorFn)
                    .setScale(scale)
                    .setGravity(Vector3.y(gravity))
                    .setMaxAge(maxAge);
            if (hasLumen) {
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(offset)
                        .setAlpha(alpha)
                        .color(FXColorFunction.WHITE)
                        .setScale(scale * 0.4F)
                        .setGravity(Vector3.y(gravity))
                        .setMaxAge(maxAge);
            }
        }

        if (rand.nextInt(20) == 0) {
            Vector3 offset = Vector3.atBottomCenter(this);
            offset = VectorUtil.withRandomOffset(offset, rand, 0.1F).setY(offset.getY());
            EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                    .spawn(offset)
                    .setup(offset.copy().addY(6 + rand.nextFloat()), 1.2F, 1.2F)
                    .setAlpha(alpha)
                    .color(colorFn);
        }
    }

    @Override
    public void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet) {
        int added = MiscUtil.roundChanced(packet.amount(), rand);
        this.getTileData().addStoredStarlight(added);
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, ForwardingStarlightReceiverNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.FORWARDING_RECEIVER_NODE;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    public void onTileEntityRemove(Level level, BlockPos pos) {
        super.onTileEntityRemove(level, pos);

        if (level instanceof ServerLevel sLevel) {
            TreeGrowUtil.removeHandler(sLevel, pos);
        }
    }

    public static class Data extends TileEntityNetwork.Data {

        public static final int LUMEN_TANK_CAPACITY = 1000;

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> treeBeaconFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P8<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, Map<BlockPos, Integer>, LumenStackList, Integer, Optional<UUID>> treeBeaconFields(RecordCodecBuilder.Instance<T> instance) {
            return netFields(instance).and(instance.group(
                    CodecUtil.defaulted(Codec.unboundedMap(CodecUtil.stringBlockPos(), Codec.INT), "treeComponents", HashMap::new, Data::getTreeComponents),
                    CodecUtil.defaulted(LumenStackList.CODEC, "lumenContents", LumenStackList::create, Data::getStoredLumen),
                    CodecUtil.defaulted(Codec.INT, "starlightStored", () -> 0, Data::getStoredStarlight),
                    CodecUtil.optional(CodecUtil.uuidCodec(), "ownerId", Data::getOwnerId)
            ));
        }

        protected Map<BlockPos, Integer> treeComponentWeights = new HashMap<>();
        protected LumenStackList storedLumen;
        protected int storedStarlight;
        protected UUID ownerId;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       boolean needsNetworkSync,
                       Map<BlockPos, Integer> treeComponents,
                       LumenStackList storedLumen,
                       int storedStarlight,
                       Optional<UUID> ownerId) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.treeComponentWeights.putAll(treeComponents);
            this.storedLumen = storedLumen;
            this.storedStarlight = storedStarlight;
            this.ownerId = ownerId.orElse(null);
        }

        public void addTreeComponent(BlockPos pos, int weight) {
            this.treeComponentWeights.put(pos, weight);
        }

        public boolean removeTreeComponent(BlockPos pos) {
            return this.treeComponentWeights.remove(pos) != null;
        }

        public Map<BlockPos, Integer> getTreeComponents() {
            return Collections.unmodifiableMap(this.treeComponentWeights);
        }

        protected LumenStackList getStoredLumen() {
            return this.storedLumen;
        }

        protected LumenHandlerViewFactory newLumenHandler() {
            return LumenHandlerViewFactory.builder()
                    .accessibleSides(Direction.DOWN)
                    .tankCapacity(lumen -> LUMEN_TANK_CAPACITY)
                    .extractFilter((toExtract, existing) -> false);
        }

        public LumenHandlerView getLumenHandler() {
            return this.newLumenHandler().createTileView(this, this.getStoredLumen());
        }

        public int getStoredStarlight() {
            return this.storedStarlight;
        }

        public void setStoredStarlight(int storedStarlight) {
            this.storedStarlight = storedStarlight;
        }

        public void addStoredStarlight(int amount) {
            int stored = this.getStoredStarlight();
            this.setStoredStarlight(stored + amount);
        }

        @Nullable
        public UUID getOwnerId() {
            return this.ownerId;
        }

        public void setOwnerId(UUID ownerId) {
            this.ownerId = ownerId;
        }
    }

    public static class Config extends ConfigEntry {

        private static final double defaultRange           = 12.0;
        private static final int    defaultProductionPivot = 300;
        private static final int    defaultHarvestChance   = 15;
        private static final int    defaultBreakChance     = 800;
        private static final int    defaultLogWeight       = 2;

        public ModConfigSpec.DoubleValue range;
        public ModConfigSpec.IntValue    productionPivot;
        public ModConfigSpec.IntValue    harvestChance;
        public ModConfigSpec.IntValue    breakChance;
        public ModConfigSpec.IntValue    logWeight;

        private Config() {
            super("tree_beacon");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.range = cfgBuilder
                    .comment("Radius of the tree beacon")
                    .translation(translationKey("range"))
                    .defineInRange("range", defaultRange, 3, 32);
            this.productionPivot = cfgBuilder
                    .comment("If blocks < pivot, it gets faster the more blocks are stored. If > pivot, it gets slower the more blocks are stored.")
                    .translation(translationKey("production_pivot"))
                    .defineInRange("production_pivot", defaultProductionPivot, 100, 2000);
            this.harvestChance = cfgBuilder
                    .comment("Chance (1 in x) per tick to harvest/generate drops of one of the block stored")
                    .translation(translationKey("harvest_chance"))
                    .defineInRange("harvest_chance", defaultHarvestChance, 2, 500);
            this.breakChance = cfgBuilder
                    .comment("Chance (1 in x) per tick to break and remove one of the blocks stored; 0 never breaks blocks.")
                    .translation(translationKey("break_chance"))
                    .defineInRange("break_chance", defaultBreakChance, 0, Integer.MAX_VALUE);
            this.logWeight = cfgBuilder
                    .comment("Weight multiplier for log blocks when picking a block to harvest. Only applies for newly captured trees after changing this value.")
                    .translation(translationKey("log_weight"))
                    .defineInRange("log_weight", defaultLogWeight, 1, 200);
        }
    }
}
