/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.entity.EntityAltarFluidInput;
import hellfirepvp.astralsorcery.common.entity.ItemEntityReplacement;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityAltarInput;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestHelper;
import hellfirepvp.astralsorcery.common.recipe.ActiveRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.AltarEffect;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import hellfirepvp.astralsorcery.common.util.data.LazyRecipeHolder;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveAltarRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ActiveAltarRecipe extends ActiveRecipe<AltarRecipe> {

    public static final Codec<ActiveAltarRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LazyRecipeHolder.typedCodec(RecipeTypesAS.ALTAR_CRAFTING_TYPE).fieldOf("recipe").forGetter(ActiveAltarRecipe::getRecipeReference),
            CodecUtil.uuidCodec().fieldOf("playerUUID").forGetter(ActiveAltarRecipe::getPlayerUUID),
            Direction.CODEC.fieldOf("recipeDirection").forGetter(ActiveAltarRecipe::getRecipeDirection),
            State.CODEC.fieldOf("craftingState").forGetter(ActiveAltarRecipe::getCraftingState),
            Codec.INT.fieldOf("tick").forGetter(recipe -> recipe.tick),
            Codec.INT.fieldOf("progressTick").forGetter(ActiveAltarRecipe::getProgressTick),
            Codec.list(LumenStack.CODEC).fieldOf("drawnLumen").forGetter(recipe -> recipe.drawnLumen),
            Codec.unboundedMap(CodecUtil.stringInteger(), FluidStack.CODEC).fieldOf("drawnFluid").forGetter(recipe -> recipe.drawnFluid),
            AdditionalInput.CODEC.listOf().fieldOf("capturedEntityUUIDs").forGetter(ActiveAltarRecipe::getAdditionalInputs)
    ).apply(inst, ActiveAltarRecipe::new));

    private final AABB CAPTURE_AREA = new AABB(-3, 0, -3, 4, 2, 4);

    private final UUID playerUUID;
    private final Direction recipeDirection;

    private State craftingState = State.ACTIVE;
    private int tick = 0;
    private int progressTick = 0;
    private final List<LumenStack> drawnLumen = new ArrayList<>();
    private final Map<Integer, FluidStack> drawnFluid = new HashMap<>();
    private final List<ActiveAltarRecipe.AdditionalInput> additionalInputs = new ArrayList<>();

    private final Map<AltarEffect, CompoundTag> activeEffectData = new HashMap<>();

    private ActiveAltarRecipe(LazyRecipeHolder<AltarRecipe> recipeReference,
                              UUID playerUUID,
                              Direction recipeDirection,
                              State craftingState,
                              int tick,
                              int progressTick,
                              List<LumenStack> drawnLumen,
                              Map<Integer, FluidStack> drawnFluid,
                              List<ActiveAltarRecipe.AdditionalInput> capturedEntityUUIDs) {
        super(recipeReference);
        this.playerUUID = playerUUID;
        this.recipeDirection = recipeDirection;
        this.craftingState = craftingState;
        this.tick = tick;
        this.progressTick = progressTick;
        this.drawnLumen.addAll(drawnLumen);
        this.drawnFluid.putAll(drawnFluid);
        this.additionalInputs.addAll(capturedEntityUUIDs);
    }

    private ActiveAltarRecipe(RecipeHolder<AltarRecipe> recipe, UUID playerUUID, Direction recipeDirection) {
        super(LazyRecipeHolder.of(recipe));
        this.playerUUID = playerUUID;
        this.recipeDirection = recipeDirection;
    }

    public static ActiveAltarRecipe of(RecipeHolder<AltarRecipe> recipe, UUID playerUUID, Direction recipeDirection) {
        return new ActiveAltarRecipe(recipe, playerUUID, recipeDirection);
    }

    public State getCraftingState() {
        return this.craftingState;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Direction getRecipeDirection() {
        return this.recipeDirection;
    }

    public List<LumenStack> getDrawnLumen() {
        return Collections.unmodifiableList(this.drawnLumen);
    }

    public Map<Integer, FluidStack> getDrawnFluid() {
        return Collections.unmodifiableMap(this.drawnFluid);
    }

    public List<AdditionalInput> getAdditionalInputs() {
        return Collections.unmodifiableList(this.additionalInputs);
    }

    public int getTick() {
        return this.tick;
    }

    public int getProgressTick() {
        return this.progressTick;
    }

    public boolean isFinished(Level level) {
        return this.getCraftingState().canProgress() &&
                this.getRecipe(level).map(AltarRecipe::getDuration)
                        .map(duration -> this.getProgressTick() >= duration)
                        .orElse(false);
    }

    public boolean matches(ServerLevel level, TileAltar altar) {
        AltarCraftingInput input = altar.createInput(level, this.getPlayerUUID());
        return this.getRecipe(level).map(recipe -> {
            return recipe.matches(input, level);
        }).orElse(false);
    }

    public void tick(Level level) {
        this.tick++;

        if (this.getCraftingState().canProgress()) {
            this.progressTick++;
        } else {
            this.progressTick = 0;
        }
    }

    public void tickEffects(Level level, TileAltar altar) {
        if (!level.isClientSide()) return;
        this.getRecipe(level).map(AltarRecipe::getEffects).ifPresent(effects -> {
            int tick = this.getTick();
            effects.forEach(effect -> {
                RandomSource effectRand = RandomSource.create(effect.hashCode() ^ (long) tick << 32 ^ tick);
                CompoundTag data = this.activeEffectData.computeIfAbsent(effect, eff -> new CompoundTag());
                effect.tick(level, altar, effectRand, this, this.getProgressTick(), tick, this.getCraftingState(), data);
            });
        });
    }

    public void tickTrackAdditionalInputs(TileAltar altar, ServerLevel level, BlockPos altarPos, Map<BaseConstellation, Float> aggregateStarlight) {
        AltarRecipe recipe = this.getRecipe(level).orElse(null);
        if (recipe == null) return;

        AABB captureBox = this.CAPTURE_AREA.move(altarPos);
        List<CountIngredient> additionalInputs = recipe.getRequiredAdditionalInputs();
        for (int itemIndex = 0; itemIndex < additionalInputs.size(); itemIndex++) {
            CountIngredient ingredient = additionalInputs.get(itemIndex);
            if (ingredient.isEmpty()) continue;

            //create
            AdditionalInput input = this.getAdditionalInput(true, itemIndex);
            if (input == null) {
                input = AdditionalInput.itemInput(itemIndex);
                this.additionalInputs.add(input);
            }

            //vaildate
            if (input.capturedEntityUUID != null) {
                Entity captured = level.getEntity(input.capturedEntityUUID);
                if (captured == null ||
                        !captured.isAlive() ||
                        captured.isRemoved() ||
                        !(captured instanceof ItemEntityAltarInput itemEntityInput)) {
                    input.capturedEntityUUID = null;
                } else {
                    itemEntityInput.refreshReference(altar.getBlockPos(), input);
                }
            }

            //instantiate
            if (input.capturedEntityUUID == null) {
                List<ItemEntity> eligibleItems = level.getEntities(EntityTypeTest.forClass(ItemEntity.class), captureBox, itemEntity -> {
                    return itemEntity.isAlive() &&
                            !itemEntity.hasPickUpDelay() &&
                            !itemEntity.getItem().isEmpty() &&
                            ingredient.ingredient().test(itemEntity.getItem());
                });
                eligibleItems = this.removeCapturedEntities(eligibleItems);
                if (eligibleItems.isEmpty()) continue;

                List<List<ItemEntity>> merged = ItemUtil.collectMergeableItems(eligibleItems);
                MiscUtil.shuffle(merged);

                for (List<ItemEntity> group : merged) {
                    if (group.isEmpty()) continue;
                    int count = group.stream().mapToInt(itemEntity -> itemEntity.getItem().getCount()).sum();
                    if (count < ingredient.count()) continue;

                    ItemEntityAltarInput inputEntity = ItemEntityReplacement.replace(EntitiesAS.ITEM_ALTAR_INPUT.get(), group.getFirst());
                    inputEntity.setUUID(UUID.randomUUID());
                    inputEntity.setDeltaMovement(Vec3.ZERO);
                    inputEntity.setUnlimitedLifetime();
                    level.addFreshEntity(inputEntity);

                    ItemStack mergedOut = group.getFirst().getItem();
                    mergedOut = mergedOut.copyWithCount(0);
                    for (ItemEntity entity : group) {
                        ItemStack stack = entity.getItem();
                        int toExtract = Math.min(stack.getCount(), ingredient.count() - mergedOut.getCount());
                        if (toExtract <= 0) break;

                        stack.shrink(toExtract);
                        mergedOut.grow(toExtract);

                        entity.setItem(stack);
                        if (stack.isEmpty()) {
                            entity.discard();
                        }
                    }

                    inputEntity.setItem(mergedOut);
                    inputEntity.refreshReference(altar.getBlockPos(), input);
                    input.capturedEntityUUID = inputEntity.getUUID();
                }
            }
        }

        List<FluidStack> requiredFluid = recipe.getRequiredFluid();
        for (int fluidIndex = 0; fluidIndex < requiredFluid.size(); fluidIndex++) {
            FluidStack fluid = requiredFluid.get(fluidIndex);
            if (fluid.isEmpty()) continue;

            AdditionalInput input = this.getAdditionalInput(false, fluidIndex);
            if (input == null) {
                input = AdditionalInput.fluidInput(fluidIndex);
                this.additionalInputs.add(input);
            }

            if (input.capturedEntityUUID != null) {
                Entity captured = level.getEntity(input.capturedEntityUUID);
                if (captured == null ||
                        !captured.isAlive() ||
                        captured.isRemoved() ||
                        !(captured instanceof EntityAltarFluidInput fluidInput) ||
                        !FluidStack.isSameFluidSameComponents(fluid, fluidInput.getFluid())) {
                    input.capturedEntityUUID = null;
                } else {
                    fluidInput.refreshReference(altar.getBlockPos(), input, fluid);
                }
            }

            if (input.capturedEntityUUID == null) {
                EntityAltarFluidInput inputEntity = EntitiesAS.FLUID_ALTAR_INPUT.get().create(level);
                if (inputEntity == null) throw new IllegalStateException("Cannot create fluid input tracking entity.");
                inputEntity.setUUID(UUID.randomUUID());
                inputEntity.setPos(altarPos.getCenter().add(0, 1, 0));
                inputEntity.setDeltaMovement(Vec3.ZERO);
                level.addFreshEntity(inputEntity);

                inputEntity.refreshReference(altar.getBlockPos(), input, fluid);
                input.capturedEntityUUID = inputEntity.getUUID();
            }

            if ((altar.getTileData().getTicksExisted() + 10) % 40 == 0) {
                FluidStack drawn = this.drawnFluid.getOrDefault(fluidIndex, FluidStack.EMPTY);

                int amtRequired = Math.min(250, fluid.getAmount() - drawn.getAmount());
                FluidStack requested = fluid.copyWithAmount(amtRequired);
                input.fluidDrawInstance.update(level, altarPos, requested);
                if (input.fluidDrawInstance.consumeLiquid(level, altarPos, requested, true)) {
                    input.fluidDrawInstance.consumeLiquid(level, altarPos, requested, false);
                    this.drawnFluid.put(fluidIndex, requested.copyWithAmount(drawn.getAmount() + requested.getAmount()));
                }
            }
        }

        Collections.sort(this.additionalInputs);
        MiscUtil.shuffle(this.additionalInputs, RandomSource.create(42));

        Vector3 effectCenter = Vector3.atCenter(altar).addY(1.2F);
        for (int i = 0; i < this.additionalInputs.size(); i++) {
            AdditionalInput input = this.additionalInputs.get(i);

            float angle = (altar.getTileData().getTicksExisted() * 1.5F) % 360F + (i / (float) this.additionalInputs.size()) * 360F;

            Vector3 offset = Vector3.RotAxis.X_AXIS.getVector().rotate((float) Math.toRadians(angle), Vector3.RotAxis.Y_AXIS);
            input.targetPosition = effectCenter.copy().add(offset.normalize().multiply(2F));
        }

        if (altar.getTileData().getTicksExisted() % 40 == 0) {
            List<LumenStack> requiredLumen = recipe.getRequiredLumen();
            for (LumenStack required : requiredLumen) {
                if (required.isEmpty()) continue;

                LumenStack drawn = this.drawnLumen.stream()
                        .filter(lumen -> lumen.isSameLumen(required))
                        .findFirst()
                        .orElse(LumenStack.EMPTY);
                int neededAmt = Math.min(100, required.getAmount() - drawn.getAmount());
                if (neededAmt > 0) {
                    LumenStack draw = required.copyWithAmount(neededAmt);
                    boolean didTransfer = LumenRequestHelper.requestRelayed(level, altarPos, draw).map(chain -> {
                        ILumenHandler handler = level.getCapability(ILumenHandler.BLOCK, chain.getEndNode().getPos(), null);
                        if (handler != null) {
                            LumenStack drained = handler.drain(draw, ILumenHandler.Action.EXECUTE);
                            if (!drained.isEmpty()) {
                                if (!drawn.isEmpty()) {
                                    drawn.grow(drained.getAmount());
                                } else {
                                    this.drawnLumen.add(drained.copy());
                                }
                                chain.playTransferEffect(level, drained.getLumen());
                                return true;
                            }
                        }
                        return false;
                    }).orElse(false);

                    if (didTransfer) {
                        break;
                    }
                }
            }
        }

        boolean valid = true;
        List<CountIngredient> requiredAdditionalInputs = recipe.getRequiredAdditionalInputs();
        for (int itemIndex = 0; itemIndex < requiredAdditionalInputs.size(); itemIndex++) {
            CountIngredient ingredient = requiredAdditionalInputs.get(itemIndex);

            AdditionalInput input = this.getAdditionalInput(true, itemIndex);
            if (input == null || input.capturedEntityUUID == null) {
                valid = false;
                break;
            }

            Entity captured = level.getEntity(input.capturedEntityUUID);
            if (!(captured instanceof ItemEntityAltarInput itemEntity) ||
                    !captured.isAlive() ||
                    captured.isRemoved() ||
                    !ingredient.ingredient().test(itemEntity.getItem())) {
                valid = false;
                break;
            }
        }
        if (!valid) {
            this.craftingState = State.WAITING_ADDITIONAL_INPUTS;
            return;
        }

        for (int fluidIndex = 0; fluidIndex < requiredFluid.size(); fluidIndex++) {
            FluidStack required = requiredFluid.get(fluidIndex);
            if (required.isEmpty()) continue;

            AdditionalInput input = this.getAdditionalInput(false, fluidIndex);
            if (input == null || input.capturedEntityUUID == null) {
                valid = false;
                break;
            }

            Entity captured = level.getEntity(input.capturedEntityUUID);
            if (!(captured instanceof EntityAltarFluidInput fluidInput) ||
                    !captured.isAlive() ||
                    captured.isRemoved() ||
                    !FluidStack.isSameFluidSameComponents(required, fluidInput.getFluid())) {
                valid = false;
                break;
            }

            FluidStack drawn = this.drawnFluid.get(fluidIndex);
            if (drawn == null) drawn = FluidStack.EMPTY;
            if (drawn.getAmount() < required.getAmount()) {
                valid = false;
                break;
            }
        }
        if (!valid) {
            this.craftingState = State.WAITING_ADDITIONAL_INPUTS;
            return;
        }

        List<LumenStack> requiredLumen = recipe.getRequiredLumen();
        for (LumenStack required : requiredLumen) {
            if (required.isEmpty()) continue;

            LumenStack drawn = this.drawnLumen.stream()
                    .filter(lumen -> lumen.isSameLumen(required))
                    .findFirst()
                    .orElse(LumenStack.EMPTY);
            if (drawn.getAmount() < required.getAmount()) {
                valid = false;
                break;
            }
        }
        if (!valid) {
            this.craftingState = State.WAITING_ADDITIONAL_INPUTS;
            return;
        }

        for (BaseConstellation cst : recipe.getRequiredStarlight()) {
            float available = aggregateStarlight.getOrDefault(cst, 0F);
            if (available <= 0) {
                valid = false;
                break;
            }
        }
        if (!valid) {
            this.craftingState = State.WAITING_STARLIGHT_LEVELS;
            return;
        }

        this.craftingState = State.ACTIVE;
    }

    @Nullable
    public AdditionalInput getAdditionalInput(boolean isItem, int index) {
        return this.additionalInputs.stream()
                .filter(input -> input.isItem == isItem && input.inputIndex == index)
                .findFirst()
                .orElse(null);
    }

    private List<ItemEntity> removeCapturedEntities(List<ItemEntity> entities) {
        List<UUID> capturedUUIDs = this.getAdditionalInputs().stream()
                .filter(input -> input.capturedEntityUUID != null)
                .map(input -> input.capturedEntityUUID)
                .toList();
        return entities.stream()
                .filter(entity -> !capturedUUIDs.contains(entity.getUUID()))
                .toList();
    }

    public boolean consumeItemInputs(TileAltar.Data altarData, ServerLevel level, BlockPos altarPos, boolean simulate) {
        AltarRecipe recipe = this.getRecipe(level).orElse(null);
        if (recipe == null) return false;

        AltarRecipeGrid grid = recipe.getGrid();
        List<IngredientBridge> inputs = grid.getInputs();
        List<IngredientBridge> relayInputs = grid.getRelayInputs();

        boolean validInputs = true;

        InventoryView altarInv = altarData.getAltarInventory();
        for (int slot = 0; slot < inputs.size(); slot++) {
            IngredientBridge ingredient = inputs.get(slot);
            if (ingredient.isEmpty()) continue;

            ItemStack slotStack = altarInv.getStackInSlot(slot);
            if (slotStack.isEmpty() || !ingredient.test(slotStack)) {
                validInputs = false;
                continue;
            }

            int invSlot = slot;
            if (!ingredient.consume(
                    () -> altarInv.extractItem(invSlot, 1, simulate),
                    stack -> altarInv.insertItem(invSlot, stack, simulate),
                    remainder -> ItemUtil.dropItemNaturally(level, altarPos.above(), remainder),
                    simulate)) {
                validInputs = false;
            }
        }

        Map<Integer, BlockPos> offsets = TileAltar.getRelayGridOffsets();
        for (int relaySlot = 0; relaySlot < relayInputs.size(); relaySlot++) {
            BlockPos offset = offsets.get(relaySlot);
            if (offset == null) continue;

            IngredientBridge ingredient = relayInputs.get(relaySlot);
            if (ingredient.isEmpty()) continue;

            TileFocusRelay relay = MiscUtil.getTileAt(level, altarPos.offset(offset), TileFocusRelay.class, true).orElse(null);
            if (relay == null) {
                validInputs = false;
                continue;
            }

            InventoryView relayInv = relay.getTileData().getInventory();
            ItemStack relayStack = relayInv.getStackInSlot(0);
            if (relayStack.isEmpty() || !ingredient.test(relayStack)) {
                validInputs = false;
                continue;
            }

            if (!ingredient.consume(
                    () -> relayInv.extractItem(0, 1, simulate),
                    stack -> relayInv.insertItem(0, stack, simulate),
                    remainder -> ItemUtil.dropItemNaturally(level, relay.getBlockPos().above(), remainder),
                    simulate)) {
                validInputs = false;
            }
        }

        List<CountIngredient> additionalInputs = recipe.getRequiredAdditionalInputs();
        for (int itemIndex = 0; itemIndex < additionalInputs.size(); itemIndex++) {
            CountIngredient ingredient = additionalInputs.get(itemIndex);
            if (ingredient.isEmpty()) continue;

            AdditionalInput input = this.getAdditionalInput(true, itemIndex);
            if (input == null || input.capturedEntityUUID == null) {
                validInputs = false;
                continue;
            }

            Entity entity = level.getEntity(input.capturedEntityUUID);
            if (!(entity instanceof ItemEntityAltarInput itemEntity) ||
                    !entity.isAlive() ||
                    entity.isRemoved()) {
                validInputs = false;
                continue;
            }

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty() || !ingredient.ingredient().test(stack)) {
                validInputs = false;
                continue;
            }

            if (!simulate) {
                itemEntity.discard();
            }
        }

        List<FluidStack> additionalFluids = recipe.getRequiredFluid();
        for (int fluidIndex = 0; fluidIndex < additionalFluids.size(); fluidIndex++) {
            FluidStack required = additionalFluids.get(fluidIndex);
            if (required.isEmpty()) continue;

            AdditionalInput input = this.getAdditionalInput(false, fluidIndex);
            if (input == null || input.capturedEntityUUID == null) {
                validInputs = false;
                continue;
            }

            Entity entity = level.getEntity(input.capturedEntityUUID);
            if (!(entity instanceof EntityAltarFluidInput fluidEntity) ||
                    !entity.isAlive() ||
                    entity.isRemoved()) {
                validInputs = false;
                continue;
            }

            FluidStack fluid = fluidEntity.getFluid();
            if (fluid.isEmpty() || !FluidStack.isSameFluidSameComponents(required, fluid)) {
                validInputs = false;
                continue;
            }

            FluidStack drawnFluid = this.drawnFluid.get(fluidIndex);
            if (drawnFluid == null) drawnFluid = FluidStack.EMPTY;
            if (drawnFluid.getAmount() < required.getAmount()) {
                validInputs = false;
                continue;
            }

            if (!simulate) {
                fluidEntity.discard();
            }
        }

        List<LumenStack> requiredLumen = recipe.getRequiredLumen();
        for (LumenStack required : requiredLumen) {
            if (required.isEmpty()) continue;

            LumenStack drawn = this.drawnLumen.stream()
                    .filter(lumen -> lumen.isSameLumen(required))
                    .findFirst()
                    .orElse(LumenStack.EMPTY);
            if (drawn.isEmpty() || drawn.getAmount() < required.getAmount()) {
                validInputs = false;
            }
        }

        return validInputs;
    }

    @OnlyIn(Dist.CLIENT)
    public void playLiquidDrawEffects(Level level, BlockPos centerPos) {
        this.getRecipe(level).ifPresent(recipe -> {
            AABB searchBox = new AABB(centerPos).inflate(10, 6, 10);
            List<FluidStack> required = recipe.getRequiredFluid();
            this.getAdditionalInputs().forEach(input -> {
                if (required.size() <= input.getInputIndex()) return;
                if (input.capturedEntityUUID == null || input.isItem()) return;
                Entity entity = level.getEntities(EntityTypeTest.forClass(EntityAltarFluidInput.class), searchBox,
                        inputEntity -> inputEntity.isAlive() && inputEntity.getUUID().equals(input.capturedEntityUUID))
                        .stream()
                        .findFirst()
                        .orElse(null);
                if (entity == null) {
                    return;
                }

                FluidStack drawnFluid = this.drawnFluid.get(input.getInputIndex());
                if (drawnFluid == null || drawnFluid.isEmpty()) {
                    return;
                }

                FluidStack requiredFluid = required.get(input.getInputIndex());
                if (requiredFluid.isEmpty()) {
                    return;
                }
                if (drawnFluid.getAmount() >= requiredFluid.getAmount()) {
                    return;
                }

                int amtSearch = Math.min(250, requiredFluid.getAmount() - drawnFluid.getAmount());
                if (amtSearch <= 0) {
                    return;
                }
                input.fluidDrawInstance.update(level, centerPos, drawnFluid.copyWithAmount(amtSearch));
                input.fluidDrawInstance.playLiquidDrawEffect(level, () -> new Vector3(entity), drawnFluid, 0.1F, 0.14F);
            });
        });
    }

    public void copyEffectDataTo(ActiveAltarRecipe newActiveRecipe) {
        this.activeEffectData.forEach((effect, data) -> {
            CompoundTag newDataTag = newActiveRecipe.activeEffectData.computeIfAbsent(effect, eff -> new CompoundTag());
            effect.copyEffectData(data, newDataTag);
        });
    }

    public enum State implements StringRepresentable {

        ACTIVE,
        WAITING_STARLIGHT_LEVELS,
        WAITING_ADDITIONAL_INPUTS;

        public static final Codec<State> CODEC = StringRepresentable.fromEnum(State::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean canProgress() {
            return this == ACTIVE;
        }
    }

    public static class AdditionalInput implements Comparable<AdditionalInput> {

        public static final Codec<AdditionalInput> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Vector3.CODEC.fieldOf("targetPosition").forGetter(input -> input.targetPosition),
                Codec.BOOL.fieldOf("isItem").forGetter(input -> input.isItem),
                Codec.INT.fieldOf("inputIndex").forGetter(input -> input.inputIndex),
                CodecUtil.uuidCodec().optionalFieldOf("capturedEntityUUID").forGetter(AdditionalInput::getCapturedEntityUUID)
        ).apply(inst, AdditionalInput::new));
        public static final StreamCodec<? super RegistryFriendlyByteBuf, AdditionalInput> STREAM_CODEC = StreamCodec.composite(
                Vector3.STREAM_CODEC,
                input -> input.targetPosition.copy(),
                ByteBufCodecs.BOOL,
                AdditionalInput::isItem,
                ByteBufCodecs.INT,
                AdditionalInput::getInputIndex,
                ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC),
                input -> Optional.ofNullable(input.capturedEntityUUID),
                AdditionalInput::new);

        public static final AdditionalInput EMPTY = new AdditionalInput(new Vector3(), true, -1, Optional.empty());

        private Vector3 targetPosition;

        private final boolean isItem;
        private final int inputIndex;

        // Either item altar input or fluid input
        private UUID capturedEntityUUID;

        //temp
        private final TileChalice.LiquidDrawInstance fluidDrawInstance = TileChalice.LiquidDrawInstance.newInstance();

        private AdditionalInput(Vector3 targetPosition,
                                boolean isItem,
                                int inputIndex,
                                Optional<UUID> capturedEntityUUID) {
            this.targetPosition = targetPosition;
            this.isItem = isItem;
            this.inputIndex = inputIndex;
            this.capturedEntityUUID = capturedEntityUUID.orElse(null);
        }

        public Vector3 getTargetPosition() {
            return this.targetPosition.copy();
        }

        public boolean isItem() {
            return this.isItem;
        }

        public int getInputIndex() {
            return this.inputIndex;
        }

        public Optional<UUID> getCapturedEntityUUID() {
            return Optional.ofNullable(this.capturedEntityUUID);
        }

        public static AdditionalInput itemInput(int ingredientIndex) {
            return new AdditionalInput(new Vector3(), true, ingredientIndex, Optional.empty());
        }

        public static AdditionalInput fluidInput(int fluidIndex) {
            return new AdditionalInput(new Vector3(), false, fluidIndex, Optional.empty());
        }

        public boolean isValid(ServerLevel sLevel, TileAltar altar) {
            if (this.capturedEntityUUID == null) return false;
            Entity entity = sLevel.getEntity(this.capturedEntityUUID);
            if (entity == null || !entity.isAlive() || entity.isRemoved()) return false;
            if (this.isItem()) {
                if (!(entity instanceof ItemEntityAltarInput itemEntity)) return false;

                Ingredient ingredient = altar.getTileData().getActiveRecipe()
                        .map(recipe -> recipe.getRecipe(sLevel))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(recipe -> recipe.getRequiredAdditionalInputs().get(this.getInputIndex()))
                        .map(CountIngredient::ingredient)
                        .orElse(null);
                if (ingredient == null) return false;
                return ingredient.test(itemEntity.getItem());
            } else {
                if (!(entity instanceof EntityAltarFluidInput fluidEntity)) return false;

                FluidStack requiredFluid = altar.getTileData().getActiveRecipe()
                        .map(recipe -> recipe.getRecipe(sLevel))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(recipe -> recipe.getRequiredFluid().get(this.getInputIndex()))
                        .orElse(FluidStack.EMPTY);
                if (requiredFluid.isEmpty()) return false;
                return FluidStack.isSameFluidSameComponents(requiredFluid, fluidEntity.getFluid());
            }
        }

        @Override
        public int compareTo(@Nonnull AdditionalInput o) {
            if (this.isItem != o.isItem) {
                return Boolean.compare(this.isItem, o.isItem);
            }
            return Integer.compare(this.inputIndex, o.inputIndex);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            AdditionalInput input = (AdditionalInput) o;
            return isItem == input.isItem &&
                    inputIndex == input.inputIndex &&
                    Objects.equals(targetPosition, input.targetPosition) &&
                    Objects.equals(capturedEntityUUID, input.capturedEntityUUID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetPosition, isItem, inputIndex, capturedEntityUUID);
        }
    }
}
