package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 12.05.2016 / 17:44
 */
public final class EffectHandler {

    private static int clientEffectTick = 0;

    public static final EffectHandler instance = new EffectHandler();

    public static final Map<IComplexEffect.RenderTarget, List<IComplexEffect>> complexEffects = new HashMap<>();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            synchronized (complexEffects) {
                complexEffects.get(IComplexEffect.RenderTarget.OVERLAY_TEXT).stream().forEach((effect) -> effect.render(event.getPartialTicks()));
            }
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        synchronized (complexEffects) {
            complexEffects.get(IComplexEffect.RenderTarget.RENDERLOOP).stream().forEach((effect) -> effect.render(event.getPartialTicks()));
        }
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onClTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tick();
    }

    public OverlayText overlayText(String message, int tickTimeout, OverlayText.OverlayTextProperties properties) {
        OverlayText text;
        if (properties == null) {
            text = new OverlayText(message, tickTimeout);
        } else {
            text = new OverlayText(message, tickTimeout, properties);
        }
        register(text);
        return text;
    }

    public TexturePlane texturePlane(BindableResource texture, Axis rotationAxis) {
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
        new Thread(() -> {
            synchronized (complexEffects) {
                complexEffects.get(effect.getRenderTarget()).add(effect);
            }
        }).start();
    }

    /*public void unregister(final IComplexEffect effect) {
        new Thread(() -> {
            synchronized (lock) {
                complexEffects.get(effect.getRenderTarget()).remove(effect);
            }
        }).start();
    }*/

    public void tick() {
        clientEffectTick++;

        synchronized (complexEffects) {
            for (IComplexEffect.RenderTarget target : complexEffects.keySet()) {
                Iterator<IComplexEffect> iterator = complexEffects.get(target).iterator();
                while (iterator.hasNext()) {
                    IComplexEffect effect = iterator.next();
                    effect.tick();
                    if(effect.canRemove()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static int getClientEffectTick() {
        return clientEffectTick;
    }

    static {
        for (IComplexEffect.RenderTarget target : IComplexEffect.RenderTarget.values()) {
            complexEffects.put(target, new LinkedList<>());
        }
    }

    public static void cleanUp() {
        synchronized (complexEffects) {
            for (IComplexEffect.RenderTarget t : IComplexEffect.RenderTarget.values()) {
                complexEffects.get(t).clear();
            }
        }
    }
}
