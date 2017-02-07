/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.client.event.ClientRenderEventHandler;
import hellfirepvp.astralsorcery.client.util.AirBlockRenderWorld;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.ItemHudRender;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockDiscoverer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemExchangeWand
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:03
 */
public class ItemExchangeWand extends Item implements ItemHandRender, ItemHudRender, ItemAlignmentChargeConsumer {

    private static final int searchDepth = 5;

    public ItemExchangeWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRenderInHandHUD(ItemStack lastCacheInstance, float fadeAlpha, float pTicks) {
        IBlockState stored = getStoredState(lastCacheInstance);
        if(stored == null || stored.getBlock().equals(Blocks.AIR)) return;
        Item i = Item.getItemFromBlock(stored.getBlock());
        if(i == null) return;
        int dmg = stored.getBlock().getMetaFromState(stored);

        int amtFound = 0;
        java.util.List<ItemStack> stacks = ItemUtils.scanInventoryFor(new InvWrapper(Minecraft.getMinecraft().player.inventory), i);
        for (ItemStack stack : stacks) {
            if(stack.getItemDamage() == dmg) {
                amtFound += stack.stackSize;
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
        VertexBuffer vb = tes.getBuffer();

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
        ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, new ItemStack(i, 1, dmg), offsetX + 5, offsetY + 5);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha(); //Because Mc item rendering..

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 13, offsetY + 16, 0);
        GL11.glScaled(0.7, 0.7, 0.7);
        int c = 0x00DDDDDD;
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(amtFound), 0, 0, c);
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
        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(Minecraft.getMinecraft().world, origin, true, searchDepth);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.ADDITIVEDARK.apply();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
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
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos origin, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.isRemote) return EnumActionResult.SUCCESS;

        IBlockState stored = getStoredState(stack);
        if(stored == null || stored.getBlock().equals(Blocks.AIR)) return EnumActionResult.SUCCESS;
        IBlockState atOrigin = world.getBlockState(origin);
        if(stored.getBlock().equals(atOrigin.getBlock()) && stored.getBlock().getMetaFromState(stored) == atOrigin.getBlock().getMetaFromState(atOrigin)) {
            return EnumActionResult.SUCCESS;
        }

        BlockArray found = BlockDiscoverer.discoverBlocksWithSameStateAround(world, origin, true, searchDepth);
        for (BlockPos placePos : found.getPattern().keySet()) {
            if(hasAtLeastCharge(playerIn, Side.SERVER, Config.exchangeWandUseCost)) {
                drainCharge(playerIn, Config.exchangeWandUseCost);
                world.setBlockState(placePos, stored);
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.ARCHITECT_PLACE, placePos);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, placePos, 40));
            }
        }

        return EnumActionResult.SUCCESS;
    }

    public static IBlockState getStoredState(ItemStack stack) {
        return Blocks.SAND.getDefaultState();
    }

}
