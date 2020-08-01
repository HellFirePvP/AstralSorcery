function initializeCoreMod() {
    return {
        'cooldown_tracker_set': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.util.CooldownTracker',
                'methodName': 'func_185145_a',
                'methodDesc': '(Lnet/minecraft/item/Item;I)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'cooldown_tracker_set\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');

                var loadMap = ASMAPI.findFirstInstructionAfter(method, Opcodes.ALOAD, 0);

                method.instructions.insert(loadMap, new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.insert(loadMap, new VarInsnNode(Opcodes.ISTORE, 2));
                method.instructions.insert(loadMap, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'fireCooldownEvent',
                    '(Lnet/minecraft/util/CooldownTracker;Lnet/minecraft/item/Item;I)I',
                    ASMAPI.MethodType.STATIC));
                method.instructions.insert(loadMap, new VarInsnNode(Opcodes.ILOAD, 2)); //ticks
                method.instructions.insert(loadMap, new VarInsnNode(Opcodes.ALOAD, 1)); //item
                //'this' is already on the stack here, no need to bloat the stack with a duplicate

                ASMAPI.log('INFO', 'Added \'cooldown_tracker_set\' ASM patch!');
                return method;
            }
        }
    }
}