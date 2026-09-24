/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXMotionFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRefreshFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXOrbitalSource;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXInfuserOrbitalSource;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.client.util.SoundUtil;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestHelper;
import hellfirepvp.astralsorcery.common.recipe.ActiveRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.ActiveInfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipeInput;
import hellfirepvp.astralsorcery.common.sound.CategorizedSoundEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryViewFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileInfuser
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileInfuser extends TileEntityTick<TileInfuser.Data> {

    private static final Set<BlockPos> LIQUID_OFFSETS = ImmutableSet.of(
            new BlockPos( 1, -1,  2),
            new BlockPos( 0, -1,  2),
            new BlockPos(-1, -1,  2),

            new BlockPos( 1, -1, -2),
            new BlockPos( 0, -1, -2),
            new BlockPos(-1, -1, -2),

            new BlockPos( 2, -1,  1),
            new BlockPos( 2, -1,  0),
            new BlockPos( 2, -1, -1),

            new BlockPos(-2, -1,  1),
            new BlockPos(-2, -1,  0),
            new BlockPos(-2, -1, -1)
    );

    private final ClientObject<FXOrbitalSource> orbitalLiquid = new ClientObject<>();
    private final ClientObject<PlayableSoundInstance> craftingSound = new ClientObject<>();

    public TileInfuser(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.INFUSER, pos, blockState);
    }

    protected TileInfuser(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static Set<BlockPos> getLiquidOffsets() {
        return LIQUID_OFFSETS;
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        this.doCraftingCycle(level);

        //if (this.getTileData().getTicksExisted() % 30 == 0) {
        //    LumenRequestHelper.requestRelayed(level, this.getBlockPos(), LumenAS.PRISMATIC.stack(100)).ifPresent(chain -> {
        //        chain.playTransferEffect(level, LumenAS.PRISMATIC.asLumen());
        //    });
        //}
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
            InfusionRecipe recipe = activeRecipe.getRecipe(level).orElse(null);
            if (recipe == null) {
                this.abortCrafting();
                return;
            }

            Data data = this.getTileData();
            activeRecipe.tick(level);

            if (activeRecipe.isFinished(level) && activeRecipe.consumeInputs(this, level)) {
                data.getInventory().setStackInSlot(0, ItemStack.EMPTY);
                ItemUtil.dropItem(level, this.getBlockPos().above(), recipe.getOutput());

                data.knownRecipes.add(activeRecipe.getRecipeId());

                ServerSoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_FINISH, level, Vector3.atCenter(this), 1F, 1F);
                data.setActiveRecipe(null);
            }

            data.markForUpdate();
        });
    }

    public Optional<RecipeHolder<InfusionRecipe>> findMatchingRecipe(Level level) {
        return level.getRecipeManager().getRecipeFor(RecipeTypesAS.INFUSION_TYPE.get(), this.createInput(level), level);
    }

    public InfusionRecipeInput createInput(Level level) {
        Map<BlockPos, FluidState> liquidInputs = LIQUID_OFFSETS.stream().map(offset -> {
            BlockPos pos = this.getBlockPos().offset(offset);
            return new Tuple<>(pos, level.getFluidState(pos));
        }).collect(Collectors.toMap(Tuple::getA, Tuple::getB));
        return InfusionRecipeInput.of(this.getTileData().getInventory().getStackInSlot(0), level, liquidInputs);
    }

    public void startCrafting(Level level, RecipeHolder<InfusionRecipe> recipe) {
        if (this.getTileData().getActiveRecipe().isPresent()) {
            this.abortCrafting();
        }

        ActiveInfusionRecipe infusionRecipe = ActiveInfusionRecipe.of(recipe);
        this.getTileData().setActiveRecipe(infusionRecipe);
        this.getTileData().markForUpdate();

        ServerSoundHelper.playSoundAround(SoundsAS.INFUSER_CRAFT_START, level, Vector3.atCenter(this), 1F, 1F);
    }

    private void abortCrafting() {
        this.getTileData().setActiveRecipe(null);
        this.getTileData().markForUpdate();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        ActiveInfusionRecipe activeRecipe = this.getTileData().getActiveRecipe().orElse(null);
        if (activeRecipe == null) return;
        InfusionRecipe recipe = activeRecipe.getRecipe(level).orElse(null);
        if (recipe == null) return;
        FluidStack requiredInput = new FluidStack(recipe.getFluidInput(), FluidType.BUCKET_VOLUME);

        this.playCraftingSound();
        this.playOrbitalEffect(activeRecipe, requiredInput);
        for (int i = 0; i < 2; i++) {
            this.playLiquidBubbleEffect(requiredInput);
        }
        for (int i = 0; i < 7; i++) {
            this.playLiquidPoolEffect(requiredInput);
        }

        activeRecipe.getDrawInstance().playLiquidDrawEffect(level, Vector3.atCenter(this).addY(0.6F), requiredInput);
    }

    @OnlyIn(Dist.CLIENT)
    private void playCraftingSound() {
        if (SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0) return;

        if (this.craftingSound.isNull() || this.craftingSound.get().hasStoppedPlaying()) {
            PlayableSoundInstance sound = PlayableSoundInstance.of(SoundsAS.INFUSER_CRAFT_LOOP)
                    .pos(Vector3.atCenter(this))
                    .loop(true)
                    .fadeInTicks(30)
                    .fadeOutTicks(20)
                    .stopFunction(inst -> {
                        return this.isRemoved() ||
                                SoundUtil.getSoundVolume(SoundSource.BLOCKS) <= 0 ||
                                this.getTileData().getActiveRecipe().isEmpty();
                    })
                    .play();

            this.craftingSound.set(sound);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playOrbitalEffect(ActiveInfusionRecipe recipe, FluidStack input) {
        if (this.orbitalLiquid.isNull() || this.orbitalLiquid.get().isRemoved()) {
            ResourceLocation recipeId = recipe.getRecipeId();
            FXOrbitalSource src = EffectHelper.source(
                    new FXInfuserOrbitalSource(new Vector3(this).add(0.5, 0, 0.5), input)
                            .setOrbitAxis(Vector3.RotAxis.Y_AXIS)
                            .setOrbitRadius(2F)
                            .setOrbitalPoints(4)
                            .setMaxAge(300)
                            .refresh(FXRefreshFunction.tileExistsAnd(this,
                                    (infuser, fx) -> infuser.getTileData().getActiveRecipe().map(ActiveRecipe::getRecipeId)
                                            .map(id -> id.equals(recipeId)).orElse(false))));

            this.orbitalLiquid.set(src);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playLiquidBubbleEffect(FluidStack input) {
        Vector3 offset = Vector3.atBottomCenter(this).addY(0.8F);
        switch (rand.nextInt(4)) {
            case 0: offset.addX( 0.375); break;
            case 1: offset.addX(-0.375); break;
            case 2: offset.addZ( 0.375); break;
            case 3: offset.addZ(-0.375); break;
        }
        offset = VectorUtil.withRandomOffset(offset, rand, 0.05F);

        EffectHelper.of(EffectTemplatesAS.BLOCK_PARTICLE)
                .spawn(offset)
                .setSprite(RenderSpriteUtil.getTexture(input))
                .setSpriteFraction(0.2F)
                .setScale(0.03F + rand.nextFloat() * 0.03F)
                .color(FXColorFunction.constant(ColorUtil.getOverlayColor(input)))
                .alpha(FXAlphaFunction.FADE_OUT)
                .motion(FXMotionFunction.target(() -> Vector3.atCenter(this).addY(0.6F), 0.3F))
                .setMaxAge(40);
    }

    @OnlyIn(Dist.CLIENT)
    private void playLiquidPoolEffect(FluidStack requiredInput) {
        List<BlockPos> offsets = TileInfuser.getLiquidOffsets().stream()
                .map(pos -> pos.offset(this.getBlockPos()))
                .toList();
        MiscUtil.getRandomEntry(offsets, rand).ifPresent(pos -> {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(pos).add(rand.nextFloat(), 1, rand.nextFloat()))
                    .setScale(0.1F + rand.nextFloat() * 0.15F)
                    .setAlpha(1F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .color(FXColorFunction.constant(ColorExtractUtil.getColor(requiredInput).orElse(ColorWrapper.WHITE)))
                    .setMotion(Vector3.y(0.15F))
                    .setGravity(Vector3.y(-0.005F + rand.nextFloat() * -0.008F));
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void clearEffects() {
        if (!this.orbitalLiquid.isNull()) {
            this.orbitalLiquid.get().requestRemoval();
            this.orbitalLiquid.set(null);
        }
    }

    @Nullable
    @Override
    public ObserverRegistryObject getRequiredObserver() {
        return ObserversAS.STRUCTURE_INFUSER;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void onClientDataUpdated(Data previousData) {
        super.onClientDataUpdated(previousData);

        if (this.getTileData().getActiveRecipe().isEmpty()) {
            this.clearEffects();
        }
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> tickFields(inst).and(inst.group(
                CodecUtil.defaulted(InventoryStackList.CODEC, "inventoryContents", InventoryStackList::create, Data::getInventoryContents),
                ActiveInfusionRecipe.CODEC.optionalFieldOf("activeRecipe").forGetter(Data::getActiveRecipe),
                CodecUtil.defaulted(SetCodec.of(ResourceLocation.CODEC), "knownRecipes", HashSet::new, Data::getKnownRecipes)
        )).apply(inst, Data::new));

        protected final InventoryStackList inventoryContents;
        protected ActiveInfusionRecipe activeRecipe;
        protected final Set<ResourceLocation> knownRecipes = new HashSet<>();

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       InventoryStackList inventoryContents,
                       Optional<ActiveInfusionRecipe> activeRecipe,
                       Set<ResourceLocation> knownRecipes) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.inventoryContents = inventoryContents;
            this.activeRecipe = activeRecipe.orElse(null);
            this.knownRecipes.addAll(knownRecipes);
        }

        protected InventoryStackList getInventoryContents() {
            return this.inventoryContents;
        }

        protected InventoryViewFactory newRelayInventory() {
            return InventoryViewFactory.builder(1)
                    .accessibleSides(Direction.DOWN)
                    .stackSizeLimiter((slot, stack) -> 1);
        }

        public InventoryView getInventory() {
            return this.newRelayInventory().createTileView(this, this.getInventoryContents());
        }

        public Optional<ActiveInfusionRecipe> getActiveRecipe() {
            return Optional.ofNullable(this.activeRecipe);
        }

        public void setActiveRecipe(@Nullable ActiveInfusionRecipe activeRecipe) {
            this.activeRecipe = activeRecipe;
        }

        protected Set<ResourceLocation> getKnownRecipes() {
            return Collections.unmodifiableSet(this.knownRecipes);
        }
    }
}
