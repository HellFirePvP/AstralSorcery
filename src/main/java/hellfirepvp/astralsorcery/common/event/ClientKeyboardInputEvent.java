/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

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

    private static final Method runKeyboardTick;

    public static void fireKeyboardEvent(Minecraft calling) {
        ClientKeyboardInputEvent ev = new ClientKeyboardInputEvent();
        MinecraftForge.EVENT_BUS.post(ev);
        if(!ev.isCanceled()) {
            try {
                runKeyboardTick.invoke(calling);
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getMessage() != null && e.getMessage().equals("Manually triggered debug crash")) {
                    throw new RuntimeException(e); //WELL....
                }
            }
        }
    }

    static {
        Method buf = null;
        try {
            buf = ReflectionHelper.findMethod(Minecraft.class, Minecraft.getMinecraft(), new String[] { "runTickKeyboard", "func_184118_az" });
        } catch (Exception exc) {
            buf = null;
        } finally {
            runKeyboardTick = buf;
        }

        if(runKeyboardTick == null) {
            throw new IllegalStateException("Could not find method for bridging keyboard input.\n" +
                    "This is a severe problem and you would not be able to play properly, even if it'd launch normally.\n" +
                    "This might be caused because you tried to run AstralSorcery on a minecraft version it is not supported for.\n" +
                    "If you think that you're playing a version it is supported for, please report that this happened.");
        }
        runKeyboardTick.setAccessible(true);
    }

}
