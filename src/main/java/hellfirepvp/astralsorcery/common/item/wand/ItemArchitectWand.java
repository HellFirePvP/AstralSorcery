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
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingOverlayUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import hellfirepvp.astralsorcery.common.CommonProxy;
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
import hellfirepvp.observerlib.client.util.BufferBuilderDecorator;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
public class ItemArchitectWand extends Item implements ItemBlockStorage, ItemOverlayRender, ItemHeldRender {

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
    @OnlyIn(Dist.CLIENT)
    public boolean renderInHand(ItemStack stack, float pTicks) {
        PlaceMode mode = getPlaceMode(stack);

        RayTraceResult result = Minecraft.getInstance().player.pick(60F, pTicks, false);
        BlockPos at = null;
        Direction placingAgainst = null;
        if (mode.needsOffset()) {
            if (result.getType() != RayTraceResult.Type.BLOCK || !(result instanceof BlockRayTraceResult)) {
                return true;
            } else {
                at = ((BlockRayTraceResult) result).getPos().offset(((BlockRayTraceResult) result).getFace());
                placingAgainst = ((BlockRayTraceResult) result).getFace();
            }
        }

        World world = Minecraft.getInstance().world;
        Map<BlockPos, BlockState> placeRender = getPlaceStates(Minecraft.getInstance().player, world, at, placingAgainst, stack);
        if (placeRender.isEmpty()) {
            return true;
        }

        TextureHelper.bindBlockAtlas();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilderDecorator buf = BufferBuilderDecorator.decorate(tes.getBuffer());

        float[] fullBright = new float[] { 15F, 15F };
        buf.setLightmapDecorator((skyLight, blockLight) -> fullBright);

        GlStateManager.enableBlend();
        Blending.ADDITIVEDARK.applyStateManager();
        GlStateManager.disableDepthTest();
        GlStateManager.disableAlphaTest();

        GlStateManager.pushMatrix();
        RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        placeRender.forEach((pos, state) -> RenderingUtils.renderSimpleBlockModel(state, buf, pos, null, false));
        tes.draw();
        GlStateManager.popMatrix();

        GlStateManager.enableAlphaTest();
        GlStateManager.enableDepthTest();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableBlend();
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
            return ActionResult.newResult(ActionResultType.SUCCESS, held);
        }
        if (world.isRemote()) {
            return ActionResult.newResult(ActionResultType.SUCCESS, held);
        }

        BlockPos at = null;
        Direction placingAgainst = null;
        RayTraceResult result = player.pick(60F, 1F, false);
        if (result.getType() == RayTraceResult.Type.BLOCK && result instanceof BlockRayTraceResult) {
            at = ((BlockRayTraceResult) result).getPos().offset(((BlockRayTraceResult) result).getFace());
            placingAgainst = ((BlockRayTraceResult) result).getFace();
        } else if (mode.needsOffset()) {
            return ActionResult.newResult(ActionResultType.FAIL, held);
        }

        Map<BlockPos, BlockState> placeStates = getPlaceStates(player, world, at, placingAgainst, held);
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
                if (world.setBlockState(placePos, stateToPlace)) {
                    if (!player.isCreative()) {
                        ItemUtils.consumeFromPlayerInventory(player, held, extractable, false);
                    }

                    PktPlayEffect ev = new PktPlayEffect(PktPlayEffect.Type.BLOCK_EFFECT)
                            .addData(buf -> {
                                ByteBufUtils.writeVector(buf, new Vector3(placePos));
                                ByteBufUtils.writeBlockState(buf, stateToPlace);
                            });
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 32));
                }
            }
        }

        return ActionResult.newResult(ActionResultType.SUCCESS, held);
    }

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

        TOWARDS_PLAYER("towards", true) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                List<BlockPos> blocks = new ArrayList<>();
                double cmpFrom, cmpTo;
                switch (placedAgainst.getAxis()) {
                    case X:
                        cmpFrom = center.getX();
                        cmpTo = player.posX;
                        break;
                    case Y:
                        cmpFrom = center.getY();
                        cmpTo = player.posY;
                        break;
                    case Z:
                        cmpFrom = center.getZ();
                        cmpTo = player.posZ;
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
                    MiscUtils.executeWithChunk(world, pos, () -> {
                        if (BlockUtils.isReplaceable(world, pos)) {
                            line.add(pos);
                        }
                    });
                    return true;
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
        SPHERE("sphere", true) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getSphere(5), at -> at.add(center));
            }
        },
        SPHERE_HOLLOW("sphere_hollow", true) {
            @Override
            public List<BlockPos> generatePlacementPositions(World world, PlayerEntity player, Direction placedAgainst, BlockPos center) {
                return MiscUtils.transformList(BlockGeometry.getHollowSphere(5, 4), at -> at.add(center));
            }
        };

        private final String name;
        private final boolean needsOffset;

        PlaceMode(String name, boolean needsOffset) {
            this.name = name;
            this.needsOffset = needsOffset;
        }

        public ITextComponent getName() {
            return new TranslationTextComponent("astralsorcery.misc.architect.mode." + this.name);
        }

        public ITextComponent getDisplay() {
            return new TranslationTextComponent("astralsorcery.misc.architect.mode", this.getName());
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
