/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingOverlayUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHeldRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemOverlayRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockGeometry;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.client.util.BufferDecoratorBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemArchitectWand
 * Created by HellFirePvP
 * Date: 28.02.2020 / 21:28
 */
public class ItemArchitectWand extends Item implements ItemBlockStorage, ItemOverlayRender, ItemHeldRender, AlignmentChargeConsumer {

    private static final float COST_PER_PLACEMENT = 8F;

    public ItemArchitectWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getPlaceMode(stack).getDisplay().setStyle(new Style().setColor(TextFormatting.GOLD)));
    }

    @Override
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        PlaceMode mode = getPlaceMode(stack);
        return getPlayerPlaceableStates(player, stack).size() * COST_PER_PLACEMENT * mode.getPlaceCostMulitplier();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderInHand(ItemStack stack, MatrixStack renderStack, float pTicks) {
        PlayerEntity player = Minecraft.getInstance().player;
        Map<BlockPos, BlockState> placeStates = getPlayerPlaceableStates(player, stack);
        if (placeStates.isEmpty()) {
            return true;
        }

        BlockAtlasTexture.getInstance().bindTexture();

        int[] fullBright = new int[] { 15, 15 };
        BufferDecoratorBuilder decorator = BufferDecoratorBuilder.withLightmap((skyLight, blockLight) -> fullBright);
        Vector3 offset = RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks);

        RenderSystem.enableBlend();
        Blending.ADDITIVEDARK.apply();
        RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.BLOCK, buf -> {
            placeStates.forEach((pos, state) -> {
                renderStack.push();
                renderStack.translate(pos.getX() - offset.getX() + 0.1F, pos.getY() - offset.getY() + 0.1F, pos.getZ() - offset.getZ() + 0.1F);
                renderStack.scale(0.8F, 0.8F, 0.8F);
                RenderingUtils.renderSimpleBlockModel(state, renderStack, decorator.decorate(buf), pos, null, false);
                renderStack.pop();
            });
        });

        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderOverlay(ItemStack stack, float pTicks) {
        List<Tuple<ItemStack, Integer>> foundStacks = ItemBlockStorage.getInventoryMatchingItemStacks(Minecraft.getInstance().player, stack);
        RenderingOverlayUtils.renderDefaultItemDisplay(foundStacks);
        return true;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        if (world.isRemote() || !(player instanceof ServerPlayerEntity) || stack.isEmpty()) {
            return ActionResultType.SUCCESS;
        }
        if (player.isSneaking()) {
            ItemBlockStorage.storeBlockState(stack, world, pos);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        PlaceMode mode = getPlaceMode(held);
        if (player.isSneaking()) {
            PlaceMode nextMode = mode.next();
            setPlaceMode(held, nextMode);
            player.sendStatusMessage(nextMode.getDisplay(), true);
            return ActionResult.resultSuccess(held);
        }
        if (world.isRemote()) {
            return ActionResult.resultSuccess(held);
        }

        Map<BlockPos, BlockState> placeStates = getPlayerPlaceableStates(player, held);
        if (placeStates.isEmpty()) {
            return ActionResult.resultFail(held);
        }

        Map<BlockState, Tuple<ItemStack, Integer>> availableStacks = MapStream.of(ItemBlockStorage.getInventoryMatching(player, held))
                .filter(tpl -> placeStates.containsValue(tpl.getA()))
                .collect(Collectors.toMap(Tuple::getA, Tuple::getB));


        for (BlockPos placePos : placeStates.keySet()) {
            BlockState stateToPlace = placeStates.get(placePos);
            Tuple<ItemStack, Integer> availableStack = availableStacks.get(stateToPlace);
            if (availableStack == null) {
                continue;
            }

            ItemStack extractable = ItemUtils.copyStackWithSize(availableStack.getA(), 1);
            boolean canExtract = player.isCreative();
            if (!canExtract) {
                if (ItemUtils.consumeFromPlayerInventory(player, held, extractable, true)) {
                    canExtract = true;
                }
            }
            if (!canExtract) {
                continue;
            }

            if (MiscUtils.canPlayerPlaceBlockPos(player, stateToPlace, placePos, Direction.UP)) {
                if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_PLACEMENT, false) &&
                        world.setBlockState(placePos, stateToPlace)) {
                    if (!player.isCreative()) {
                        ItemUtils.consumeFromPlayerInventory(player, held, extractable, false);
                    }

                    PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                            .addData(buf -> {
                                ByteBufUtils.writePos(buf, placePos);
                                ByteBufUtils.writeBlockState(buf, stateToPlace);
                            });
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 32));
                }
            }
        }

        return ActionResult.resultSuccess(held);
    }

    @Nonnull
    private Map<BlockPos, BlockState> getPlayerPlaceableStates(PlayerEntity player, ItemStack stack) {
        PlaceMode mode = getPlaceMode(stack);
        World world = player.getEntityWorld();

        BlockRayTraceResult rtr = MiscUtils.rayTraceLookBlock(player, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, 60F);
        if (rtr == null && mode.needsOffset()) {
            return new HashMap<>();
        }

        Map<BlockPos, BlockState> placeStates;
        if (rtr != null) {
            Direction placingAgainst = rtr.getFace();
            BlockPos at = rtr.getPos().offset(rtr.getFace());
            placeStates = getPlaceStates(player, world, at, placingAgainst, stack);
        } else {
            placeStates = getPlaceStates(player, world, null, null, stack);
        }
        return placeStates;
    }

    @Nonnull
    private Map<BlockPos, BlockState> getPlaceStates(PlayerEntity placer, World world, @Nullable BlockPos origin, @Nullable Direction placingAgainst, ItemStack refStack) {
        Map<BlockState, Tuple<ItemStack, Integer>> tplStates = ItemBlockStorage.getInventoryMatching(placer, refStack);
        PlaceMode placeMode = getPlaceMode(refStack);
        Map<BlockPos, BlockState> placeables = Maps.newHashMap();

        int totalItems = 0;
        if (placer.isCreative()) {
            totalItems = Integer.MAX_VALUE;
        } else {
            for (Tuple<ItemStack, Integer> amountTpl : tplStates.values()) {
                totalItems += (amountTpl.getB() == -1 ? 500_000 : amountTpl.getB());
            }
        }

        List<BlockPos> foundPositions = placeMode.generatePlacementPositions(world, placer, placingAgainst, origin);
        if (foundPositions.isEmpty()) {
            return placeables; //It.. shouldn't actually be empty here, ever. Should at least have 1 entry.
        }
        foundPositions = foundPositions.subList(0, Math.min(foundPositions.size(), totalItems));

        Map<BlockState, Integer> placeAmounts = Maps.newHashMap();
        for (BlockState state : tplStates.keySet()) {
            placeAmounts.put(state, placer.isCreative() ? Integer.MAX_VALUE : tplStates.get(state).getB());
        }
        List<BlockState> placeableStates = Lists.newArrayList(placeAmounts.keySet());
        Random rand = ItemBlockStorage.getPreviewRandomFromWorld(world);

        for (BlockPos pos : foundPositions) {
            Collections.shuffle(placeableStates, rand);
            BlockState toPlace = Iterables.getFirst(placeableStates, null);

            if (toPlace == null) {
                continue;
            }

            MiscUtils.executeWithChunk(world, pos, () -> {
                if (BlockUtils.isReplaceable(world, pos)) {

                    if (!placer.isCreative()) {
                        int count = placeAmounts.get(toPlace);
                        count--;
                        if (count <= 0) {
                            placeAmounts.remove(toPlace);
                            placeableStates.remove(toPlace);
                        } else {
                            placeAmounts.put(toPlace, count);
                        }
                    }

                    placeables.put(pos, toPlace);
                }
            });
        }
        return placeables;
    }

    public static void setPlaceMode(@Nonnull ItemStack stack, @Nonnull PlaceMode mode) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemArchitectWand)) {
            return;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        nbt.putInt("placeMode", mode.ordinal());
    }

    @Nonnull
    public static PlaceMode getPlaceMode(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemArchitectWand)) {
            return PlaceMode.TOWARDS_PLAYER;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        return MiscUtils.getEnumEntry(PlaceMode.class, nbt.getInt("placeMode"));
    }

    public static enum PlaceMode {

        TOWARDS_PLAYER("towards", true, 3F) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                List<BlockPos> blocks = new ArrayList<>();
                double cmpFrom, cmpTo;
                switch (placedAgainst.getAxis()) {
                    case X:
                        cmpFrom = center.getX();
                        cmpTo = player.getPosX();
                        break;
                    case Y:
                        cmpFrom = center.getY();
                        cmpTo = player.getPosY();
                        break;
                    case Z:
                        cmpFrom = center.getZ();
                        cmpTo = player.getPosZ();
                        break;
                    default:
                        return Lists.newLinkedList();
                }
                int length = (int) Math.min(20, Math.abs(cmpFrom + 0.5 - cmpTo));
                for (int i = 0; i < length; i++) {
                    BlockPos at = center.offset(placedAgainst, i);
                    if (MiscUtils.executeWithChunk(world, at, () -> !BlockUtils.isReplaceable(world, at), true)) {
                        break;
                    }
                    blocks.add(at);
                }
                return blocks;
            }
        },
        FROM_PLAYER("line", false) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                BlockPos origin = player.getPosition().down();
                RayTraceResult result = player.pick(60F, 1F, false);
                BlockPos hit;
                if (result instanceof BlockRayTraceResult) {
                    hit = ((BlockRayTraceResult) result).getPos();
                } else {
                    hit = new BlockPos(result.getHitVec());
                }
                List<BlockPos> line = new ArrayList<>();
                RaytraceAssist rta = new RaytraceAssist(origin, hit);
                rta.forEachBlockPos(pos -> {
                    return MiscUtils.executeWithChunk(world, pos, () -> {
                        if (BlockUtils.isReplaceable(world, pos)) {
                            line.add(pos);
                            return true;
                        }
                        return false;
                    }, false);
                });
                return line;
            }
        },
        H_PLANE("plane", true) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getPlane(Direction.UP, 5), at -> at.add(center));
            }
        },
        V_PLANE("wall", true) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getPlane(player.getHorizontalFacing(), 5), at -> at.add(center));
            }
        },
        SPHERE("sphere", true, 0.2F) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getSphere(5), at -> at.add(center));
            }
        },
        SPHERE_HOLLOW("sphere_hollow", true, 0.5F) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getHollowSphere(5, 4), at -> at.add(center));
            }
        };

        private final String name;
        private final boolean needsOffset;
        private final float placeCostMulitplier;

        PlaceMode(String name, boolean needsOffset) {
            this(name, needsOffset, 1F);
        }

        PlaceMode(String name, boolean needsOffset, float placeCostMultiplier) {
            this.name = name;
            this.needsOffset = needsOffset;
            this.placeCostMulitplier = placeCostMultiplier;
        }

        public ITextComponent getName() {
            return new TranslationTextComponent("astralsorcery.misc.architect.mode." + this.name);
        }

        public ITextComponent getDisplay() {
            return new TranslationTextComponent("astralsorcery.misc.architect.mode", this.getName());
        }

        public float getPlaceCostMulitplier() {
            return placeCostMulitplier;
        }

        public boolean needsOffset() {
            return needsOffset;
        }

        public abstract List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center);

        @Nonnull
        private PlaceMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtils.getEnumEntry(PlaceMode.class, next);
        }

    }
}
