/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkMap;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.data.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.item.base.render.ItemAlignmentChargeRevealer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHandRender;
import hellfirepvp.astralsorcery.common.item.base.render.ItemHudRender;
import hellfirepvp.astralsorcery.common.item.tool.ItemSkyResonator;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.util.SoundHelper;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopEffectHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.List;
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

    private static int chargePermRevealTicks = 0;
    private static float visibilityPermCharge = 0F; //0F-1F

    private static int chargeTempRevealTicks = 0;
    private static float visibilityTempCharge = 0F;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().world;
        if(Config.constellationSkyDimWhitelist.contains(world.provider.getDimension())) {
            if (!(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
                world.provider.setSkyRenderer(new RenderSkybox(world, world.provider.getSkyRenderer()));
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

    public static void requestPermChargeReveal(int forTicks) {
        chargePermRevealTicks = forTicks;
    }

    public static void resetPermChargeReveal() {
        chargePermRevealTicks = 0;
        visibilityPermCharge = 0F;
    }

    public static void requestTempChargeReveal(int forTicks) {
        chargeTempRevealTicks = forTicks;
    }

    public static void resetTempChargeReveal() {
        chargeTempRevealTicks = 0;
        visibilityTempCharge = 0F;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            playItemEffects(Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND));
            playItemEffects(Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND));

            tickTimeFreezeEffects();

            if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiJournalPerkMap) {
                requestPermChargeReveal(20);
            }
            chargePermRevealTicks--;
            chargeTempRevealTicks--;

            if((chargePermRevealTicks - fadeTicks) < 0) {
                if(visibilityPermCharge > 0) {
                    visibilityPermCharge = Math.max(0, visibilityPermCharge - visibilityChange);
                }
            } else {
                if(visibilityPermCharge < 1) {
                    visibilityPermCharge = Math.min(1, visibilityPermCharge + visibilityChange);
                }
            }

            if((chargeTempRevealTicks - fadeTicks) < 0) {
                if(visibilityTempCharge > 0) {
                    visibilityTempCharge = Math.max(0, visibilityTempCharge - visibilityChange);
                }
            } else {
                if(visibilityTempCharge < 1) {
                    visibilityTempCharge = Math.min(1, visibilityTempCharge + visibilityChange);
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
    private void tickTimeFreezeEffects() {
        World w = Minecraft.getMinecraft().world;
        if(w != null && w.provider != null) {
            List<TimeStopEffectHelper> effects =
                    ((DataTimeFreezeEffects) SyncDataHolder.getData(Side.CLIENT, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS))
                            .client_getTimeStopEffects(w);

            if(effects != null) {
                for (TimeStopEffectHelper helper : effects) {
                    helper.playClientTickEffect();
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
                if(((ItemAlignmentChargeRevealer) i).shouldReveal(ItemAlignmentChargeRevealer.ChargeType.PERM, inHand)) {
                    requestPermChargeReveal(20);
                }
                if(((ItemAlignmentChargeRevealer) i).shouldReveal(ItemAlignmentChargeRevealer.ChargeType.TEMP, inHand)) {
                    requestTempChargeReveal(20);
                }
            }
            if(i instanceof ItemSkyResonator) {
                ItemSkyResonator.ResonatorUpgrade upgrade = ItemSkyResonator.getCurrentUpgrade(Minecraft.getMinecraft().player, inHand);
                upgrade.playResonatorEffects();
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

    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if(visibilityTempCharge > 0) {
                SpriteSheetResource ssr = SpriteLibrary.spriteCharge;
                ssr.getResource().bind();

                ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
                int width = res.getScaledWidth();
                int height = res.getScaledHeight();
                int barWidth = 194;
                int offsetLeft = width / 2 - barWidth / 2;
                int offsetTop = height + 3 - 54; //*sigh* vanilla

                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                Tuple<Double, Double> uvPos = ssr.getUVOffset(ClientScheduler.getClientTick());

                float percFilled = Minecraft.getMinecraft().player.isCreative() ? 1F : PlayerChargeHandler.INSTANCE.clientCharge;
                double uLength = ssr.getULength() * percFilled;

                GlStateManager.color(1F, 1F, 1F, visibilityTempCharge);
                Tessellator tes = Tessellator.getInstance();
                BufferBuilder vb = tes.getBuffer();
                vb.begin(7, DefaultVertexFormats.POSITION_TEX);
                vb.pos(offsetLeft,            offsetTop + 27, 10).tex(uvPos.key, uvPos.value + ssr.getVLength()).endVertex();
                vb.pos(offsetLeft + barWidth * percFilled, offsetTop + 27, 10).tex(uvPos.key + uLength, uvPos.value + ssr.getVLength()).endVertex();
                vb.pos(offsetLeft + barWidth * percFilled, offsetTop,          10).tex(uvPos.key + uLength, uvPos.value).endVertex();
                vb.pos(offsetLeft,            offsetTop,         10).tex(uvPos.key, uvPos.value).endVertex();
                tes.draw();
                GlStateManager.enableAlpha();
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);

                TextureHelper.refreshTextureBindState();
            }

            if(visibilityPermCharge > 0) {
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
                GlStateManager.color(1F, 1F, 1F, 1F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
            }
            ItemStack inHand = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
            if(!inHand.isEmpty()) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if(!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.getPartialTicks());
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GL11.glColor4f(1F, 1F, 1F, 1F);
                    }
                }
            }
            inHand = Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND);
            if(!inHand.isEmpty()) {
                Item i = inHand.getItem();
                if (i instanceof ItemHudRender) {
                    if(!((ItemHudRender) i).hasFadeIn()) {
                        ((ItemHudRender) i).onRenderInHandHUD(inHand, 1F, event.getPartialTicks());
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GL11.glColor4f(1F, 1F, 1F, 1F);
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
        GL11.glColor4f(1F, 1F, 1F, visibilityPermCharge * 0.9F);

        //Draw hud itself
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
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
        GL11.glColor4f(255F / 255F, 230F / 255F, 0F / 255F, visibilityPermCharge * 0.9F);
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
        GL11.glColor4f(0.86F, 0.86F, 0.86F, visibilityPermCharge);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX + 13, 94, 0);
        GL11.glScaled(1.2, 1.2, 1.2);
        int c = 0x00DDDDDD;
        c |= ((int) (255F * visibilityPermCharge)) << 24;
        if(visibilityPermCharge > 0.1E-4) {
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

        if(event.getEntityPlayer().isRiding() || event.getEntityPlayer().isElytraFlying()) return;

        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY(), event.getZ());
        Minecraft.getMinecraft().renderEngine.bindTexture(tex);
        boolean f = event.getEntityPlayer().capabilities.isFlying;
        double ma = f ? 15 : 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40D)) +
                ((65 - ma) * Math.max(0, Math.min(1, new Vector3(event.getEntityPlayer().motionX, 0, event.getEntityPlayer().motionZ).length())));
        float rot = RenderingUtils.interpolateRotation(event.getEntityPlayer().prevRenderYawOffset, event.getEntityPlayer().renderYawOffset, event.getPartialRenderTick());
        GlStateManager.rotate(180F - rot, 0F, 1F, 0F);
        GlStateManager.scale(0.07, 0.07, 0.07);
        GlStateManager.translate(0, 5.5, 0.7 - (((float) (r / ma)) * (f ? 0.5D : 0.2D)));
        if(dList == -1) {
            dList = GLAllocation.generateDisplayLists(2);
            GlStateManager.glNewList(dList, GL11.GL_COMPILE);
            obj.renderOnly(true, "wR");
            GlStateManager.glEndList();
            GlStateManager.glNewList(dList + 1, GL11.GL_COMPILE);
            obj.renderOnly(true, "wL");
            GlStateManager.glEndList();
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (20.0 + r), 0, -1, 0);
        GlStateManager.callList(dList);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.rotate((float) (20.0 + r), 0, 1, 0);
        GlStateManager.callList(dList + 1);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
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
