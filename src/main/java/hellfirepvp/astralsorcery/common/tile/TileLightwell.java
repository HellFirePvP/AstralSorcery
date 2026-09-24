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
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.ForwardingStarlightReceiverNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.ForwardingStarlightReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.RecipeFinder;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryView;
import hellfirepvp.astralsorcery.common.util.inventory.FilteredInventoryViewFactory;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryStackList;
import hellfirepvp.astralsorcery.common.util.inventory.InventoryView;
import hellfirepvp.astralsorcery.common.util.tank.FluidContainerList;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankViewFactory;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLightwell
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLightwell extends TileEntityNetwork<ForwardingStarlightReceiverNode, TileLightwell.Data> implements ForwardingStarlightReceiverNode.ReceiverTile {

    private LightwellRecipe activeRecipe = null;

    public TileLightwell(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.LIGHTWELL, pos, blockState);
    }

    protected TileLightwell(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

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
        ItemStack catalyst = this.getTileData().getInventory().getStackInSlot(0);
        if (catalyst.isEmpty()) {
            this.activeRecipe = null;
            return;
        }
        if (!level.isEmptyBlock(this.getBlockPos().above())) {
            this.breakCatalyst();
            return;
        }

        if (this.activeRecipe == null) {
            this.activeRecipe = this.getTileData().findMatchingRecipe(catalyst)
                    .map(RecipeHolder::value)
                    .orElse(null);
        }
        if (this.activeRecipe == null) {
            this.breakCatalyst();
            return;
        }

        int multiplier = Math.max(1, catalyst.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()).getTotalTierCount());
        double gainAttempts = Math.max(1, Math.sqrt(multiplier));
        gainAttempts *= this.activeRecipe.getProductionMultiplier();

        int gain = Mth.floor(gainAttempts);
        gainAttempts -= gain;
        if (rand.nextFloat() < gainAttempts) {
            gain++;
        }
        if (gain > 0) {
            FluidStack gainedStack = new FluidStack(this.activeRecipe.getGeneratedFluid(), gain);
            this.getTileData().getFluidTank().withoutFilters(tank -> {
                tank.fill(gainedStack, IFluidHandler.FluidAction.EXECUTE);
            });
        }

        if (this.activeRecipe.getShatterMultiplier() > 0) {
            if (this.rand.nextInt(1 + (int) (1000 * (multiplier * (1F / this.activeRecipe.getShatterMultiplier())))) == 0) {
                this.breakCatalyst();
            }
        }
    }

    public void breakCatalyst() {
        InventoryView inv = this.getTileData().getInventory();
        if (!inv.getStackInSlot(0).isEmpty()) {
            inv.setStackInSlot(0, ItemStack.EMPTY);
            ServerSoundHelper.playSoundAround(SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, this.getLevel(), this.getBlockPos(), 1F, 1F);
        }
        this.activeRecipe = null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        ItemStack catalyst = this.getTileData().getInventory().getStackInSlot(0);
        if (!catalyst.isEmpty()) {
            this.getTileData().findMatchingRecipe(catalyst).ifPresent(recipe -> {
                this.playCatalystEffect(recipe.value().getCatalystColor());
            });
        }

        FluidStack contained = this.getTileData().getContainedFluid();
        if (!contained.isEmpty()) {
            this.playFluidEffect(contained, ColorExtractUtil.getColor(contained).orElse(ColorWrapper.WHITE));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playCatalystEffect(ColorWrapper color) {
        if (this.rand.nextInt(6) != 0) return;

        Vector3 at = VectorUtil.withRandomOffset(Vector3.atBottomCenter(this).addY(1.4F), this.rand, 0.2F);
        int age = 30 + this.rand.nextInt(20);
        float scale = 0.15F + rand.nextFloat() * 0.05F;

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .alpha(FXAlphaFunction.FADE_OUT)
                .color(FXColorFunction.constant(color))
                .setScale(scale)
                .setGravity(Vector3.y(-0.0004F))
                .setMaxAge(age);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setScale(scale * 0.3F)
                .setGravity(Vector3.y(-0.0004F))
                .setMaxAge(Mth.ceil(age * 0.75F));
    }

    @OnlyIn(Dist.CLIENT)
    private void playFluidEffect(FluidStack contained, ColorWrapper color) {
        if (this.rand.nextInt(3) != 0) return;

        float fillPercent = (float) contained.getAmount() / this.getTileData().getFluidTank().getTankCapacity(0);
        float yOffset = 0.32F + fillPercent * 0.6F;

        Vector3 at = new Vector3(this).add(0.1F + this.rand.nextFloat() * 0.8F, yOffset, 0.1F + this.rand.nextFloat() * 0.8F);
        float scale = 0.15F + this.rand.nextFloat() * 0.05F;
        int age = 30 + this.rand.nextInt(10);

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .color(FXColorFunction.constant(color))
                .setScale(scale)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setGravity(Vector3.y(0.0002F))
                .setMaxAge(age);
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .setScale(scale * 0.3F)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setGravity(Vector3.y(0.0002F))
                .setMaxAge(Mth.ceil(age * 0.75F));
    }

    @Override
    public void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet) {}

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, ForwardingStarlightReceiverNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.FORWARDING_RECEIVER_NODE;
    }

    public static class Data extends TileEntityNetwork.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> lightwellFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P6<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, FluidContainerList, InventoryStackList> lightwellFields(RecordCodecBuilder.Instance<T> instance) {
            return TileEntityNetwork.Data.netFields(instance).and(instance.group(
                    CodecUtil.defaulted(FluidContainerList.CODEC, "fluidContents", FluidContainerList::create, Data::getFluidContents),
                    CodecUtil.defaulted(InventoryStackList.CODEC, "inventoryContents", InventoryStackList::create, Data::getInventoryContents)
            ));
        }

        protected final FluidContainerList fluidContents;
        protected final InventoryStackList inventoryContents;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       boolean needsNetworkSync,
                       FluidContainerList fluidContents,
                       InventoryStackList inventoryContents) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.fluidContents = fluidContents;
            this.inventoryContents = inventoryContents;
        }

        public FluidContainerList getFluidContents() {
            return this.fluidContents;
        }

        public InventoryStackList getInventoryContents() {
            return this.inventoryContents;
        }

        public FluidStack getContainedFluid() {
            return this.fluidContents.getTank(0).getContent();
        }

        protected Optional<RecipeHolder<LightwellRecipe>> findMatchingRecipe(ItemStack stack) {
            return RecipeFinder.of()
                    .flatMap(finder -> finder.findLightwellRecipe(stack, this.getContainedFluid()));
        }

        protected FluidTankViewFactory newFluidTank() {
            return FluidTankViewFactory.builder(1)
                    .tankCapacity(tank -> 2000)
                    .inputFilter((tank, stack, existing) -> false)
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
                        return this.findMatchingRecipe(toAdd).isPresent();
                    })
                    .accessibleSides(Direction.DOWN);
        }

        public FilteredInventoryView getInventory() {
            return this.newInventoryHandler().createTileView(this, this.inventoryContents);
        }
    }
}
