package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.text.OverlayText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectHandler
 * Created by HellFirePvP
 * Date: 12.05.2016 / 17:44
 */
public final class EffectHandler {

    public static final EffectHandler instance = new EffectHandler();

    public static List<OverlayText> overlayTexts = new ArrayList<OverlayText>();

    public static final Object lock = new Object();

    private EffectHandler() {}

    public static EffectHandler getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            OverlayText.sheduleRender(overlayTexts, event.getResolution(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public void onClTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;

        tick();
    }

    public OverlayText overlayText(String message, int tickTimeout, OverlayText.OverlayTextProperties properties) {
        OverlayText text;
        if(properties == null) {
            text = new OverlayText(message, tickTimeout);
        } else {
            text = new OverlayText(message, tickTimeout, properties);
        }
        registerTextOverlay(text);
        return text;
    }

    public void registerTextOverlay(final OverlayText text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    overlayTexts.add(text);
                }
            }
        }).start();
    }

    public void unregisterTextOverlay(final OverlayText text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    overlayTexts.remove(text);
                }
            }
        }).start();
    }

    public void tick() {
        OverlayText.tickTexts(overlayTexts);
    }

}
