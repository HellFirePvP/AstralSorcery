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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchCatchCustomPayload
 * Created by HellFirePvP
 * Date: 13.01.2017 / 00:50
 */
public class PatchCatchCustomPayload extends ClassPatch {

    public PatchCatchCustomPayload() { //Move to packet class... ?
        super("net.minecraft.client.network.NetHandlerPlayClient");
    }

    @Override
    public void patch(ClassNode cn) {
        //MethodNode mn = getMethod(cn, "handleCustomPayload", "func_147240_a", "(Lnet/minecraft/network/play/server/SPacketCustomPayload;)V");
        //AbstractInsnNode aisn = findFirstInstruction(mn, Opcodes.ALOAD); //Skip the first few setup label things, however still be before everything.
        MethodNode mn = getMethod(cn, "handleCustomPayload", "func_147240_a", "(Lnet/minecraft/network/play/server/SPacketCustomPayload;)V");
        AbstractInsnNode asisn = findFirstInstruction(mn, Opcodes.INVOKESTATIC).getNext();
        mn.instructions.insertBefore(asisn, new VarInsnNode(Opcodes.ALOAD, 1));
        mn.instructions.insertBefore(asisn, new MethodInsnNode(Opcodes.INVOKESTATIC, "hellfirepvp/astralsorcery/common/event/listener/EventHandlerNetwork", "clientCatchWorldHandlerPayload", "(Lnet/minecraft/network/play/server/SPacketCustomPayload;)V", false));
        //mn.instructions.insert(asisn, new MethodInsnNode(Opcodes.INVOKESTATIC, "hellfirepvp/astralsorcery/common/event/listener/EventHandlerNetwork", "clientCatchWorldHandlerPayload", "(Lnet/minecraft/network/play/server/SPacketCustomPayload;)V", false));
    }

}
