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
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXScaleFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.block.tile.LumenArrayBlock;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerViewFactory;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenStackList;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNetworkHelper;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNode;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestChain;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestHelper;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchMessageHelper;
import hellfirepvp.astralsorcery.common.tile.base.TileDataOwned;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityLumenDisplay;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.codec.CodecProducts;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryView;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryViewFactory;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.tank.FluidContainerList;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankViewFactory;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenArray
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenArray extends TileEntityTick<TileLumenArray.Data> implements TileEntityLumenDisplay {

    private boolean nodeAdded = false;

    private long lastSearchGameTime = 0L;
    private LumenGenerationRecipe activeRecipe = null;
    private final Map<Lumen, LumenRequestChain> activeRequestChains = new HashMap<>();

    public TileLumenArray(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.LUMEN_ARRAY, pos, blockState);
    }

    protected TileLumenArray(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (!this.nodeAdded) {
            this.nodeAdded = true;
            LumenNetworkHelper.createNode(level, this.getBlockPos(), this.getBlockPos().getCenter(), LumenNode.ConnectionType.SOURCE);
        }
        if (level.getGameTime() % 20L == 0 && !this.getTileData().getAssignedLumen().equals(LumenAS.NONE.get())) {
            LumenNetworkHelper.getNode(level, this.getBlockPos()).ifPresent(thisNode -> {
                Lumen assignedLumen = this.getTileData().getAssignedLumen();
                if (!thisNode.getProvidedLumenTypes().contains(assignedLumen)) {
                    LumenNetworkHelper.setNodeProvidedLumenTypes(level, thisNode, Set.of(assignedLumen));
                }
            });
        }

        this.doCraftingCycle(level);

        if (this.getTileData().getTicksExisted() % 20 == 0) {
            FluidStack stack = this.getTileData().getContainedFluid();
            if (!stack.isEmpty()) {
                this.setLight(level, stack.getFluidType().getLightLevel(stack));
            } else {
                this.setLight(level, 0);
            }
        }
    }

    private void doCraftingCycle(ServerLevel level) {
        LumenNode thisNode = LumenNetworkHelper.getNode(level, this.getBlockPos()).orElse(null);
        FluidStack starlight = this.getTileData().getFluidTank().getFluidInTank(0);
        ItemStack catalyst = this.getTileData().getInventory().getStackInSlot(0);
        if (thisNode == null || starlight.isEmpty() || catalyst.isEmpty()) {
            this.breakCatalyst();
            return;
        }

        if (this.activeRecipe == null) this.findRecipe(level, catalyst);
        if (this.activeRecipe == null) return;

        // Validate and build required lumen chains
        if (level.getGameTime() % 40L == 0) {
            this.activeRecipe.getLumenCombinationInputs().keySet().forEach(requiredType -> {
                LumenRequestChain requestChain = this.activeRequestChains.get(requiredType);
                if (requestChain != null) {
                    // Check if chain LOS is valid and target still has lumen
                    if (!requestChain.reValidateChain(level)) {
                        this.activeRequestChains.remove(requiredType);
                        requestChain = null;
                    } else {
                        LumenNode targetNode = requestChain.getEndNode();
                        LumenStack requiredPeek = LumenStack.of(requiredType, 100);
                        ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, targetNode.getPos(), null);
                        if (handler == null || handler.drain(requiredPeek, ILumenHandler.Action.SIMULATE).isEmpty()) {
                            this.activeRequestChains.remove(requiredType);
                            requestChain = null;
                        }
                    }
                }
                //Try build new chain
                if (requestChain == null) {
                    LumenStack requiredPeek = LumenStack.of(requiredType, 100);
                    LumenRequestHelper.requestDirect(level, thisNode, requiredPeek).ifPresent(chain -> {
                        this.activeRequestChains.put(requiredType, chain);
                    });
                }
            });
        }

        if (level.getGameTime() % 20L == 0) {
            int lumenGenerationAttempts = this.getLumenGenerationAttempts(this.activeRecipe.getProductionAttemptMultiplier());
            int starlightConsumptionAttempts = 2;

            boolean canGenerate = this.getBlockState().getValue(LumenArrayBlock.ENABLED);
            if (canGenerate) {
                //Generate lumen
                if (lumenGenerationAttempts > 0) {
                    int generatedLumenAmount = this.activeRecipe.getProducedLumenAmount() * lumenGenerationAttempts;
                    LumenStack newLumen = LumenStack.of(this.activeRecipe.getProducedLumen(), generatedLumenAmount);
                    int fillable = this.getTileData().getLumenHandler().fill(newLumen, ILumenHandler.Action.SIMULATE);
                    int fillableAttempts = fillable / this.activeRecipe.getProducedLumenAmount();
                    if (fillableAttempts > 0) {

                        //Evaluate up to how many sets can be drained at all
                        for (Lumen additionalInput : this.activeRecipe.getLumenCombinationInputs().keySet()) {
                            int additionalRequiredAmount = this.activeRecipe.getLumenCombinationInputs().get(additionalInput);

                            LumenRequestChain requestChain = this.activeRequestChains.get(additionalInput);
                            if (requestChain == null) {
                                fillableAttempts = 0;
                                break;
                            }
                            LumenStack drainAttempt = LumenStack.of(additionalInput, fillableAttempts * additionalRequiredAmount);
                            ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, requestChain.getEndNode().getPos(), null);
                            LumenStack drainable;
                            if (handler == null || (drainable = handler.drain(drainAttempt, ILumenHandler.Action.SIMULATE)).isEmpty()) {
                                fillableAttempts = 0;
                                break;
                            }
                            int drainableAttempts = drainable.getAmount() / additionalRequiredAmount;
                            if (drainableAttempts < fillableAttempts) {
                                fillableAttempts = drainableAttempts;
                            }
                            if (fillableAttempts <= 0) {
                                break;
                            }
                        }
                        if (fillableAttempts > 0) {
                            int requiredInputs = this.activeRecipe.getLumenCombinationInputs().size();

                            //Actually drain and produce lumen
                            for (Lumen additionalInput : this.activeRecipe.getLumenCombinationInputs().keySet()) {
                                int additionalRequiredAmount = this.activeRecipe.getLumenCombinationInputs().get(additionalInput);
                                LumenRequestChain requestChain = this.activeRequestChains.get(additionalInput);
                                if (requestChain != null) {
                                    int drainAmount = fillableAttempts * additionalRequiredAmount;
                                    LumenStack drainAttempt = LumenStack.of(additionalInput, drainAmount);
                                    LumenStack drained = LumenUtil.tryChainTransfer(ILumenHandler.voiding(), level, requestChain, drainAttempt, ILumenHandler.Action.EXECUTE);
                                    if (drained.getAmount() >= drainAmount) {
                                        requestChain.playTransferEffect(level, additionalInput);
                                        requiredInputs--;
                                    }
                                }
                            }

                            if (requiredInputs <= 0) {
                                LumenStack produced = LumenStack.of(this.activeRecipe.getProducedLumen(), fillableAttempts * this.activeRecipe.getProducedLumenAmount());
                                this.getTileData().getLumenHandler().fill(produced, ILumenHandler.Action.EXECUTE);

                                this.getTileData().getOwner(level).ifPresent(sPlayer -> {
                                    List<Lumen> newlyDiscovered = ResearchHelper.discoverLumen(sPlayer, RecipeUtil.findAnyLumenMakingUp(level, produced.getLumen()));
                                    if (!newlyDiscovered.isEmpty()) {
                                        ResearchMessageHelper.sendLumenDiscovery(sPlayer, newlyDiscovered);
                                    }
                                });
                            }
                        }
                    }
                }
            } else {
                lumenGenerationAttempts = rand.nextFloat() >= 0.95F ? 1 : 0;
                starlightConsumptionAttempts = 0;
            }

            //Consume liquid starlight
            int starlightConsumptions = Math.max(starlightConsumptionAttempts, lumenGenerationAttempts);
            starlightConsumptions = Math.max(lumenGenerationAttempts > 0 ? 1 : 0, Mth.ceil(starlightConsumptions * this.activeRecipe.getAttemptStarlightConsumption()));
            if (starlightConsumptions > 0) {
                FluidStack newStarlight = starlight.copy();
                newStarlight.shrink(starlightConsumptions);
                this.getTileData().getFluidContents().getTank(0).setContent(newStarlight);
                this.getTileData().markForUpdate();
            }

            if (this.activeRecipe.getCatalystShatterMultiplier() > 0) {
                int chance = Mth.ceil(1500 * (1F / this.activeRecipe.getCatalystShatterMultiplier()));
                if (rand.nextInt(Math.max(chance, 1)) == 0) {
                    this.breakCatalyst();
                }
            }
        }
    }

    private int getLumenGenerationAttempts(float attemptMultiplier) {
        int capacity = this.getTileData().getLumenHandler().getCapacity(this.getTileData().getAssignedLumen());
        int current = this.getTileData().getContainedLumen().map(LumenStack::getAmount).orElse(0);

        int segment = capacity / 8;
        int centerDistance = Math.abs(capacity / 2 - current);
        float attemptChance = attemptMultiplier;
        if (centerDistance > segment) {
            int maxDistance = capacity / 2 - segment;
            int segmentDistance = Math.min(centerDistance - segment, maxDistance);
            float multiplier = Mth.clamp(1F - (float) segmentDistance / (float) maxDistance, 0.05F, 0.8F);
            attemptChance *= multiplier;
        }
        attemptChance *= 0.5F;

        return MiscUtil.roundChanced(attemptChance, rand);
    }

    private void findRecipe(ServerLevel level, ItemStack catalyst) {
        if (level.getGameTime() - this.lastSearchGameTime < 100L) return;
        this.lastSearchGameTime = level.getGameTime();

        this.getTileData().findMatchingRecipe(catalyst)
                .map(RecipeHolder::value)
                .ifPresent(matchingRecipe -> {
                    this.activeRecipe = matchingRecipe;
                });
    }

    public void breakCatalyst() {
        InventoryView inv = this.getTileData().getInventory();
        if (!inv.getStackInSlot(0).isEmpty()) {
            inv.setStackInSlot(0, ItemStack.EMPTY);
            ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, this.getLevel(), this.getBlockPos(), 1F, 1F);
        }
        this.activeRecipe = null;
        this.activeRequestChains.clear();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        this.getTileData().getContainedLumen().ifPresent(lumenStack -> {
            Lumen lumen = lumenStack.getLumen();
            float filled = this.getTileData().getLumenFilledPercentage();

            Vector3 pos;
            if (rand.nextFloat() < filled * 2F) {

                pos = Vector3.atCenter(this).addY(1F);
                pos = VectorUtil.withRandomOffset(pos, rand, 0.05F);
                Vector3 dir = Vector3.random(rand).normalize().multiply((0.0015F + rand.nextFloat() * 0.0005F) * filled);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos.copy())
                        .setRoll(Mth.PI * 2F * rand.nextFloat())
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.15F + rand.nextFloat() * 0.1F)
                        .setGravity(dir)
                        .setMaxAge(15 + rand.nextInt(5));

                pos = Vector3.atCenter(this).addY(1F);
                pos = VectorUtil.withRandomOffset(pos, rand, 0.05F);
                dir = Vector3.random(rand).normalize().multiply((0.0015F + rand.nextFloat() * 0.0005F) * filled);
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos.copy())
                        .setRoll(Mth.PI * 2F * rand.nextFloat())
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.15F + rand.nextFloat() * 0.15F)
                        .color(FXColorFunction.constant(lumen.getColor(level, pos)))
                        .setGravity(dir)
                        .setMaxAge(20 + rand.nextInt(5));
            }

            if (rand.nextInt(20) == 0) {
                pos = Vector3.atCenter(this).addY(1F);
                pos = VectorUtil.withRandomOffset(pos, rand, 0.4F);
                EffectHelper.of(EffectTemplatesAS.LUMEN_PARTICLE)
                        .spawn(pos)
                        .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(lumenStack.getLumen()))
                        .setAlpha(0.75F)
                        .color(FXColorFunction.constant(lumen.getColor(level, pos)))
                        .setGravity(Vector3.y(0.0001F));
            }

            if (filled >= 0.8F && rand.nextFloat() < filled) {
                float dist = 5F / 16F;
                pos = Vector3.atBottomCenter(this).addY(1F)
                        .subtract(dist, 0, dist)
                        .add(rand.nextBoolean() ? 0 : dist * 2F, 0, rand.nextBoolean() ? 0 : dist * 2F);
                pos = VectorUtil.withRandomOffset(pos, rand, 0.02F);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setRoll(Mth.PI * 2F * rand.nextFloat())
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.15F + rand.nextFloat() * 0.1F)
                        .color(FXColorFunction.constant(lumen.getColor(level, pos)))
                        .setGravity(Vector3.y(0.0001F))
                        .setMaxAge(30 + rand.nextInt(15));
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .setRoll(Mth.PI * 2F * rand.nextFloat())
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.07F + rand.nextFloat() * 0.05F)
                        .color(FXColorFunction.WHITE)
                        .setGravity(Vector3.y(0.0001F))
                        .setMaxAge(30 + rand.nextInt(15));
            }
        });
    }

    @Override
    public Optional<StoredLumenDisplayTooltip> getDisplayTooltip() {
        if (this.getTileData().getLumenContents().isEmpty()) {
            int cap = this.getTileData().getLumenHandler().getCapacity(LumenAS.NONE.get());
            StoredLumenComponent.StoredLumen emptyStore = StoredLumenComponent.StoredLumen.of(LumenStack.EMPTY, cap);
            return Optional.of(StoredLumenDisplayTooltip.of(this.getDisplayComponentIdentifier(), emptyStore));
        }
        return TileEntityLumenDisplay.super.getDisplayTooltip();
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data implements TileDataOwned {

        public static final int LUMEN_TANK_CAPACITY = 4000;
        public static final int TANK_CAPACITY = 2000;

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> lumenArrayFields(inst).apply(inst, Data::new));

        protected static <T extends TileLumenArray.Data> Products.P9<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Optional<UUID>, LumenStackList, FluidContainerList, InventoryStackList, Boolean, Lumen> lumenArrayFields(RecordCodecBuilder.Instance<T> instance) {
            return CodecProducts.and(
                    TileEntityTick.Data.tickFields(instance),
                    TileDataOwned.ownedFields(instance),
                    instance.group(
                            CodecUtil.defaulted(LumenStackList.CODEC, "lumenContents", LumenStackList::create, Data::getLumenContents),
                            CodecUtil.defaulted(FluidContainerList.CODEC, "fluidContents", FluidContainerList::create, Data::getFluidContents),
                            CodecUtil.defaulted(InventoryStackList.CODEC, "inventoryContents", InventoryStackList::create, Data::getInventoryContents),
                            Codec.BOOL.optionalFieldOf("allowExtendedLumenTransfer", false).forGetter(Data::doesAllowExtendedLumenTransfer),
                            CodecUtil.lenientDefaulted(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), "assignedLumen", LumenAS.NONE, Data::getAssignedLumen)
                    )
            );
        }

        protected UUID ownerId;
        protected final LumenStackList lumenContents;
        protected final FluidContainerList fluidContents;
        protected final InventoryStackList inventoryContents;

        protected boolean allowExtendedLumenTransfer;
        protected Lumen assignedLumen;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       Optional<UUID> ownerId,
                       LumenStackList lumenContents,
                       FluidContainerList fluidContents,
                       InventoryStackList inventoryContents,
                       boolean allowExtendedLumenTransfer,
                       Lumen assignedLumen) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.ownerId = ownerId.orElse(null);
            this.lumenContents = lumenContents;
            this.fluidContents = fluidContents;
            this.inventoryContents = inventoryContents;
            this.allowExtendedLumenTransfer = allowExtendedLumenTransfer;
            this.assignedLumen = assignedLumen;
        }

        protected LumenStackList getLumenContents() {
            return this.lumenContents;
        }

        protected FluidContainerList getFluidContents() {
            return this.fluidContents;
        }

        protected InventoryStackList getInventoryContents() {
            return this.inventoryContents;
        }

        protected boolean doesAllowExtendedLumenTransfer() {
            return this.allowExtendedLumenTransfer;
        }

        protected Lumen getAssignedLumen() {
            return this.assignedLumen;
        }

        protected Optional<LumenStack> getContainedLumen() {
            return this.getLumenContents().getLumenStacks().stream()
                    .filter(lumenStack -> !lumenStack.isEmpty())
                    .findFirst();
        }

        public FluidStack getContainedFluid() {
            return this.fluidContents.getTank(0).getContent();
        }

        public float getLumenFilledPercentage() {
            return this.getContainedLumen()
                    .map(lumenStack -> (float) lumenStack.getAmount() / (float) this.getLumenHandler().getCapacity(lumenStack.getLumen()))
                    .orElse(0F);
        }

        protected Optional<RecipeHolder<LumenGenerationRecipe>> findMatchingRecipe(ItemStack stack) {
            return RecipeFinder.of()
                    .flatMap(finder -> finder.findLumenGenerationRecipe(stack, this.getAssignedLumen(), false));
        }

        private void updateExtendedTransferState() {
            if (this.assignedLumen == LumenAS.NONE.get()) {
                this.getContainedLumen().ifPresent(lumenStack -> {
                    this.assignedLumen = lumenStack.getLumen();
                });
            }

            if (this.doesAllowExtendedLumenTransfer()) {
                int containedAmount = this.getLumenContents().getLumenStacks().stream()
                        .findFirst()
                        .map(LumenStack::getAmount)
                        .orElse(0);
                if (containedAmount < LUMEN_TANK_CAPACITY / 8) {
                    this.allowExtendedLumenTransfer = false;
                } else if (containedAmount >= LUMEN_TANK_CAPACITY / 4) {
                    this.allowExtendedLumenTransfer = true;
                }
            }
        }

        protected LumenHandlerViewFactory newLumenHandler() {
            return LumenHandlerViewFactory.builder()
                    .accessibleSides(Direction.DOWN)
                    .tankCapacity(lumen -> LUMEN_TANK_CAPACITY)
                    .onChange(lumen -> this.updateExtendedTransferState())
                    .inputFilter((toAdd, existing) ->
                            this.getAssignedLumen().equals(LumenAS.NONE.get()) ||
                                    this.getAssignedLumen().equals(toAdd.getLumen()))
                    .extractFilter((amount, existing) ->
                            this.doesAllowExtendedLumenTransfer() ? existing.getAmount() >= LUMEN_TANK_CAPACITY / 4 : existing.getAmount() >= LUMEN_TANK_CAPACITY / 2);
        }

        public LumenHandlerView getLumenHandler() {
            return this.newLumenHandler().createTileView(this, this.getLumenContents());
        }

        protected FluidTankViewFactory newFluidTank() {
            return FluidTankViewFactory.builder(1)
                    .tankCapacity(tank -> TANK_CAPACITY)
                    .inputFilter((tank, stack, existing) -> stack.getFluid().isSame(FluidsAS.LIQUID_STARLIGHT.getSource().get()))
                    .extractFilter((tank, amount, existing) -> false)
                    .accessibleSides(Direction.DOWN);
        }

        public FluidTankView getFluidTank() {
            return this.newFluidTank().createTileView(this, this.fluidContents);
        }

        protected FilteredInventoryViewFactory newInventoryHandler() {
            return FilteredInventoryViewFactory.filteredBuilder(1)
                    .stackSizeLimiter((slot, stack) -> 1)
                    .extractFilter((slot, amount, existing) -> false)
                    .inputFilter((slot, toAdd, existing) -> {
                        if (!existing.isEmpty()) return false;
                        if (this.getFluidTank().getFluidInTank(0).isEmpty()) return false;

                        return this.findMatchingRecipe(toAdd).isPresent();
                    })
                    .accessibleSides(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        }

        public FilteredInventoryView getInventory() {
            return this.newInventoryHandler().createTileView(this, this.inventoryContents);
        }

        @Nullable
        @Override
        public UUID getOwnerId() {
            return this.ownerId;
        }

        @Override
        public void setOwnerId(UUID ownerId) {
            this.ownerId = ownerId;
        }
    }
}
