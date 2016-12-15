package hellfirepvp.astralsorcery.common.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientKeyboardInputEvent
 * Created by HellFirePvP
 * Date: 14.12.2016 / 00:40
 */
@Cancelable
public class ClientKeyboardInputEvent extends Event {

    private static final Method runKeyboardTick = ReflectionHelper.findMethod(Minecraft.class, Minecraft.getMinecraft(),
            new String[]{"runTickKeyboard", "func_184118_az"});

    public static void fireKeyboardEvent(Minecraft calling) {
        ClientKeyboardInputEvent ev = new ClientKeyboardInputEvent();
        MinecraftForge.EVENT_BUS.post(ev);
        if(!ev.isCanceled()) {
            try {
                runKeyboardTick.invoke(Minecraft.getMinecraft());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static {
        runKeyboardTick.setAccessible(true);
    }

}
