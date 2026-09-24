/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.fluid.LiquidStarlightBlock;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabBucketItem;
import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.util.TriPredicate;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidsAS {

    public static final DeferredRegister<Fluid> FLUID_REGISTER =
            DeferredRegister.create(BuiltInRegistries.FLUID, AstralSorcery.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPE_REGISTER =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, AstralSorcery.MODID);

    public static FluidLiquidStarlight LIQUID_STARLIGHT = new FluidLiquidStarlight("liquid_starlight");

    public static class FluidLiquidStarlight {

        private final ResourceLocation stillTexture;
        private final ResourceLocation flowingTexture;
        private final DeferredHolder<FluidType, FluidType> fluidType;
        private DeferredHolder<Fluid, BaseFlowingFluid> source;
        private DeferredHolder<Fluid, BaseFlowingFluid> flowing;
        private DeferredBlock<LiquidBlock> fluidBlock;
        private DeferredItem<BucketItem> bucket;

        public FluidLiquidStarlight(String fluidName) {
            this.stillTexture = AstralSorcery.key("block/" + fluidName + "_still");
            this.flowingTexture = AstralSorcery.key("block/" + fluidName + "_flowing");
            this.fluidType = FLUID_TYPE_REGISTER.register(fluidName, () -> new FluidType(createTypeProperties()) {
                @SuppressWarnings("removal")
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {

                        @Override
                        public ResourceLocation getStillTexture() {
                            return FluidLiquidStarlight.this.stillTexture;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FluidLiquidStarlight.this.flowingTexture;
                        }

                        @Override
                        public int getTintColor() {
                            return 0xCCFFFFFF;
                        }
                    });
                }

                @Override
                public void setItemMovement(ItemEntity entity) {
                    double gravity = entity.getGravity();
                    if (gravity != 0) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -gravity, 0.0));
                    }
                }
            });

            source = FLUID_REGISTER.register(fluidName, () -> new LiquidStarlightFluid.Source(createProperties(fluidType, source, flowing, bucket, fluidBlock)));
            flowing = FLUID_REGISTER.register(fluidName + "_flowing", () -> new LiquidStarlightFluid.Flowing(createProperties(fluidType, source, flowing, bucket, fluidBlock)));
            fluidBlock = BlocksAS.BLOCK_REGISTER.register(fluidName, () -> new LiquidStarlightBlock(source.get()));
            bucket = ItemsAS.ITEM_REGISTER.register(fluidName + "_bucket", () -> new CreativeTabBucketItem(source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        }

        public DeferredBlock<LiquidBlock> getFluidBlock() {
            return this.fluidBlock;
        }

        public DeferredItem<BucketItem> getBucket() {
            return this.bucket;
        }

        public FluidType getFluidType() {
            return this.fluidType.get();
        }

        public DeferredHolder<Fluid, BaseFlowingFluid> getSource() {
            return this.source;
        }

        public DeferredHolder<Fluid, BaseFlowingFluid> getFlowing() {
            return this.flowing;
        }

        public boolean isSource(FluidState state) {
            return state.is(this.getSource().get());
        }

        public boolean isFlowing(FluidState state) {
            return state.is(this.getFlowing().get());
        }

        public FluidStack stack(int amount) {
            return new FluidStack(this.getSource(), amount);
        }

        private static BaseFlowingFluid.Properties createProperties(Supplier<FluidType> type,
                                                                    Supplier<? extends Fluid> still,
                                                                    Supplier<? extends Fluid> flowing,
                                                                    DeferredItem<? extends Item> bucket,
                                                                    Supplier<? extends LiquidBlock> block) {
            return new BaseFlowingFluid.Properties(type, still, flowing)
                    .tickRate(1)
                    .bucket(bucket)
                    .block(block);
        }

        public static FluidType.Properties createTypeProperties() {
            return FluidType.Properties.create()
                    .rarity(Rarity.EPIC)
                    .lightLevel(15)
                    .density(1001)
                    .viscosity(300)
                    .temperature(10)
                    .canSwim(false)
                    .canPushEntity(false)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
        }
    }

    public static void addLiquidInteractions() {
        addInteractionNoSound(FluidsAS.LIQUID_STARLIGHT.getFluidType(),
                (level, pos, otherType) -> otherType.getTemperature(level.getFluidState(pos), level, pos) <= 600,
                (level, pos, state) -> Blocks.PACKED_ICE.defaultBlockState());
        addInteractionNoSound(FluidsAS.LIQUID_STARLIGHT.getFluidType(),
                (level, pos, otherType) -> otherType.getTemperature(level.getFluidState(pos), level, pos) > 600,
                (level, pos, state) -> {
                    if (level.getRandom().nextInt(800) == 0) {
                        return BlocksAS.AQUAMARINE_SHALE.get().defaultBlockState();
                    }
                    return Blocks.SAND.defaultBlockState();
                });
    }

    public static void addInteractionNoSound(FluidType type, TriPredicate<Level, BlockPos, FluidType> otherTypeTest, TriFunction<Level, BlockPos, FluidState, BlockState> getInteractedState) {
        FluidInteractionRegistry.addInteraction(type, new FluidInteractionRegistry.InteractionInformation(
                (level, currentPos, relativePos, currentState) -> {
                    FluidType otherType = level.getFluidState(relativePos).getFluidType();
                    return !otherType.isAir() && otherType != type && otherTypeTest.test(level, relativePos, otherType);
                }, (level, currentPos, relativePos, currentState) -> {
                    level.setBlockAndUpdate(currentPos, EventHooks.fireFluidPlaceBlockEvent(level, currentPos, currentPos,
                            getInteractedState.apply(level, currentPos, currentState)));
                }));
    }

    public abstract static class LiquidStarlightFluid extends BaseFlowingFluid {

        protected LiquidStarlightFluid(Properties properties) {
            super(properties);
        }

        public static class Flowing extends LiquidStarlightFluid {

            public Flowing(Properties properties) {
                super(properties);
                registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
            }

            protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
                super.createFluidStateDefinition(builder);
                builder.add(LEVEL);
            }

            public int getAmount(FluidState state) {
                return state.getValue(LEVEL);
            }

            public boolean isSource(FluidState state) {
                return false;
            }
        }

        public static class Source extends LiquidStarlightFluid {

            public Source(Properties properties) {
                super(properties);
            }

            public int getAmount(FluidState state) {
                return 8;
            }

            public boolean isSource(FluidState state) {
                return true;
            }
        }
    }
}
