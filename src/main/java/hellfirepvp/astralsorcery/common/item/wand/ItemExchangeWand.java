/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
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
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
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

import java.util.Collection;

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
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
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
        ItemStack matchStack = getStoredStateAsStack(stack);
        if(stored == null || stored.getBlock().equals(Blocks.AIR) || matchStack.isEmpty()) return;

        EntityPlayer pl = Minecraft.getMinecraft().player;
        PlayerControllerMP ctrl = Minecraft.getMinecraft().playerController;
        if(ctrl == null || pl == null) return;
        RayTraceResult rtr = getLookBlock(pl, false, true, ctrl.getBlockReachDistance());
        if(rtr == null) return;

        IBlockAccess airWorld = new AirBlockRenderWorld(Biomes.PLAINS, Minecraft.getMinecraft().world.getWorldType());
        BlockPos origin = rtr.getBlockPos();
        IBlockState atOrigin = Minecraft.getMinecraft().world.getBlockState(origin);
        if(stored.getBlock().equals(atOrigin.getBlock()) && stored.getBlock().getMetaFromState(stored) == atOrigin.getBlock().getMetaFromState(atOrigin)) {
            return;
        }
        float hardness = atOrigin.getBlockHardness(Minecraft.getMinecraft().world, origin);
        if(Config.exchangeWandMaxHardness != -1) {
            if(hardness > Config.exchangeWandMaxHardness) {
                return;
            }
        }
        if(hardness == -1) {
            return;
        }
        int amt = 0;
        if (pl.isCreative()) {
            amt = -1;
        } else {
            if(Mods.BOTANIA.isPresent()) {
                amt = ModIntegrationBotania.getItemCount(Minecraft.getMinecraft().player, stack, stored);
            } else {
                for (ItemStack st : ItemUtils.findItemsInPlayerInventory(pl, matchStack, false)) {
                    amt += st.getCount();
                }
            }
        }
        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(Minecraft.getMinecraft().world, origin, true, searchDepth, amt, false);
        if(found.isEmpty()) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVEDARK.apply();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        for (BlockPos pos : found.getPattern().keySet()) {
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlock(stored, pos, airWorld, vb);
        }
        vb.sortVertexData((float) TileEntityRendererDispatcher.staticPlayerX, (float) TileEntityRendererDispatcher.staticPlayerY, (float) TileEntityRendererDispatcher.staticPlayerZ);
        tes.draw();

        Blending.DEFAULT.apply();
        if(!blend) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
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

        IBlockState stored = getStoredState(stack);
        ItemStack consumeStack = getStoredStateAsStack(stack);
        if(stored == null || stored.getBlock().equals(Blocks.AIR) || consumeStack.isEmpty()) return EnumActionResult.SUCCESS;
        IBlockState atOrigin = world.getBlockState(origin);
        if(stored.getBlock().equals(atOrigin.getBlock()) && stored.getBlock().getMetaFromState(stored) == atOrigin.getBlock().getMetaFromState(atOrigin)) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState atState = world.getBlockState(origin);
        float hardness = atState.getBlockHardness(world, origin);
        if(Config.exchangeWandMaxHardness != -1) {
            if(hardness > Config.exchangeWandMaxHardness) {
                return EnumActionResult.SUCCESS;
            }
        }
        if(hardness == -1) {
            return EnumActionResult.SUCCESS;
        }

        int amt = 0;
        if (playerIn.isCreative()) {
            amt = -1;
        } else {
            if(Mods.BOTANIA.isPresent()) {
                amt = ModIntegrationBotania.getItemCount(playerIn, consumeStack, stored);
            } else {
                for (ItemStack st : ItemUtils.findItemsInPlayerInventory(playerIn, consumeStack, false)) {
                    amt += st.getCount();
                }
            }
        }
        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(world, origin, true, searchDepth, amt, false);
        if (found.isEmpty()) return EnumActionResult.SUCCESS;

        for (BlockPos placePos : found.getPattern().keySet()) {
            if(drainTempCharge(playerIn, Config.exchangeWandUseCost, true)
                    && (playerIn.isCreative() || ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), true))) {
                if(((EntityPlayerMP) playerIn).interactionManager.tryHarvestBlock(placePos)) {
                    drainTempCharge(playerIn, Config.exchangeWandUseCost, false);
                    if(!playerIn.isCreative()) {
                        ItemUtils.consumeFromPlayerInventory(playerIn, stack, ItemUtils.copyStackWithSize(consumeStack, 1), false);
                    }
                    world.setBlockState(placePos, stored);
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ARCHITECT_PLACE, placePos);
                    ev.setAdditionalData(Block.getStateId(atOrigin));
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
                }
            }
        }

        return EnumActionResult.SUCCESS;
    }

}
