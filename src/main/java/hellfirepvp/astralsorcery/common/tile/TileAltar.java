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
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.SoundUtil;
import hellfirepvp.astralsorcery.common.block.tile.AltarBlock;
import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.container.*;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityLumenDisplay;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.ForwardingStarlightReceiverNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.ForwardingStarlightReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.codec.CodecProducts;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.MenuTypeRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.*;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import hellfirepvp.observerlib.api.ObserverProvider;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.common.change.ObserverProviderStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileAltar extends TileEntityNetwork<ForwardingStarlightReceiverNode, TileAltar.Data> implements ForwardingStarlightReceiverNode.ReceiverTile, TileEntityLumenDisplay {

    private static final Map<Integer, BlockPos> RELAY_GRID_OFFSETS = new HashMap<>();
    private static final List<Integer> INNER_RELAY_SLOTS = List.of(6, 7, 8, 11, 13, 16, 17, 18);
    private static final List<Integer> OUTER_RELAY_SLOTS = IntStream.range(0, 25).filter(i -> i != 12 && !INNER_RELAY_SLOTS.contains(i)).boxed().toList();

    private final Map<BaseConstellation, Float> aggregateStarlight = new HashMap<>();
    private final ClientObject<PlayableSoundInstance> craftingSound = new ClientObject<>();
    private final ClientObject<PlayableSoundInstance> craftingWaitSound = new ClientObject<>();

    static {
        BlockPos origin = new BlockPos(-4, 1, -4);
        for (int relaySlot = 0; relaySlot < 25; relaySlot++) {
            if (relaySlot == 12) continue; // center/altar

            BlockPos offset = origin.offset((relaySlot % 5) * 2, 0, (relaySlot / 5) * 2);
            if (INNER_RELAY_SLOTS.contains(relaySlot)) offset = offset.below();
            RELAY_GRID_OFFSETS.put(relaySlot, offset);
        }
    }

    public TileAltar(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.ALTAR, pos, blockState);
    }

    protected TileAltar(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        if (blockState.getBlock() instanceof AltarBlock altarBlock) {
            this.getTileData().setAltarType(altarBlock.getAltarType());
        }
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        this.doesSeeSky();
        this.hasStructure();

        this.doCraftingCycle(level);

        this.clearAggregatedStarlight();
    }

    private void doCraftingCycle(ServerLevel level) {
        if (!this.hasStructure()) {
            this.abortCrafting();
            return;
        }

        this.getTileData().getActiveRecipe().ifPresent(activeRecipe -> {
            if (!activeRecipe.isValid(level) || !activeRecipe.matches(level, this)) {
                this.abortCrafting();
                return;
            }
            AltarRecipe recipe = activeRecipe.getRecipe(level).orElse(null);
            if (recipe == null) {
                this.abortCrafting();
                return;
            }

            activeRecipe.tickTrackAdditionalInputs(this, level, this.getBlockPos(), this.aggregateStarlight);
            activeRecipe.tick(level);

            if (activeRecipe.isFinished(level) && activeRecipe.consumeItemInputs(this.getTileData(), level, this.getBlockPos(), true)) {
                recipe.createOutput(this.createInput(level, activeRecipe.getPlayerUUID()), level.registryAccess());
                activeRecipe.consumeItemInputs(this.getTileData(), level, this.getBlockPos(), false);

                if (recipe.getBaseFocusShatterChance() > 0F &&
                        recipe.getFocusConstellation().isPresent() &&
                        this.rand.nextFloat() < recipe.getBaseFocusShatterChance()) {
                    this.getTileData().setFocusItem(ItemStack.EMPTY);
                    ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, this.getLevel(), this.getBlockPos(), 1F, 1F);
                }

                this.getTileData().addKnownRecipe(activeRecipe.getRecipeId());

                if (!recipe.mayChain() || !activeRecipe.matches(level, this)) {
                    ServerSoundHelper.playSoundAround(SoundsAS.ALTAR_CRAFT_FINISH, level, Vector3.atCenter(this), 0.6F, 1F);
                    this.abortCrafting();
                    return;
                }
            }

            this.getTileData().markForUpdate();
        });
    }

    public void startCrafting(RecipeHolder<AltarRecipe> recipe, UUID playerUUID) {
        if (this.getTileData().getActiveRecipe().isPresent()) {
            this.abortCrafting();
        }

        ActiveAltarRecipe activeRecipe = ActiveAltarRecipe.of(recipe, playerUUID, Direction.NORTH);
        this.getTileData().setActiveRecipe(activeRecipe);
        this.getTileData().markForUpdate();

        ServerSoundHelper.playSoundAround(SoundsAS.ALTAR_CRAFT_START, level, Vector3.atCenter(this), 0.6F, 1F);
    }

    public Optional<RecipeHolder<AltarRecipe>> findMatchingRecipe(Level level) {
        AltarCraftingInput input = this.createInput(level, null);
        return RecipeFinder.of(level).findAltarRecipe(this.getLevel(), input);
    }

    public AltarCraftingInput createInput(Level level, UUID playerUUID) {
        return AltarCraftingInput.create(this, level, playerUUID);
    }

    public void abortCrafting() {
        this.getTileData().setActiveRecipe(null);
        this.markForUpdate();
    }

    private void clearAggregatedStarlight() {
        this.aggregateStarlight.clear();
    }

    @Override
    public void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet) {
        float amt = this.aggregateStarlight.getOrDefault(packet.constellation(), 0F);
        this.aggregateStarlight.put(packet.constellation(), amt + packet.amount());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        if (this.hasStructure()) {
            this.playActiveSparkle(level);
        }

        this.getTileData().getActiveRecipe().ifPresent(activeRecipe -> {
            if (!activeRecipe.isValid(level)) return;

            this.playCraftingSound(activeRecipe, level);
            activeRecipe.tickEffects(level, this);
            activeRecipe.playLiquidDrawEffects(level, this.getBlockPos());
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void playActiveSparkle(Level level) {
        if (!this.hasStructure()) return;
        if (this.getRequiredObserver() == null || this.getRequiredObserver() == ObserversAS.STRUCTURE_EMPTY) return;
        if (this.getTileData().getActiveRecipe().isPresent()) return;

        ObserverProvider<?> provider = ObserversAS.STRUCTURE_ALTAR_T4.observer().get();
        if (!(provider instanceof ObserverProviderStructure providerStructure)) return;
        MatchableStructure structure = providerStructure.getStructure();

        List<BlockPos> sootyMarblePositions = new ArrayList<>();
        structure.getContents().forEach((offset, matchState) -> {
            if (matchState.getDescriptiveState(0).is(TagsAS.Blocks.SOOTY_MARBLE)) {
                BlockPos at = offset.offset(this.getBlockPos());
                if (matchState.matches(level, at, level.getBlockState(at))) {
                    sootyMarblePositions.add(at);
                }
            }
        });
        if (sootyMarblePositions.isEmpty()) return;

        int attempts = MiscUtil.roundChanced(sootyMarblePositions.size() / 200F, this.rand);
        for (int i = 0; i < attempts; i++) {
            MiscUtil.getRandomEntry(sootyMarblePositions, this.rand).ifPresent(pos -> {
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(new Vector3(pos).add(Vector3.positiveRandom(this.rand).setY(1.01F)))
                        .color(FXColorFunction.WHITE)
                        .setAlpha(0.75F)
                        .setScale(0.3F + rand.nextFloat() * 0.1F);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playCraftingSound(ActiveAltarRecipe activeRecipe, Level level) {
        if (SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0) return;

        if (this.craftingSound.isNull() || this.craftingSound.get().hasStoppedPlaying()) {
            CategorizedSoundEvent soundEvent = switch (this.getTileData().getAltarType()) {
                case ILLUMINATION -> SoundsAS.ALTAR_CRAFT_LOOP_T1;
                case RESONANCE -> SoundsAS.ALTAR_CRAFT_LOOP_T2;
                case LUMINANCE -> SoundsAS.ALTAR_CRAFT_LOOP_T3;
                case RADIANCE -> SoundsAS.ALTAR_CRAFT_LOOP_T4;
            };

            PlayableSoundInstance sound = PlayableSoundInstance.of(soundEvent)
                    .pos(Vector3.atCenter(this))
                    .volume(0.6F)
                    .loop(true)
                    .fadeInTicks(40)
                    .fadeOutTicks(20)
                    .stopFunction(inst -> {
                        return this.isRemoved() ||
                                SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                this.getTileData().getActiveRecipe().isEmpty();
                    })
                    .play();

            this.craftingSound.set(sound);
        }

        if (!activeRecipe.getCraftingState().canProgress()) {
            if (this.craftingWaitSound.isNull() || this.craftingWaitSound.get().hasStoppedPlaying()) {
                PlayableSoundInstance sound = PlayableSoundInstance.of(SoundsAS.ALTAR_CRAFT_LOOP_WAITING)
                        .pos(Vector3.atCenter(this))
                        .volume(0.6F)
                        .loop(true)
                        .fadeInTicks(30)
                        .fadeOutTicks(30)
                        .stopFunction(inst -> {
                            return this.isRemoved() ||
                                    SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                    this.getTileData().getActiveRecipe().isEmpty() ||
                                    this.getTileData().getActiveRecipe().get().getCraftingState().canProgress();
                        })
                        .play();
                this.craftingWaitSound.set(sound);
            }

            this.craftingSound.get().setVolumeMultiplier(0.3F);
        } else {
            this.craftingSound.get().setVolumeMultiplier(1F);
        }
    }

    public static Map<Integer, BlockPos> getRelayGridOffsets() {
        return Collections.unmodifiableMap(RELAY_GRID_OFFSETS);
    }

    public static List<Integer> getInnerRelaySlots() {
        return INNER_RELAY_SLOTS;
    }

    public static List<Integer> getOuterRelaySlots() {
        return OUTER_RELAY_SLOTS;
    }

    @Override
    public Optional<StoredLumenDisplayTooltip> getDisplayTooltip() {
        return this.getTileData().getActiveRecipe().flatMap(activeRecipe -> {
            AltarRecipe recipe = activeRecipe.getRecipe(this.getLevel()).orElse(null);
            if (recipe == null) return Optional.empty();
            List<LumenStack> requiredLumen = recipe.getRequiredLumen();
            if (requiredLumen.isEmpty()) return Optional.empty();

            List<StoredLumenComponent.StoredLumen> storedLumen = new ArrayList<>();
            List<StoredLumenComponent.StoredLumenDisplay> lumenDisplays = new ArrayList<>();
            for (LumenStack stack : requiredLumen) {
                Lumen lumen = stack.getLumen();

                LumenStack drawn = activeRecipe.getDrawnLumen().stream()
                        .filter(l -> l.is(lumen))
                        .findFirst()
                        .orElse(LumenStack.EMPTY);
                storedLumen.add(new StoredLumenComponent.StoredLumen(lumen, drawn.getAmount(), stack.getAmount()));

                lumen.getRegistryKey().ifPresent(holder -> {
                    lumenDisplays.add(new StoredLumenComponent.StoredLumenDisplay(holder, true, lumen.getName()));
                });
            }
            StoredLumenComponent cmp = new StoredLumenComponent(storedLumen, lumenDisplays);
            return Optional.of(StoredLumenDisplayTooltip.of(this.getDisplayComponentIdentifier(), cmp));
        });
    }

    @Nullable
    @Override
    public ObserverRegistryObject getRequiredObserver() {
        return this.getTileData().getAltarType().getRequiredStructure();
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
    protected void onClientDataUpdated(Data previousData) {
        super.onClientDataUpdated(previousData);

        previousData.getActiveRecipe().ifPresent(previousActiveRecipe -> {
            this.getTileData().getActiveRecipe().ifPresent(previousActiveRecipe::copyEffectDataTo);
        });
    }

    @Override
    public void setBlockState(BlockState newState) {
        BlockState oldState = this.getBlockState();
        super.setBlockState(newState);

        //No removal, just different tier/altar block
        if (newState.getBlock() instanceof AltarBlock) {
            AltarType oldType = ((AltarBlock) oldState.getBlock()).getAltarType();
            AltarType newType = ((AltarBlock) newState.getBlock()).getAltarType();

            if (oldType != newType) {
                this.getTileData().setAltarType(newType);
                this.hasStructure();

                this.getTileData().markForUpdate();
            }
        }
    }

    @Override
    public boolean shouldRemoveStructureObserver(BlockState currentState, BlockState newState) {
        return !(newState.getBlock() instanceof AltarBlock);
    }

    public static class Data extends TileEntityNetwork.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> altarFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P9<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, InventoryStackList, Optional<ActiveAltarRecipe>, Set<ResourceLocation>, ItemStack, AltarType> altarFields(RecordCodecBuilder.Instance<T> instance) {
            return CodecProducts.and(netFields(instance), instance.group(
                    CodecUtil.defaulted(InventoryStackList.CODEC, "altarInventoryContents", InventoryStackList::create, Data::getAltarInventoryContents),
                    ActiveAltarRecipe.CODEC.optionalFieldOf("activeRecipe").forGetter(Data::getActiveRecipe),
                    CodecUtil.defaulted(SetCodec.of(ResourceLocation.CODEC), "knownRecipes", HashSet::new, data -> data.knownRecipes),
                    CodecUtil.lenientDefaulted(ItemStack.CODEC, "focusItem", () -> ItemStack.EMPTY, Data::getFocusItem, ItemStack::isEmpty),
                    AltarType.CODEC.optionalFieldOf("altarType", AltarType.ILLUMINATION).forGetter(Data::getAltarType)
            ));
        }

        protected final InventoryStackList altarInventoryContents;
        protected ActiveAltarRecipe activeRecipe;
        protected Set<ResourceLocation> knownRecipes = new HashSet<>();
        protected ItemStack focusItem;
        protected AltarType altarType;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       boolean needsNetworkSync,
                       InventoryStackList altarInventoryContents,
                       Optional<ActiveAltarRecipe> activeRecipe,
                       Set<ResourceLocation> knownRecipes,
                       ItemStack focusItem,
                       AltarType altarType) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.altarInventoryContents = altarInventoryContents;
            this.activeRecipe = activeRecipe.orElse(null);
            this.knownRecipes.addAll(knownRecipes);
            this.focusItem = focusItem;
            this.altarType = altarType;
        }

        protected InventoryStackList getAltarInventoryContents() {
            return this.altarInventoryContents;
        }

        protected FilteredInventoryViewFactory newAltarInventory() {
            return FilteredInventoryViewFactory.filteredBuilder(9)
                    .accessibleSides(Direction.DOWN);
        }

        public InventoryView getAltarInventory() {
            return this.newAltarInventory().createTileView(this, this.getAltarInventoryContents());
        }

        public ItemStack getFocusItem() {
            return this.focusItem;
        }

        public void setFocusItem(ItemStack focusItem) {
            this.focusItem = focusItem;
        }

        public Optional<BaseConstellation> getFocusedConstellation() {
            return this.getFocusItem()
                    .getOrDefault(DataComponentsAS.ATTUNED_CONSTELLATION, AttunedConstellationComponent.EMPTY)
                    .getConstellation();
        }

        public boolean canSetFocusItem(ItemStack stack) {
            return stack.is(TagsAS.Items.FUNCTIONAL_ALTAR_CONSTELLATION_ITEM) &&
                    stack.getOrDefault(DataComponentsAS.ATTUNED_CONSTELLATION, AttunedConstellationComponent.EMPTY).getConstellation().isPresent();
        }

        public AltarType getAltarType() {
            return this.altarType;
        }

        public void setAltarType(AltarType altarType) {
            this.altarType = altarType;
        }

        public Optional<ActiveAltarRecipe> getActiveRecipe() {
            return Optional.ofNullable(this.activeRecipe);
        }

        public void setActiveRecipe(@Nullable ActiveAltarRecipe activeRecipe) {
            this.activeRecipe = activeRecipe;
        }

        public Set<ResourceLocation> getKnownRecipes() {
            return Collections.unmodifiableSet(this.knownRecipes);
        }

        public boolean addKnownRecipe(ResourceLocation recipeId) {
            return this.knownRecipes.add(recipeId);
        }

        public boolean removeKnownRecipe(ResourceLocation recipeId) {
            return this.knownRecipes.remove(recipeId);
        }
    }

    public enum AltarType implements StringRepresentable {

        ILLUMINATION(ResearchTier.ILLUMINATION, ObserversAS.STRUCTURE_EMPTY, BlocksAS.ALTAR_ILLUMINATION, Shapes.block(),
                type(MenuTypesAS.ALTAR_ILLUMINATION, ContainerAltarIllumination::new, ContainerAltarIllumination::new)),
        RESONANCE(ResearchTier.RESONANCE, ObserversAS.STRUCTURE_ALTAR_T2, BlocksAS.ALTAR_RESONANCE,
                Shapes.box(-1F / 16F, 0F, -1F / 16F, 17F / 16F, 1F, 17F / 16F),
                type(MenuTypesAS.ALTAR_RESONANCE, ContainerAltarResonance::new, ContainerAltarResonance::new)),
        LUMINANCE(ResearchTier.LUMINANCE, ObserversAS.STRUCTURE_ALTAR_T3, BlocksAS.ALTAR_LUMINANCE,
                Shapes.box(-2F / 16F, 0F, -2F / 16F, 18F / 16F, 17F / 16F, 18F / 16F),
                type(MenuTypesAS.ALTAR_LUMINANCE, ContainerAltarLuminance::new, ContainerAltarLuminance::new)),
        RADIANCE(ResearchTier.RADIANCE, ObserversAS.STRUCTURE_ALTAR_T4, BlocksAS.ALTAR_RADIANCE,
                Shapes.box(-2F / 16F, 0F, -2F / 16F, 18F / 16F, 20F / 16F, 18F / 16F),
                type(MenuTypesAS.ALTAR_RADIANCE, ContainerAltarRadiance::new, ContainerAltarRadiance::new));

        public static final Codec<AltarType> CODEC = StringRepresentable.fromEnum(AltarType::values);
        public static final StreamCodec<FriendlyByteBuf, AltarType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(AltarType.class);

        private final ResearchTier requiredTier;
        private final ObserverRegistryObject requiredStructure;
        private final Supplier<? extends ItemLike> altarItemSupplier;
        private final VoxelShape shape;
        private final ContainerAltar.Type containerType;

        AltarType(ResearchTier requiredTier,
                  ObserverRegistryObject requiredStructure,
                  Supplier<? extends ItemLike> altarItemSupplier,
                  VoxelShape shape,
                  ContainerAltar.Type containerType) {
            this.requiredTier = requiredTier;
            this.requiredStructure = requiredStructure;
            this.altarItemSupplier = altarItemSupplier;
            this.shape = shape;
            this.containerType = containerType;
        }

        private static ContainerAltar.Type type(MenuTypeRegistryObject<? extends ContainerAltar> menuType,
                                                ContainerAltar.Provider provider,
                                                ContainerAltar.ClientProvider clientProvider) {
            return new ContainerAltar.Type(menuType, provider, clientProvider);
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public ResearchTier getRequiredTier() {
            return this.requiredTier;
        }

        public ObserverRegistryObject getRequiredStructure() {
            return this.requiredStructure;
        }

        public ItemStack getAltarItem() {
            return new ItemStack(this.altarItemSupplier.get());
        }

        public VoxelShape getShape() {
            return this.shape;
        }

        public ContainerAltar.Type getContainerType() {
            return this.containerType;
        }

        public boolean isThisLaterOrEqual(AltarType other) {
            return ordinal() >= other.ordinal();
        }

        public boolean isThisLater(AltarType other) {
            return ordinal() > other.ordinal();
        }
    }
}
