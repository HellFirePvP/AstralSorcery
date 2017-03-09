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
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkMap;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.sky.RenderRiftSkybox;
import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.ClientKeyboardInputEvent;
import hellfirepvp.astralsorcery.common.item.ItemAlignmentChargeRevealer;
import hellfirepvp.astralsorcery.common.item.ItemHudRender;
import hellfirepvp.astralsorcery.common.item.tool.ItemSkyResonator;
import hellfirepvp.astralsorcery.common.item.ItemHandRender;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SkyCollectionHelper;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientRenderEventHandler
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:43
 */
public class ClientRenderEventHandler {

    private static final BindableResource texChargeFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_frame");
    private static final BindableResource texChargeCharge = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_charge_charge");
    public static final BindableResource texHUDItemFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "hud_item_frame");

    private static final Map<ItemHudRender, ItemStackHudRenderInstance> ongoingItemRenders = new HashMap<>();

    private static final Random rand = new Random();
    private static final int fadeTicks = 15;
    private static final float visibilityChange = 1F / ((float) fadeTicks);

    private static int chargeRevealTicks = 0;
    private static float visibility = 0F; //0F-1F

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().world;
        if (((DataWorldSkyHandlers) SyncDataHolder.getDataClient(SyncDataHolder.DATA_SKY_HANDLERS)).hasWorldHandler(world)
                && world.provider.getDimension() != Config.dimensionIdSkyRift) {
            if (!(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
                world.provider.setSkyRenderer(new RenderSkybox(world, world.provider.getSkyRenderer()));
            }
        }
        if(world.provider.getDimension() == Config.dimensionIdSkyRift) {
            if (!(world.provider.getSkyRenderer() instanceof RenderRiftSkybox)) {
                world.provider.setSkyRenderer(new RenderRiftSkybox());
            }
        }

        playHandAndHudRenders(Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND), EnumHand.MAIN_HAND, event.getPartialTicks());
        playHandAndHudRenders(Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND),  EnumHand.OFF_HAND,  event.getPartialTicks());
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
            playItemEffects(Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND));
            playItemEffects(Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND));

            if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiJournalPerkMap) {
                requestChargeReveal(20);
            }
            chargeRevealTicks--;

            if((chargeRevealTicks - fadeTicks) < 0) {
                if(visibility > 0) {
                    visibility = Math.max(0, visibility - visibilityChange);
                }
            } else {
                if(visibility < 1) {
                    visibility = Math.min(1, visibility + visibilityChange);
                }
            }

            Iterator<Map.Entry<ItemHudRender, ItemStackHudRenderInstance>> iterator = ongoingItemRenders.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemHudRender, ItemStackHudRenderInstance> entry = iterator.next();
                ItemStackHudRenderInstance instance = entry.getValue();
                if(instance.active) {
                    instance.active = false;
                } else {
                    if(instance.visibility <= 0) {
                        iterator.remove();
                    } else {
                        instance.visibility = Math.max(0, instance.visibility - instance.visibilityChange);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playHandAndHudRenders(ItemStack inHand, EnumHand hand, float pTicks) {
        if(!inHand.isEmpty()) {
            Item i = inHand.getItem();
            if(i instanceof ItemHandRender) {
                ((ItemHandRender) i).onRenderWhileInHand(inHand, hand, pTicks);
            }
            if(i instanceof ItemHudRender) {
                if(((ItemHudRender) i).hasFadeIn()) {
                    if(!ongoingItemRenders.containsKey(i)) {
                        ongoingItemRenders.put((ItemHudRender) i, new ItemStackHudRenderInstance(inHand, 1F / ((float) ((ItemHudRender) i).getFadeInTicks())));
                    }
                    ItemStackHudRenderInstance instance = ongoingItemRenders.get(i);
                    instance.active = true;
                    instance.stack = inHand;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playItemEffects(ItemStack inHand) {
        if(!inHand.isEmpty()) {
            Item i = inHand.getItem();
            if(i instanceof ItemAlignmentChargeRevealer) {
                if(((ItemAlignmentChargeRevealer) i).shouldReveal(inHand)) {
                    requestChargeReveal(20);
                }
            }
            if(i instanceof ItemSkyResonator) {
                spawnSurfaceParticles();
            }
            if(i instanceof ItemHudRender) {
                ItemStackHudRenderInstance instance = ongoingItemRenders.get(i);
                if(instance != null) {
                    if(instance.visibility < 1) {
                        instance.visibility = Math.min(1, instance.visibility + instance.visibilityChange);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnSurfaceParticles() {
        if(!DataWorldSkyHandlers.hasWorldHandler(Minecraft.getMinecraft().world, Side.CLIENT)) return;
        if(!ConstellationSkyHandler.getInstance().getSeedIfPresent(Minecraft.getMinecraft().world).isPresent()) return;

        float nightPerc = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
        if(nightPerc >= 0.05) {
            Color c = new Color(0, 6, 58);
            BlockPos center = Minecraft.getMinecraft().player.getPosition();
            int offsetX = center.getX();
            int offsetZ = center.getZ();
            BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain(center);

            for (int xx = -30; xx <= 30; xx++) {
                for (int zz = -30; zz <= 30; zz++) {

                    BlockPos top = Minecraft.getMinecraft().world.getTopSolidOrLiquidBlock(pos.setPos(offsetX + xx, 0, offsetZ + zz));
                    //Can be force unwrapped since statement 2nd Line prevents non-present values.
                    Float opF = SkyCollectionHelper.getSkyNoiseDistributionClient(Minecraft.getMinecraft().world, top).get();

                    float fPerc = (float) Math.pow((opF - 0.4F) * 1.65F, 2);
                    if(opF >= 0.4F && rand.nextFloat() <= fPerc) {
                        if(rand.nextFloat() <= fPerc && rand.nextInt(6) == 0) {
                            EffectHelper.genericFlareParticle(top.getX() + rand.nextFloat(), top.getY() + 0.15, top.getZ() + rand.nextFloat())
                                    .scale(4F)
                                    .setColor(c)
                                    .enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID)
                                    .gravity(0.004)
                                    .setAlphaMultiplier(nightPerc * fPerc);
                            if(opF >= 0.8F && rand.nextInt(3) == 0) {
                                EffectHelper.genericFlareParticle(top.getX() + rand.nextFloat(), top.getY() + 0.15, top.getZ() + rand.nextFloat())
                                        .scale(0.3F)
                                        .setColor(Color.WHITE)
                                        .gravity(0.01)
                                        .setAlphaMultiplier(nightPerc);
                            }
                        }
                    }
                }
            }

            pos.release();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if(visibility > 0) {
                renderAlignmentChargeOverlay(event.getPartialTicks());
            }
            if(!ongoingItemRenders.isEmpty()) {
                for (Map.Entry<ItemHudRender, ItemStackHudRenderInstance> entry : new HashSet<>(ongoingItemRenders.entrySet())) {
                    if(!entry.getKey().hasFadeIn()) {
                        entry.getKey().onRenderInHandHUD(entry.getValue().stack, 1F, event.getPartialTicks());
                    } else {
                        entry.getKey().onRenderInHandHUD(entry.getValue().stack, entry.getValue().visibility, event.getPartialTicks());
                    }
                }
            }
            ItemStack inHand = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
            if(!inHand.isEmpty()) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if(!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.getPartialTicks());
                    }
                }
            }
            inHand = Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND);
            if(!inHand.isEmpty()) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if(!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.getPartialTicks());
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderAlignmentChargeOverlay(float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        float height  = 128F;
        float width   =  32F;
        float offsetX =  0F;
        float offsetY =  5F;

        texChargeFrame.bind();
        GL11.glColor4f(1F, 1F, 1F, visibility * 0.9F);

        //Draw hud itself
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, 10).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, 10).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          10).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          10).tex(0, 0).endVertex();
        tes.draw();

        //Draw charge
        float filled = ConstellationPerkLevelManager.getPercToNextLevel(ResearchManager.clientProgress);
        height = 78F;
        offsetY = 27.5F + (1F - filled) * height;
        GL11.glColor4f(255F / 255F, 230F / 255F, 0F / 255F, visibility * 0.9F);
        texChargeCharge.bind();
        height *= filled;

        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, 10).tex(0,           1).endVertex();
        vb.pos(offsetX + width, offsetY + height, 10).tex(1,           1).endVertex();
        vb.pos(offsetX + width, offsetY,          10).tex(1, 1F - filled).endVertex();
        vb.pos(offsetX,         offsetY,          10).tex(0, 1F - filled).endVertex();
        tes.draw();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        TextureHelper.refreshTextureBindState();
        //Draw level
        int level = ResearchManager.clientProgress.getAlignmentLevel();
        GL11.glColor4f(0.86F, 0.86F, 0.86F, visibility);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 13, 94, 0);
        GL11.glScaled(1.2, 1.2, 1.2);
        int c = 0x00DDDDDD;
        c |= ((int) (255F * visibility)) << 24;
        if(visibility > 0.1E-4) {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(String.valueOf(level + 1), 0, 0, c);
        }
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        TextureHelper.refreshTextureBindState();
        Blending.DEFAULT.apply();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.color(1F, 1F, 1F, 1F);
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

    static {
        ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelassec.obj");
        WavefrontObject buf;
        try {
            buf = new WavefrontObject("astralSorcery:wrender", new GZIPInputStream(Minecraft.getMinecraft().getResourceManager().getResource(mod).getInputStream()));
        } catch (Exception exc) {
            buf = null;
        }
        obj = buf;
    }

    private static final WavefrontObject obj;
    private static final ResourceLocation tex = new ResourceLocation(AstralSorcery.MODID + ":textures/models/texw.png");
    private static int dList = -1;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
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

    private static class ItemStackHudRenderInstance {

        private ItemStack stack;
        private float visibility = 0;
        private float visibilityChange;
        private boolean active = true;

        private ItemStackHudRenderInstance(ItemStack stack, float visibilityChange) {
            this.stack = stack;
            this.visibilityChange = visibilityChange;
        }
    }

}
