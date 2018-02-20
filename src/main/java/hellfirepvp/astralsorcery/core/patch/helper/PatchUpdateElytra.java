/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core.patch.helper;

import hellfirepvp.astralsorcery.core.ClassPatch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchUpdateElytra
 * Created by HellFirePvP
 * Date: 15.10.2017 / 22:13
 */
public class PatchUpdateElytra extends ClassPatch {

    public PatchUpdateElytra() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "updateElytra", "func_184616_r", "()V");
        AbstractInsnNode first = mn.instructions.getFirst();
        mn.instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 0)); //thisEntity
        mn.instructions.insertBefore(first, new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "hellfirepvp/astralsorcery/common/event/listener/EventHandlerCapeEffects",
                "updateElytraEventPre",
                "(Lnet/minecraft/entity/EntityLivingBase;)V",
                false));

        InsnList post = new InsnList();
        post.add(new VarInsnNode(Opcodes.ALOAD, 0)); //thisEntity
        post.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "hellfirepvp/astralsorcery/common/event/listener/EventHandlerCapeEffects",
                "updateElytraEventPost",
                "(Lnet/minecraft/entity/EntityLivingBase;)V",
                false));
        mn.instructions.insert(mn.instructions.getLast().getPrevious().getPrevious(), post);
    }

}
