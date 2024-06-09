package phanastrae.mirthlight_encore.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlEntityAttachment;
import phanastrae.mirthlight_encore.util.RegionPos;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MirthlightCommand {
    private static final DynamicCommandExceptionType FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthlight_encore.dreamtwirl.join.failed.target_not_in_dreamtwirl", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthlight_encore.dreamtwirl.join.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Text.stringifiedTranslatable("commands.mirthlight_encore.dreamtwirl.join.failed.multiple", playerCount)
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthlight_encore.dreamtwirl.leave.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Text.stringifiedTranslatable("commands.mirthlight_encore.dreamtwirl.leave.failed.multiple", playerCount)
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("mirthlight")
                        .then(literal("dreamtwirl")
                                .then(literal("join")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .then(argument("regionX", IntegerArgumentType.integer())
                                                .then(argument("regionZ", IntegerArgumentType.integer())
                                                        .executes(context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                                        .then(
                                                                argument("targets", EntityArgumentType.entities())
                                                                        .executes(context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), EntityArgumentType.getEntities(context, "targets")))
                                                        )
                                                )
                                        )
                                        .then(literal("player")
                                                .then(argument("targetPlayer", EntityArgumentType.player())
                                                        .executes(context -> joinPlayer(context.getSource(), EntityArgumentType.getPlayer(context, "targetPlayer"), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                                        .then(
                                                                argument("targets", EntityArgumentType.entities())
                                                                        .executes(context -> joinPlayer(context.getSource(), EntityArgumentType.getPlayer(context, "targetPlayer"), EntityArgumentType.getEntities(context, "targets")))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("leave")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(context -> leave(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                        .then(
                                                argument("targets", EntityArgumentType.entities())
                                                        .executes(context -> leave(context.getSource(), EntityArgumentType.getEntities(context, "targets")))
                                        )
                                )
                        )
        );
    }

    private static int joinPlayer(ServerCommandSource source, Entity entity, Collection<? extends Entity> targets) throws CommandSyntaxException {
        DreamtwirlEntityAttachment DTEA = DreamtwirlEntityAttachment.fromEntity(entity);
        RegionPos regionPos = DTEA.getDreamtwirlRegion();
        if(regionPos == null) {
            throw FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION.create(entity.getName());
        } else {
            return join(source, regionPos.regionX, regionPos.regionZ, targets);
        }
    }

    private static int join(ServerCommandSource source, int regionX, int regionZ, Collection<? extends Entity> targets) throws CommandSyntaxException {
        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);

        int successCount = 0;
        for(Entity entity : targets) {
            if(DreamtwirlEntityAttachment.joinDreamtwirl(entity, dreamtwirlRegionPos)) {
                successCount++;
                source.sendFeedback(() -> Text.translatable("commands.mirthlight_encore.dreamtwirl.join.success", entity.getDisplayName(), regionX, regionZ), true);
            }
        }

        if(successCount == 0) {
            if(targets.size() == 1) {
                throw FAILED_JOIN_SINGLE_EXCEPTION.create((targets.iterator().next()).getName());
            } else {
                throw FAILED_JOIN_MULTIPLE_EXCEPTION.create(targets.size());
            }
        }

        return successCount;
    }

    private static int leave(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int successCount = 0;
        for(Entity entity : targets) {
            if(DreamtwirlEntityAttachment.leaveDreamtwirl(entity)) {
                successCount++;
                source.sendFeedback(() -> Text.translatable("commands.mirthlight_encore.dreamtwirl.leave.success", entity.getDisplayName()), true);
            }
        }

        if(successCount == 0) {
            if(targets.size() == 1) {
                throw FAILED_LEAVE_SINGLE_EXCEPTION.create((targets.iterator().next()).getName());
            } else {
                throw FAILED_LEAVE_MULTIPLE_EXCEPTION.create(targets.size());
            }
        }

        return successCount;
    }
}
