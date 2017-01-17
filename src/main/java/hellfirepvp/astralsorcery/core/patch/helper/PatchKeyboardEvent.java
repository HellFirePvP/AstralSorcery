/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchKeyboardEvent
 * Created by HellFirePvP
 * Date: 14.12.2016 / 00:42
 */
public class PatchKeyboardEvent extends ClassPatch {

    public PatchKeyboardEvent() {
        super("net.minecraft.client.Minecraft");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "runTick", "func_71407_l", "()V");

        MethodInsnNode m = getFirstMethodCall(mn, "net/minecraft/client/Minecraft", "runTickKeyboard", "func_184118_az", "()V");
        m.setOpcode(Opcodes.INVOKESTATIC);
        m.owner = "hellfirepvp/astralsorcery/common/event/ClientKeyboardInputEvent";
        m.name = "fireKeyboardEvent";
        m.desc = "(Lnet/minecraft/client/Minecraft;)V";
    }
}
