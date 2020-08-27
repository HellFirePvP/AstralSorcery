function initializeCoreMod() {
    return {
        'set_player_field': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.LivingEntity',
                'methodName': '<init>',
                'methodDesc': '(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'set_player_field\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var putAttributeMgr = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, 0);

                var attributeMgrField = ASMAPI.mapField('field_110155_d');

                while (putAttributeMgr !== null) {
                    if (putAttributeMgr.name == attributeMgrField) {
                        var prev = putAttributeMgr.getPrevious();

                        method.instructions.insert(prev, ASMAPI.buildMethodCall(
                            'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                            'markPlayer',
                            '(Lnet/minecraft/entity/ai/attributes/AttributeModifierManager;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/ai/attributes/AttributeModifierManager;',
                            ASMAPI.MethodType.STATIC));
                        method.instructions.insert(prev, new VarInsnNode(Opcodes.ALOAD, 0));

                        putAttributeMgr = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, method.instructions.indexOf(putAttributeMgr) + 3);
                    } else {
                        putAttributeMgr = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, method.instructions.indexOf(putAttributeMgr) + 1);
                    }
                }

                ASMAPI.log('INFO', 'Added \'set_player_field\' ASM patch!');
                return method;
            }
        }
    }
}