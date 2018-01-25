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
 * Class: PatchKnockbackEvent
 * Created by HellFirePvP
 * Date: 02.12.2016 / 19:02
 */
public class PatchKnockbackEvent extends ClassPatch {

    public PatchKnockbackEvent() {
        super("net.minecraft.entity.EntityLivingBase");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "knockBack", "func_70653_a", "(Lnet/minecraft/entity/Entity;FDD)V");
        AbstractInsnNode n = mn.instructions.getFirst();
        mn.instructions.insertBefore(n, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
        mn.instructions.insertBefore(n, new TypeInsnNode(Opcodes.NEW, "hellfirepvp/astralsorcery/common/event/EntityKnockbackEvent"));
        mn.instructions.insertBefore(n, new InsnNode(Opcodes.DUP));
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 0)); //thisEntity
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.ALOAD, 1)); //attackingEntity
        mn.instructions.insertBefore(n, new VarInsnNode(Opcodes.FLOAD, 2)); //str
        mn.instructions.insertBefore(n, new MethodInsnNode(Opcodes.INVOKESPECIAL, "hellfirepvp/astralsorcery/common/event/EntityKnockbackEvent", "<init>", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;F)V", false));
        mn.instructions.insertBefore(n, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
        mn.instructions.insertBefore(n, new InsnNode(Opcodes.POP));
    }

}
