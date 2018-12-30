/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationBotania;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.lwjgl.opengl.GL11;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemExchangeWand
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:03
 */
public class ItemExchangeWand extends ItemBlockStorage implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final int searchDepth = 5;

    public ItemExchangeWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 0;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        Collection<ItemStack> stored = getMappedStoredStates(lastCacheInstance).values();
        if(stored.isEmpty()) return;

        Map<ItemStack, Integer> amountMap = new LinkedHashMap<>();
        for (ItemStack stack : stored) {
            int found = 0;
            if(Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(Minecraft.getMinecraft().player, lastCacheInstance, ItemUtils.createBlockState(stack));
            } else {
                Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(new InvWrapper(Minecraft.getMinecraft().player.inventory), stack, false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.getCount();
                }
            }
            amountMap.put(stack, found);
        }

        int heightNormal = 26;
        int heightSplit  = 13;
        int width   =  26;
        int offsetX =  30;
        int offsetY =  15;

        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        GlStateManager.color(1F, 1F, 1F, fadeAlpha * 0.9F);
        GL11.glColor4f(1F, 1F, 1F, fadeAlpha * 0.9F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        int tempOffsetY = offsetY;
        for (int i = 0; i < amountMap.size(); i++) {
            boolean first = i == 0;
            boolean last = (i + 1 == amountMap.size());
            if(first) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                vb.pos(offsetX,            tempOffsetY + heightSplit, 10).tex(0, 0.5).endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightSplit, 10).tex(1, 0.5).endVertex();
                vb.pos(offsetX + width,    tempOffsetY,               10).tex(1, 0)  .endVertex();
                vb.pos(offsetX,               tempOffsetY,               10).tex(0, 0)  .endVertex();
                tempOffsetY += heightSplit;
                tes.draw();
            } else {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrameEx.bind();
                vb.pos(offsetX,            tempOffsetY + heightNormal, 10).tex(0, 1).endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightNormal, 10).tex(1, 1).endVertex();
                vb.pos(offsetX + width,    tempOffsetY,                10).tex(1, 0).endVertex();
                vb.pos(offsetX,               tempOffsetY,                10).tex(0, 0).endVertex();
                tempOffsetY += heightNormal;
                tes.draw();
            }
            if(last) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                ClientRenderEventHandler.texHUDItemFrame.bind();
                vb.pos(offsetX,            tempOffsetY + heightSplit, 10).tex(0, 1)  .endVertex();
                vb.pos(offsetX + width, tempOffsetY + heightSplit, 10).tex(1, 1)  .endVertex();
                vb.pos(offsetX + width,    tempOffsetY,               10).tex(1, 0.5).endVertex();
                vb.pos(offsetX,               tempOffsetY,               10).tex(0, 0.5).endVertex();
                tempOffsetY += heightSplit;
                tes.draw();
            }
        }

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        RenderHelper.enableGUIStandardItemLighting();
        RenderItem ri = Minecraft.getMinecraft().getRenderItem();

        tempOffsetY = offsetY;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, entry.getKey(), offsetX + 5, tempOffsetY + 5);
            tempOffsetY += heightNormal;
            GlStateManager.enableAlpha(); //Because Mc item rendering..
        }

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX + 14, offsetY + 16, 0);
        int c = 0x00DDDDDD;
        for (Map.Entry<ItemStack, Integer> entry : amountMap.entrySet()) {
            String amountStr = String.valueOf(entry.getValue());
            if(entry.getValue() == -1) {
                amountStr = "\u221E";
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(-Minecraft.getMinecraft().fontRenderer.getStringWidth(amountStr) / 3, 0, 0);
            GlStateManager.scale(0.7, 0.7, 0.7);
            if(amountStr.length() > 3) {
                GlStateManager.scale(0.9, 0.9, 0.9);
            }
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(amountStr, 0, 0, c);
            GlStateManager.popMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.translate(0, heightNormal, 0);
        }
        TextureHelper.refreshTextureBindState();

        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, EnumHand hand, float pTicks) {
        Map<IBlockState, ItemStack> storedStates = getMappedStoredStates(stack);
        if(storedStates.isEmpty()) return;
        World world = Minecraft.getMinecraft().world;
        Random r = getPreviewRandomFromWorld(world);

        EntityPlayer pl = Minecraft.getMinecraft().player;
        PlayerControllerMP ctrl = Minecraft.getMinecraft().playerController;
        if(ctrl == null || pl == null) return;
        RayTraceResult rtr = getLookBlock(pl, false, true, ctrl.getBlockReachDistance());
        if(rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) return;

        IBlockAccess airWorld = new AirBlockRenderWorld(Biomes.PLAINS, world.getWorldType());
        BlockPos origin = rtr.getBlockPos();
        IBlockState atOrigin = world.getBlockState(origin);
        IBlockState match = MiscUtils.getMatchingState(storedStates.keySet(), atOrigin);
        if(match != null && storedStates.keySet().size() <= 1) {
            storedStates.remove(match);
        }
        if(storedStates.isEmpty()) {
            return;
        }
        float hardness = atOrigin.getBlockHardness(world, origin);
        if(Config.exchangeWandMaxHardness != -1) {
            if(hardness > Config.exchangeWandMaxHardness) {
                return;
            }
        }
        if(hardness == -1) {
            return;
        }

        int total = 0;
        Map<IBlockState, Tuple<ItemStack, Integer>> amountMap = new LinkedHashMap<>();
        for (Map.Entry<IBlockState, ItemStack> entry : storedStates.entrySet()) {
            int found = 0;
            if(Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(Minecraft.getMinecraft().player, stack, ItemUtils.createBlockState(entry.getValue()));
            } else {
                Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(new InvWrapper(Minecraft.getMinecraft().player.inventory), entry.getValue(), false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.getCount();
                }
            }
            total += (found == -1 ? 500_000 : found); //500k should be large enough.
            amountMap.put(entry.getKey(), new Tuple<>(entry.getValue(), found));
        }

        Map<IBlockState, Integer> amtMap = MiscUtils.remap(amountMap, tpl -> tpl.value);
        if(pl.isCreative()) {
            for (IBlockState state : amtMap.keySet()) {
                amtMap.put(state, Integer.MAX_VALUE);
            }
            total = Integer.MAX_VALUE;
        }
        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(world, origin, true, searchDepth, total, false);
        if(found.isEmpty()) return;

        List<IBlockState> applicableStates = Lists.newArrayList(storedStates.keySet());

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blending.ADDITIVEDARK.applyStateManager();
        Blending.ADDITIVEDARK.apply();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        TextureHelper.setActiveTextureToAtlasSprite();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for (BlockPos pos : found.getPattern().keySet()) {
            Collections.shuffle(applicableStates, r);
            IBlockState potentialState = Iterables.getFirst(applicableStates, Blocks.AIR.getDefaultState());
            try {
                potentialState = potentialState.getBlock().getStateForPlacement(world, pos, rtr.sideHit, (float) rtr.hitVec.x, (float) rtr.hitVec.y, (float) rtr.hitVec.z, potentialState.getBlock().getMetaFromState(potentialState), pl, hand);
            } catch (Exception exc) {}
            RenderingUtils.renderBlockSafely(airWorld, pos, potentialState, vb);
        }
        vb.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX, (float) TileEntityRendererDispatcher.staticPlayerY, (float) TileEntityRendererDispatcher.staticPlayerZ);
        tes.draw();
        TextureHelper.refreshTextureBindState();

        Blending.DEFAULT.apply();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos origin, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.isRemote) return EnumActionResult.SUCCESS;
        ItemStack stack = playerIn.getHeldItem(hand);
        if(stack.isEmpty()) return EnumActionResult.SUCCESS;

        if(playerIn.isSneaking()) {
            tryStoreBlock(stack, world, origin);
            return EnumActionResult.SUCCESS;
        }

        Map<IBlockState, ItemStack> storedStates = getMappedStoredStates(stack);
        IBlockState atOrigin = world.getBlockState(origin);
        IBlockState match = MiscUtils.getMatchingState(storedStates.keySet(), atOrigin);
        if(match != null && storedStates.keySet().size() <= 1) {
            storedStates.remove(match);
        }
        if(storedStates.isEmpty()) return EnumActionResult.SUCCESS;

        float hardness = atOrigin.getBlockHardness(world, origin);
        if(Config.exchangeWandMaxHardness != -1) {
            if(hardness > Config.exchangeWandMaxHardness) {
                return EnumActionResult.SUCCESS;
            }
        }
        if(hardness == -1) {
            return EnumActionResult.SUCCESS;
        }

        int total = 0;
        Map<IBlockState, Tuple<ItemStack, Integer>> amountMap = new LinkedHashMap<>();
        for (Map.Entry<IBlockState, ItemStack> entry : storedStates.entrySet()) {
            int found = 0;
            if(Mods.BOTANIA.isPresent()) {
                found = ModIntegrationBotania.getItemCount(playerIn, stack, ItemUtils.createBlockState(entry.getValue()));
            } else {
                Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(new InvWrapper(playerIn.inventory), entry.getValue(), false);
                for (ItemStack foundStack : stacks) {
                    found += foundStack.getCount();
                }
            }
            total += (found == -1 ? 500_000 : found); //500k should be large enough.
            amountMap.put(entry.getKey(), new Tuple<>(entry.getValue(), found));
        }

        Map<IBlockState, Integer> amtMap = MiscUtils.remap(amountMap, tpl -> tpl.value);
        if(playerIn.isCreative()) {
            for (IBlockState state : amtMap.keySet()) {
                amtMap.put(state, Integer.MAX_VALUE);
            }
            total = Integer.MAX_VALUE;
        }
        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(playerIn.getEntityWorld(), origin, true, searchDepth, total, false);
        if(found.isEmpty()) return  EnumActionResult.SUCCESS;


        List<Tuple<IBlockState, ItemStack>> shuffleable = MiscUtils.flatten(storedStates, Tuple::new);
        Random r = getPreviewRandomFromWorld(world);
        for (BlockPos placePos : found.getPattern().keySet()) {
            Collections.shuffle(shuffleable, r);
            Tuple<IBlockState, ItemStack> applicable = playerIn.isCreative() ? Iterables.getFirst(shuffleable, null) : null;
            if(!playerIn.isCreative()) {
                for (Tuple<IBlockState, ItemStack> it : shuffleable) {
                    ItemStack test = ItemUtils.copyStackWithSize(it.value, 1);
                    if(ItemUtils.consumeFromPlayerInventory(playerIn, stack, test, true)) {
                        applicable = it;
                        break;
                    }
                }
            }
            if(applicable == null) break; //No more blocks. LUL

            if(drainTempCharge(playerIn, Config.exchangeWandUseCost, true)) {
                if(((EntityPlayerMP) playerIn).interactionManager.tryHarvestBlock(placePos)) {
                    IBlockState place = applicable.key;
                    try {
                        place = applicable.key.getBlock().getStateForPlacement(world, placePos, facing, hitX, hitY, hitZ, applicable.value.getMetadata(), playerIn, hand);
                    } catch (Exception exc) {}
                    if (MiscUtils.canPlayerPlaceBlockPos(playerIn, hand, place, placePos, EnumFacing.UP)) {
                        if (world.setBlockState(placePos, place)) {
                            drainTempCharge(playerIn, Config.exchangeWandUseCost, false);
                            if(!playerIn.isCreative()) {
                                ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(applicable.value, 1), false);
                            }
                            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ARCHITECT_PLACE, placePos);
                            ev.setAdditionalDataLong(Block.getStateId(atOrigin));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                        }
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

}
