function initializeCoreMod() {
    return {
        'add_custom_voxel_collision': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.util.math.shapes.VoxelShapeSpliterator',
                'methodName': 'tryAdvance',
                'methodDesc': '(Ljava/util/function/Consumer;)Z'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_custom_voxel_collision\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

                var first = method.instructions.getFirst();
                var firstLabel = findFirstLabel(method);
                method.instructions.insertBefore(first, new LabelNode());
                method.instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(first, new FieldInsnNode(Opcodes.GETFIELD,
                    "net/minecraft/util/math/shapes/VoxelShapeSpliterator", "as_didCustomCollision", "Z"))
                method.instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insertBefore(first, new VarInsnNode(Opcodes.ALOAD, 1));
                method.instructions.insertBefore(first, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'addCustomCollision',
                    '(ZLnet/minecraft/util/math/shapes/VoxelShapeSpliterator;Ljava/util/function/Consumer;)Z',
                    ASMAPI.MethodType.STATIC));
                method.instructions.insertBefore(first, new JumpInsnNode(Opcodes.IFEQ, firstLabel));
                method.instructions.insertBefore(first, new InsnNode(Opcodes.ICONST_1));
                method.instructions.insertBefore(first, new InsnNode(Opcodes.IRETURN));

                ASMAPI.log('INFO', 'Added \'add_custom_voxel_collision\' ASM patch!');
                return method;
            }
        }
    }
}

function findFirstLabel(method) {
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

    for (i = 0; i < method.instructions.size(); i++) {
        node = method.instructions.get(i);
        if (node instanceof LabelNode) {
            return node;
        }
    }
    return null;
}
