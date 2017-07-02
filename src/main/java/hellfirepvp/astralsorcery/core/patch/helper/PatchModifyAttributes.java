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
import org.objectweb.asm.tree.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatchModifyAttributes
 * Created by HellFirePvP
 * Date: 04.11.2016 / 13:14
 */
public class PatchModifyAttributes extends ClassPatch {

    public PatchModifyAttributes() {
        super("net.minecraft.item.ItemStack");
    }

    @Override
    public void patch(ClassNode cn) {
        MethodNode mn = getMethod(cn, "getAttributeModifiers", "func_111283_C", "(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lcom/google/common/collect/Multimap;");

        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "hellfirepvp/astralsorcery/common/util/SwordSharpenHelper", "applySharpenModifier", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/inventory/EntityEquipmentSlot;Lcom/google/common/collect/Multimap;)V", false));
        mn.instructions.insert(mn.instructions.getLast().getPrevious().getPrevious(), list);
    }
}
