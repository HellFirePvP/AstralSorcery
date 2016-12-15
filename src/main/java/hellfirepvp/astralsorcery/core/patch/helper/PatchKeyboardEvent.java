package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
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
