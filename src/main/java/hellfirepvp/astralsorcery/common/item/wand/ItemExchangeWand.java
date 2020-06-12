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
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingOverlayUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.entry.WandsConfig;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHeldRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemOverlayRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.LogicalSide;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemExchangeWand
 * Created by HellFirePvP
 * Date: 28.02.2020 / 21:04
 */
public class ItemExchangeWand extends Item implements ItemBlockStorage, ItemOverlayRender, ItemHeldRender, AlignmentChargeConsumer {

    private static final float COST_PER_EXCHANGE = 5F;

    public ItemExchangeWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getSizeMode(stack).getDisplay().setStyle(new Style().setColor(TextFormatting.GOLD)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return 3;
    }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return Sets.newHashSet(ToolType.PICKAXE, ToolType.AXE, ToolType.SHOVEL);
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return true;
    }

    @Override
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        BlockRayTraceResult hitResult = MiscUtils.rayTraceLookBlock(player, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE);
        if (hitResult == null) {
            return 0F;
        }
        return getPlaceStates(player, player.getEntityWorld(), hitResult.getPos(), stack).size() * COST_PER_EXCHANGE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean renderInHand(ItemStack stack, MatrixStack renderStack, float pTicks) {
        BlockRayTraceResult hitResult = MiscUtils.rayTraceLookBlock(Minecraft.getInstance().player, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE);
        if (hitResult == null) {
            return true;
        }
        World world = Minecraft.getInstance().world;
        BlockPos at = hitResult.getPos();
        Map<BlockPos, BlockState> placeStates = getPlaceStates(Minecraft.getInstance().player, world, at, stack);
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

        // availableStacks should already contain enough to fill whatever placeStates has precalculated
        Map<BlockPos, BlockState> placeStates = getPlaceStates(player, world, pos, stack);
        Map<BlockState, Tuple<ItemStack, Integer>> availableStacks = MapStream.of(ItemBlockStorage.getInventoryMatching(player, stack))
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
                if (ItemUtils.consumeFromPlayerInventory(player, stack, extractable, true)) {
                    canExtract = true;
                }
            }
            if (!canExtract) {
                continue;
            }

            BlockState prevState = world.getBlockState(placePos);
            if (((ServerPlayerEntity) player).interactionManager.tryHarvestBlock(placePos)) {
                if (MiscUtils.canPlayerPlaceBlockPos(player, stateToPlace, placePos, Direction.UP)) {
                    if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_EXCHANGE, false) &&
                            world.setBlockState(placePos, stateToPlace)) {
                        if (!player.isCreative()) {
                            ItemUtils.consumeFromPlayerInventory(player, stack, extractable, false);
                        }

                        PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                                .addData(buf -> {
                                    ByteBufUtils.writePos(buf, placePos);
                                    ByteBufUtils.writeBlockState(buf, prevState);
                                });
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 32));
                    }
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            SizeMode nextMode = getSizeMode(held).next();
            setSizeMode(held, nextMode);
            playerIn.sendStatusMessage(nextMode.getDisplay(), true);
        }
        return ActionResult.resultSuccess(held);
    }

    @Nonnull
    private Map<BlockPos, BlockState> getPlaceStates(PlayerEntity placer, World world, BlockPos origin, ItemStack refStack) {
        Map<BlockState, Tuple<ItemStack, Integer>> tplStates = ItemBlockStorage.getInventoryMatching(placer, refStack);
        BlockState atState = world.getBlockState(origin);
        SizeMode mode = getSizeMode(refStack);
        Map<BlockPos, BlockState> placeables = Maps.newHashMap();

        BlockState match = BlockUtils.getMatchingState(tplStates.keySet(), atState);
        if (match != null && tplStates.size() <= 1) {
            return placeables; //If trying to replace a block with its identical block.
        }
        float hardness = atState.getBlockHardness(world, origin);
        int cfgHardness = WandsConfig.CONFIG.exchangeWandMaxHardness.get();
        if (hardness == -1 || (cfgHardness != -1 && hardness > cfgHardness)) {
            return placeables; //Don't break/exchange too hard or unbreakable blocks.
        }

        int totalItems = 0;
        if (placer.isCreative()) {
            totalItems = Integer.MAX_VALUE;
        } else {
            for (Tuple<ItemStack, Integer> amountTpl : tplStates.values()) {
                totalItems += (amountTpl.getB() == -1 ? 500_000 : amountTpl.getB());
            }
        }

        List<BlockPos> foundPositions = BlockDiscoverer.discoverBlocksWithSameStateAround(world, origin, true, mode.getSearchRadius(), totalItems, false);
        if (foundPositions.isEmpty()) {
            return placeables; //It.. shouldn't actually be empty here, ever. Should at least have 1 entry.
        }

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
        return placeables;
    }

    public static void setSizeMode(@Nonnull ItemStack stack, @Nonnull SizeMode mode) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemExchangeWand)) {
            return;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        nbt.putInt("sizeMode", mode.ordinal());
    }

    @Nonnull
    public static SizeMode getSizeMode(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemExchangeWand)) {
            return SizeMode.RANGE_2;
        }
        CompoundNBT nbt = NBTHelper.getPersistentData(stack);
        return MiscUtils.getEnumEntry(SizeMode.class, nbt.getInt("sizeMode"));
    }

    public static enum SizeMode {

        RANGE_2(2),
        RANGE_3(3),
        RANGE_4(4),
        RANGE_5(5);

        private final int searchRadius;

        SizeMode(int searchRadius) {
            this.searchRadius = searchRadius;
        }

        public int getSearchRadius() {
            return searchRadius;
        }

        public ITextComponent getName() {
            return new TranslationTextComponent("astralsorcery.misc.exchange.size." + this.searchRadius);
        }

        public ITextComponent getDisplay() {
            return new TranslationTextComponent("astralsorcery.misc.exchange.size", this.getName());
        }

        @Nonnull
        private SizeMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtils.getEnumEntry(SizeMode.class, next);
        }
    }
}
