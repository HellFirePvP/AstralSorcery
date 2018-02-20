/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.client.effect.compound.CompoundObjectEffect;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightning;
import hellfirepvp.astralsorcery.client.effect.texture.TexturePlane;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.event.ClientGatewayHandler;
import hellfirepvp.astralsorcery.client.render.tile.TESRMapDrawingTable;
import hellfirepvp.astralsorcery.client.render.tile.TESRPrismLens;
import hellfirepvp.astralsorcery.client.render.tile.TESRTranslucentBlock;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.tile.IMultiblockDependantTile;
import hellfirepvp.astralsorcery.common.util.Counter;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 12.05.2016 / 17:44
 */
public final class EffectHandler {

    public static final Random STATIC_EFFECT_RAND = new Random();
    public static final EffectHandler instance = new EffectHandler();

    private static boolean acceptsNewParticles = true, cleanRequested = false;
    private static List<IComplexEffect> toAddBuffer = new LinkedList<>();

    private UIGateway uiGateway = null;
    private int gatewayUITicks = 0;
    public boolean renderGateway = true;

    private StructureMatchPreview structurePreview = null;

    public static final Map<IComplexEffect.RenderTarget, Map<Integer, List<IComplexEffect>>> complexEffects = new HashMap<>();
    public static final List<EntityFXFacingDepthParticle> fastRenderDepthParticles = new LinkedList<>();
    public static final List<EntityFXFacingParticle> fastRenderParticles = new LinkedList<>();
    public static final List<EffectLightning> fastRenderLightnings = new LinkedList<>();
    public static final Map<CompoundObjectEffect.ObjectGroup, List<CompoundObjectEffect>> objects = new HashMap<>();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return instance;
    }

    public void requestGatewayUIFor(World world, Vector3 pos, double sphereRadius) {
        if(uiGateway == null || !uiGateway.getPos().equals(pos)) {
            uiGateway = UIGateway.initialize(world, pos, sphereRadius);
        }
        gatewayUITicks = 20;
    }

    public void requestStructurePreviewFor(IMultiblockDependantTile tile) {
        if(!(tile instanceof TileEntity)) return;
        if(structurePreview == null || !structurePreview.isOriginatingFrom(tile)) {
            structurePreview = new StructureMatchPreview(tile);
        }
        structurePreview.resetTimeout();
    }

    @Nullable
    public UIGateway getUiGateway() {
        return uiGateway;
    }

    public static int getDebugEffectCount() {
        final Counter c = new Counter(0);
        for (Map<Integer, List<IComplexEffect>> effects : complexEffects.values()) {
            for (List<IComplexEffect> eff : effects.values()) {
                c.value += eff.size();
            }
        }
        c.value += fastRenderParticles.size();
        c.value += fastRenderLightnings.size();
        objects.values().forEach((l) -> c.value += l.size());
        return c.value;
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            acceptsNewParticles = false;
            Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects.get(IComplexEffect.RenderTarget.OVERLAY_TEXT);
            for (int i = 0; i <= 2; i++) {
                for (IComplexEffect effect : layeredEffects.get(i)) {
                    GL11.glPushMatrix();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    effect.render(event.getPartialTicks());
                    GL11.glPopAttrib();
                    GL11.glPopMatrix();
                }
            }
            acceptsNewParticles = true;
        }
    }

    @SubscribeEvent
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            event.getLeft().add("");
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " EffectHandler:");
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " > Complex effects: " + getDebugEffectCount());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(RenderWorldLastEvent event) {
        TESRPrismLens.renderColoredPrismsLast();
        float pTicks = event.getPartialTicks();
        acceptsNewParticles = false;
        for (CompoundObjectEffect.ObjectGroup og : objects.keySet()) {
            og.prepareGLContext();
            for (CompoundObjectEffect effect : objects.get(og)) {
                effect.render(pTicks);
            }
            og.revertGLContext();
        }
        if(uiGateway != null) {
            if(renderGateway) {
                uiGateway.renderIntoWorld(pTicks);
            }
            if(ClientGatewayHandler.focusingEntry != null) {
                uiGateway.renderGatewayTarget(pTicks);
            }
        }
        if(structurePreview != null) {
            structurePreview.appendPreviewBlocks();
        }
        GlStateManager.disableDepth();
        EntityFXFacingParticle.renderFast(pTicks, fastRenderDepthParticles);
        GlStateManager.enableDepth();
        EntityFXFacingParticle.renderFast(pTicks, fastRenderParticles);
        EffectLightning.renderFast(pTicks, fastRenderLightnings);

        Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects.get(IComplexEffect.RenderTarget.RENDERLOOP);
        for (int i = 0; i <= 2; i++) {
            for (IComplexEffect effect : layeredEffects.get(i)) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                effect.render(pTicks);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
        acceptsNewParticles = true;
        TESRMapDrawingTable.renderRemainingGlasses(pTicks);
        TESRTranslucentBlock.renderTranslucentBlocks();
    }

    @SubscribeEvent
    public void onClTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tick();

        /*if(Minecraft.getMinecraft().player == null) return;
        if(ClientScheduler.getClientTick() % 10 != 0) return;
        ItemStack main = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(main != null && main.getItem() instanceof ItemIlluminationWand) {
            RayTraceResult res = MiscUtils.rayTraceLook(Minecraft.getMinecraft().player, 60);
            if(res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
                EffectLightning.buildAndRegisterLightning(new Vector3(res.getBlockPos()).addY(7), new Vector3(res.getBlockPos()));
            }
        }*/
    }

    public EntityComplexFX registerFX(EntityComplexFX entityComplexFX) {
        register(entityComplexFX);
        return entityComplexFX;
    }

    public EffectTranslucentFallingBlock translucentFallingBlock(Vector3 position, IBlockState state) {
        EffectTranslucentFallingBlock block = new EffectTranslucentFallingBlock(state);
        block.setPosition(position.clone().add(-0.5, -0.5, -0.5));
        register(block);
        return block;
    }

    public EffectLightning lightning(Vector3 from, Vector3 to) {
        return EffectLightning.buildAndRegisterLightning(from, to);
    }

    public OrbitalEffectController orbital(OrbitalEffectController.OrbitPointEffect pointEffect, @Nullable OrbitalEffectController.OrbitPersistence persistence, @Nullable OrbitalEffectController.OrbitTickModifier tickModifier) {
        OrbitalEffectController ctrl = new OrbitalEffectController(pointEffect, persistence, tickModifier);
        register(ctrl);
        return ctrl;
    }

    public TextureSpritePlane textureSpritePlane(SpriteSheetResource sheetResource, Vector3 rotationAxis) {
        TextureSpritePlane plane = new TextureSpritePlane(sheetResource, rotationAxis);
        register(plane);
        return plane;
    }

    public TexturePlane texturePlane(BindableResource texture, Vector3 rotationAxis) {
        TexturePlane plane = new TexturePlane(texture, rotationAxis);
        register(plane);
        return plane;
    }

    public EffectLightbeam lightbeam(Vector3 from, Vector3 to, double beamRadSize) {
        EffectLightbeam beam = new EffectLightbeam(from, to, beamRadSize);
        register(beam);
        return beam;
    }

    public EffectLightbeam lightbeam(Vector3 from, Vector3 to, double fromBeamSize, double toBeamSize) {
        EffectLightbeam beam = new EffectLightbeam(from, to, fromBeamSize, toBeamSize);
        register(beam);
        return beam;
    }

    private void register(final IComplexEffect effect) {
        if(AssetLibrary.reloading || effect == null || Minecraft.getMinecraft().isGamePaused()) return;

        //instead of getEffeciveSide - neither is pretty, but this at least prevents async editing.
        if (!Thread.currentThread().getName().contains("Client thread")) {
            AstralSorcery.proxy.scheduleClientside(() -> register(effect));
            return;
        }

        if(acceptsNewParticles) {
            registerUnsafe(effect);
        } else {
            toAddBuffer.add(effect);
        }
    }

    private void registerUnsafe(IComplexEffect effect) {
        if(!mayAcceptParticle(effect)) return;

        if(effect instanceof EffectLightning) {
            fastRenderLightnings.add((EffectLightning) effect);
        } else if(effect instanceof EntityFXFacingDepthParticle) {
            fastRenderDepthParticles.add((EntityFXFacingDepthParticle) effect);
        } else if(effect instanceof EntityFXFacingParticle) {
            fastRenderParticles.add((EntityFXFacingParticle) effect);
        } else if(effect instanceof CompoundObjectEffect) {
            CompoundObjectEffect.ObjectGroup group = ((CompoundObjectEffect) effect).getGroup();
            if(!objects.containsKey(group)) objects.put(group, new LinkedList<>());
            objects.get(group).add((CompoundObjectEffect) effect);
        } else {
            complexEffects.get(effect.getRenderTarget()).get(effect.getLayer()).add(effect);
        }
        effect.clearRemoveFlag();
    }

    public void tick() {
        if(cleanRequested) {
            for (IComplexEffect.RenderTarget t : IComplexEffect.RenderTarget.values()) {
                for (int i = 0; i <= 2; i++) {
                    List<IComplexEffect> effects = complexEffects.get(t).get(i);
                    effects.forEach(IComplexEffect::flagAsRemoved);
                    effects.clear();
                }
            }
            fastRenderParticles.clear();
            fastRenderLightnings.clear();
            objects.clear();
            toAddBuffer.clear();
            uiGateway = null;
            structurePreview = null;
            gatewayUITicks = 0;
            cleanRequested = false;
        }
        if(Minecraft.getMinecraft().player == null) {
            return;
        }

        if(structurePreview != null) {
            structurePreview.tick();
            if(structurePreview.shouldBeRemoved()) {
                structurePreview = null;
            }
        }

        if(gatewayUITicks > 0) {
            gatewayUITicks--;
            if(gatewayUITicks <= 0) {
                uiGateway = null;
            }
        }

        acceptsNewParticles = false;
        for (IComplexEffect.RenderTarget target : complexEffects.keySet()) {
            Map<Integer, List<IComplexEffect>> layeredEffects = complexEffects.get(target);
            for (int i = 0; i <= 2; i++) {
                Iterator<IComplexEffect> iterator = layeredEffects.get(i).iterator();
                while (iterator.hasNext()) {
                    IComplexEffect effect = iterator.next();
                    effect.tick();
                    if(effect.canRemove()) {
                        effect.flagAsRemoved();
                        iterator.remove();
                    }
                }
            }
        }

        Vector3 playerPos = Vector3.atEntityCorner(Minecraft.getMinecraft().player);
        for (EntityFXFacingParticle effect : new ArrayList<>(fastRenderParticles)) {
            if (effect == null) {
                fastRenderParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove() || effect.getPosition().distanceSquared(playerPos) >= Config.maxEffectRenderDistanceSq) {
                effect.flagAsRemoved();
                fastRenderParticles.remove(effect);
            }
        }
        for (EntityFXFacingParticle effect : new ArrayList<>(fastRenderDepthParticles)) {
            if (effect == null) {
                fastRenderDepthParticles.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove() || effect.getPosition().distanceSquared(playerPos) >= Config.maxEffectRenderDistanceSq) {
                effect.flagAsRemoved();
                fastRenderDepthParticles.remove(effect);
            }
        }
        for (EffectLightning effect : new ArrayList<>(fastRenderLightnings)) {
            if (effect == null) {
                fastRenderLightnings.remove(null);
                continue;
            }
            effect.tick();
            if (effect.canRemove()) {
                effect.flagAsRemoved();
                fastRenderLightnings.remove(effect);
            }
        }
        Iterator<CompoundObjectEffect.ObjectGroup> itGroups = objects.keySet().iterator();
        while (itGroups.hasNext()) {
            CompoundObjectEffect.ObjectGroup group = itGroups.next();
            List<CompoundObjectEffect> effects = objects.get(group);
            if(effects == null || effects.isEmpty()) {
                itGroups.remove();
                continue;
            }
            Iterator<CompoundObjectEffect> itObjects = effects.iterator();
            while (itObjects.hasNext()) {
                CompoundObjectEffect effect = itObjects.next();
                if (effect == null) {
                    itObjects.remove();
                    continue;
                }
                effect.tick();
                if (effect.canRemove()) {
                    effect.flagAsRemoved();
                    itObjects.remove();
                }
            }
        }
        acceptsNewParticles = true;
        List<IComplexEffect> effects = new LinkedList<>(toAddBuffer);
        toAddBuffer.clear();
        for (IComplexEffect eff : effects) {
            registerUnsafe(eff);
        }
    }

    public static boolean mayAcceptParticle(IComplexEffect effect) {
        int cfg = Config.particleAmount;
        if(cfg > 1 && !Minecraft.isFancyGraphicsEnabled()) {
            cfg = 1;
        }
        if(effect instanceof IComplexEffect.PreventRemoval || cfg == 2) return true;
        return cfg == 1 && STATIC_EFFECT_RAND.nextInt(3) == 0;
    }

    static {
        for (IComplexEffect.RenderTarget target : IComplexEffect.RenderTarget.values()) {
            Map<Integer, List<IComplexEffect>> layeredEffects = new HashMap<>();
            for (int i = 0; i <= 2; i++) {
                layeredEffects.put(i, new LinkedList<>());
            }
            complexEffects.put(target, layeredEffects);
        }
    }

    public static void cleanUp() {
        cleanRequested = true;
    }
}
