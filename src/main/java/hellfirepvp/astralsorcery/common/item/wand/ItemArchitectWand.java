/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationBotania;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemArchitectWand
 * Created by HellFirePvP
 * Date: 06.02.2017 / 22:49
 */
public class ItemArchitectWand extends ItemBlockStorage implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final double architectRange = 60.0D;

    public ItemArchitectWand() {
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
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        ItemStack blockStackStored = getStoredStateAsStack(lastCacheInstance);
        if(blockStackStored.isEmpty()) return;

        int amtFound = 0;
        if(Mods.BOTANIA.isPresent()) {
            amtFound = ModIntegrationBotania.getItemCount(Minecraft.getMinecraft().player, lastCacheInstance, ItemUtils.createBlockState(blockStackStored));
        } else {
            Collection<ItemStack> stacks = ItemUtils.scanInventoryForMatching(new InvWrapper(Minecraft.getMinecraft().player.inventory), blockStackStored, false);
            for (ItemStack stack : stacks) {
                amtFound += stack.getCount();
            }
        }

        int height  =  26;
        int width   =  26;
        int offsetX =  30;
        int offsetY =  15;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        ClientRenderEventHandler.texHUDItemFrame.bind();

        GL11.glColor4f(1F, 1F, 1F, fadeAlpha * 0.9F);
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, 10).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, 10).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          10).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          10).tex(0, 0).endVertex();
        tes.draw();

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        RenderHelper.enableGUIStandardItemLighting();
        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, blockStackStored, offsetX + 5, offsetY + 5);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha(); //Because Mc item rendering..

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 14, offsetY + 16, 0);
        String amtString = String.valueOf(amtFound);
        if(amtFound == -1) {
            amtString = "∞";
        }
        GL11.glTranslated(-Minecraft.getMinecraft().fontRenderer.getStringWidth(amtString) / 3, 0, 0);
        GL11.glScaled(0.7, 0.7, 0.7);
        if(amtString.length() > 3) {
            GL11.glScaled(0.9, 0.9, 0.9);
        }
        int c = 0x00DDDDDD;
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(amtString, 0, 0, c);
        GlStateManager.color(1F, 1F, 1F, 1F);
        TextureHelper.refreshTextureBindState();

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderWhileInHand(ItemStack stack, EnumHand hand, float pTicks) {
        IBlockState stored = getStoredState(stack);
        if(stored == null || stored.getBlock().equals(Blocks.AIR)) return;

        Deque<BlockPos> placeable = filterBlocksToPlace(Minecraft.getMinecraft().player, Minecraft.getMinecraft().world, architectRange);
        if(!placeable.isEmpty()) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.ADDITIVEDARK.apply();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
            World w = Minecraft.getMinecraft().world;

            Tessellator tes = Tessellator.getInstance();
            BufferBuilder vb = tes.getBuffer();
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            for (BlockPos pos : placeable) {
                Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(stored, pos, w, vb);
            }
            vb.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX, (float) TileEntityRendererDispatcher.staticPlayerY, (float) TileEntityRendererDispatcher.staticPlayerZ);
            tes.draw();
            Blending.DEFAULT.apply();
            if(!blend) {
                GL11.glDisable(GL11.GL_BLEND);
            }
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty()) return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(hand));

        if (world.isRemote) return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        IBlockState stored = getStoredState(stack);
        ItemStack consumeStack = getStoredStateAsStack(stack);
        if(stored == null || stored.getBlock().equals(Blocks.AIR) || consumeStack.isEmpty()) return ActionResult.newResult(EnumActionResult.SUCCESS, stack);

        Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
        if(!placeable.isEmpty()) {
            for (BlockPos placePos : placeable) {
                if(drainTempCharge(playerIn, Config.architectWandUseCost, true)
                        && (playerIn.isCreative() || ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), true))) {
                    drainTempCharge(playerIn, Config.architectWandUseCost, false);
                    if(!playerIn.isCreative()) {
                        ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), false);
                    }
                    world.setBlockState(placePos, stored);
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ARCHITECT_PLACE, placePos);
                    ev.setAdditionalData(Block.getStateId(stored));
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if(stack.isEmpty()) return EnumActionResult.SUCCESS;

        if(playerIn.isSneaking()) {
            tryStoreBlock(stack, world, pos);
            return EnumActionResult.SUCCESS;
        } else {
            if(!world.isRemote) {
                IBlockState stored = getStoredState(stack);
                ItemStack consumeStack = getStoredStateAsStack(stack);
                if(stored == null || stored.getBlock().equals(Blocks.AIR) || consumeStack.isEmpty()) return EnumActionResult.SUCCESS;

                Deque<BlockPos> placeable = filterBlocksToPlace(playerIn, world, architectRange);
                if(!placeable.isEmpty()) {
                    for (BlockPos placePos : placeable) {
                        if(drainTempCharge(playerIn, Config.architectWandUseCost, true)
                                && (playerIn.isCreative() || ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), true))) {
                            drainTempCharge(playerIn, Config.architectWandUseCost, false);
                            if(!playerIn.isCreative()) {
                                ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), false);
                            }
                            world.setBlockState(placePos, stored);
                            PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ARCHITECT_PLACE, placePos);
                            ev.setAdditionalData(Block.getStateId(stored));
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                        }
                    }
                }
            }
        }

        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    public static void playArchitectPlaceEvent(PktParticleEvent event) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            Vector3 at = event.getVec();
            IBlockState state = Block.getStateById((int) Math.round(event.getAdditionalData()));
            RenderingUtils.playBlockBreakParticles(at.toBlockPos(), state);
            for (int i = 0; i < 9; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                        at.getX() + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                        at.getY() + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)),
                        at.getZ() + (itemRand.nextBoolean() ? -(itemRand.nextFloat() * 0.1) : 1 + (itemRand.nextFloat() * 0.1)));
                p.motion((itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                        (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1),
                        (itemRand.nextFloat() * 0.03F) * (itemRand.nextBoolean() ? 1 : -1));
                p.scale(0.35F).setColor(Color.WHITE.brighter());
            }
        }, 1);
    }

    private Deque<BlockPos> filterBlocksToPlace(Entity entity, World world, double range) {
        Deque<BlockPos> placeable = getBlocksToPlaceAt(entity, range);
        boolean discard = false;
        Iterator<BlockPos> iterator = placeable.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if(discard) {
                iterator.remove();
                continue;
            }
            if(!world.isAirBlock(pos) && !world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
                discard = true;
                iterator.remove();
            }
        }
        return placeable;
    }

    private Deque<BlockPos> getBlocksToPlaceAt(Entity entity, double range) {
        RayTraceResult rtr = getLookBlock(entity, false, true, range);
        if(rtr == null) {
            return Lists.newLinkedList();
        }
        LinkedList<BlockPos> blocks = Lists.newLinkedList();
        EnumFacing sideHit = rtr.sideHit;
        BlockPos hitPos = rtr.getBlockPos();
        int length;
        int cmpFrom;
        double cmpTo;
        switch (sideHit.getAxis()) {
            case X:
                cmpFrom = hitPos.getX();
                cmpTo = entity.posX;
                break;
            case Y:
                cmpFrom = hitPos.getY();
                cmpTo = entity.posY;
                break;
            case Z:
                cmpFrom = hitPos.getZ();
                cmpTo = entity.posZ;
                break;
            default:
                return Lists.newLinkedList();
        }
        length = (int) Math.min(20, Math.abs(cmpFrom + 0.5 - cmpTo));
        for (int i = 1; i < length; i++) {
            blocks.add(hitPos.offset(sideHit, i));
        }
        return blocks;
    }

}
