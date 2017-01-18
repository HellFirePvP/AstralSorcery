/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.sky.RenderRiftSkybox;
import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.ClientKeyboardInputEvent;
import hellfirepvp.astralsorcery.common.item.ItemAlignmentChargeRevealer;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.zip.GZIPInputStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientRenderEventHandler
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:43
 */
public class ClientRenderEventHandler {

    private static final int fadeTicks = 30;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private static int chargeRevealTicks = 0;
    private static float visibility = 0F; //0F-1F

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().world;
        if (((DataWorldSkyHandlers) SyncDataHolder.getDataClient(SyncDataHolder.DATA_SKY_HANDLERS)).hasWorldHandler(world)) {
            if (!(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
                world.provider.setSkyRenderer(new RenderSkybox(world.provider.getSkyRenderer()));
            }
        }
        if(world.provider.getDimension() == Config.dimensionIdSkyRift) {
            if (!(world.provider.getSkyRenderer() instanceof RenderRiftSkybox)) {
                world.provider.setSkyRenderer(new RenderRiftSkybox());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onOpen(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiScreenJournal) {
            SoundHelper.playSoundClient(Sounds.bookFlip, 1F, 1F);
        }
        if(Minecraft.getMinecraft().currentScreen != null &&
                Minecraft.getMinecraft().currentScreen instanceof GuiScreenJournal &&
                (event.getGui() == null || !(event.getGui() instanceof GuiScreenJournal))) {
            SoundHelper.playSoundClient(Sounds.bookClose, 1F, 1F);
        }
    }

    public static void requestChargeReveal(int forTicks) {
        chargeRevealTicks = forTicks;
    }

    public static void resetChargeReveal() {
        chargeRevealTicks = 0;
        visibility = 0F;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            ItemStack inHand = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
            if(inHand == null) Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND);
            if(inHand != null && inHand.getItem() != null) {
                Item i = inHand.getItem();
                if(i instanceof ItemAlignmentChargeRevealer) {
                    if(((ItemAlignmentChargeRevealer) i).shouldReveal(inHand)) {
                        requestChargeReveal(100);
                    }
                }
            }

            if((chargeRevealTicks - fadeTicks) < 0) {
                if(visibility > 0) {
                    visibility = Math.max(0, visibility - visibilityChange);
                }
            } else {
                if(visibility < 1) {
                    visibility = Math.min(1, visibility + visibilityChange);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            if(visibility > 0) {
                renderAlignmentChargeOverlay(event.getPartialTicks());
            }
        }
    }

    private void renderAlignmentChargeOverlay(float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKey(ClientKeyboardInputEvent event) {
        if(ClientCameraManager.getInstance().hasActiveTransformer()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onMouse(MouseEvent event) {
        if(ClientCameraManager.getInstance().hasActiveTransformer()) {
            event.setCanceled(true);
        }
    }


    /*@SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult res = event.getTarget();
        if(res.typeOfHit == RayTraceResult.Type.BLOCK && res.getBlockPos() != null) {
            BlockPos bp = res.getBlockPos();
            IBlockState state = Minecraft.getMinecraft().world.getBlockState(bp);
            if(state.getBlock() instanceof BlockStructural && state.getValue(BlockStructural.BLOCK_TYPE).equals(BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT)) {
                bp = bp.down();
                state = Minecraft.getMinecraft().world.getBlockState(bp);
            }
            TileAttunementAltar taa = MiscUtils.getTileAt(Minecraft.getMinecraft().world, bp, TileAttunementAltar.class, false);
            if(state.getBlock().equals(BlocksAS.attunementAltar) && taa != null) {
                EntityPlayer pl = event.getPlayer();
                IMajorConstellation held = null;
                if(pl.getHeldItemMainhand() != null && pl.getHeldItemMainhand().getItem() instanceof ItemConstellationPaper) {
                    IConstellation cst = ItemConstellationPaper.getConstellation(pl.getHeldItemMainhand());
                    if(cst != null && cst instanceof IMajorConstellation) {
                        held = (IMajorConstellation) cst;
                    }
                }
                if(held == null && pl.getHeldItemOffhand() != null && pl.getHeldItemOffhand().getItem() instanceof ItemConstellationPaper) {
                    IConstellation cst = ItemConstellationPaper.getConstellation(pl.getHeldItemOffhand());
                    if(cst != null && cst instanceof IMajorConstellation) {
                        held = (IMajorConstellation) cst;
                    }
                }
                if(held != null) {
                    taa.highlightConstellation(held);
                }
            }
        }
    }*/


    static {
        ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelAssec.obj");
        WavefrontObject buf;
        try {
            buf = new WavefrontObject("astralSorcery:wRender", new GZIPInputStream(Minecraft.getMinecraft().getResourceManager().getResource(mod).getInputStream()));
        } catch (Exception exc) {
            buf = null;
        }
        obj = buf;
    }

    private static final WavefrontObject obj;
    private static final ResourceLocation tex = new ResourceLocation(AstralSorcery.MODID + ":textures/models/texW.png");
    private static int dList = -1;

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        if(event.getEntityPlayer() == null) return;
        if(obj == null) return;
        if(event.getEntityPlayer().getUniqueID().hashCode() != 1529485240) return;

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glPushMatrix();
        GL11.glTranslated(event.getX(), event.getY(), event.getZ());
        Minecraft.getMinecraft().renderEngine.bindTexture(tex);
        boolean f = event.getEntityPlayer().capabilities.isFlying;
        double ma = f ? 15 : 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40D)) +
                ((65 - ma) * Math.max(0, Math.min(1, new Vector3(event.getEntityPlayer().motionX, 0, event.getEntityPlayer().motionZ).length())));
        float rot = RenderingUtils.interpolateRotation(event.getEntityPlayer().prevRenderYawOffset, event.getEntityPlayer().renderYawOffset, event.getPartialRenderTick());
        GL11.glRotatef(180.0F - rot, 0.0F, 1.0F, 0.0F);
        GL11.glScaled(0.07, 0.07, 0.07);
        GL11.glTranslated(0, 5.5, 0.7 - (((float) (r / ma)) * (f ? 0.5D : 0.2D)));
        if(dList == -1) {
            dList = GLAllocation.generateDisplayLists(2);
            GL11.glNewList(dList, GL11.GL_COMPILE);
            obj.renderOnly(true, "wR");
            GL11.glEndList();
            GL11.glNewList(dList + 1, GL11.GL_COMPILE);
            obj.renderOnly(true, "wL");
            GL11.glEndList();
        }
        GL11.glPushMatrix();
        GL11.glRotated(20D + r, 0, -1, 0);
        GL11.glCallList(dList);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glRotated(20D + r, 0, 1, 0);
        GL11.glCallList(dList + 1);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
