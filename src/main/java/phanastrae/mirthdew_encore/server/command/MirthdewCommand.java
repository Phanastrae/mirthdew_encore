package phanastrae.mirthdew_encore.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Collection;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MirthdewCommand {
    private static final SimpleCommandExceptionType FAILED_NO_MANAGER_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.failed.no_manager")
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.join.failed.target_not_in_dreamtwirl", playerName)
    );
    private static final SimpleCommandExceptionType FAILED_DOES_NOT_EXIST_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.failed.does_not_exist")
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.join.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.join.failed.multiple", playerCount)
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.create.failed.already_exists")
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_NO_CANDIDATE_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.create.failed.no_candidate")
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.create.failed.invalid_position")
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.leave.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Text.stringifiedTranslatable("commands.mirthdew_encore.dreamtwirl.leave.failed.multiple", playerCount)
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("mirthdew")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("dreamtwirl")
                                .then(literal("join")
                                        .then(literal("region")
                                                .then(argument("regionX", IntegerArgumentType.integer())
                                                        .then(argument("regionZ", IntegerArgumentType.integer())
                                                                .executes(context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                                                .then(
                                                                        argument("targets", EntityArgumentType.entities())
                                                                                .executes(
                                                                                        context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), EntityArgumentType.getEntities(context, "targets"))
                                                                                )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("player")
                                                .then(argument("targetPlayer", EntityArgumentType.player())
                                                        .executes(context -> joinPlayer(context.getSource(), EntityArgumentType.getPlayer(context, "targetPlayer"), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                                        .then(
                                                                argument("targets", EntityArgumentType.entities())
                                                                        .executes(
                                                                                context -> joinPlayer(context.getSource(), EntityArgumentType.getPlayer(context, "targetPlayer"), EntityArgumentType.getEntities(context, "targets"))
                                                                        )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("leave")
                                        .executes(context -> leave(context.getSource(), ImmutableList.of(context.getSource().getEntityOrThrow())))
                                        .then(
                                                argument("targets", EntityArgumentType.entities())
                                                        .executes(
                                                                context -> leave(context.getSource(), EntityArgumentType.getEntities(context, "targets"))
                                                        )
                                        )
                                )
                                .then(literal("create")
                                        .executes(context -> create(context.getSource()))
                                        .then(argument("regionX", IntegerArgumentType.integer())
                                                .then(argument("regionZ", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> create(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("list")
                                        .executes(context -> list(context.getSource()))
                                )
                                .then(literal("edit")
                                        .then(argument("regionX", IntegerArgumentType.integer())
                                                .then(argument("regionZ", IntegerArgumentType.integer())
                                                        .then(literal("generate")
                                                            .executes(
                                                                    context -> generate(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"))
                                                            )
                                                        )
                                                        .then(literal("clearAllChunks")
                                                                .requires(source -> source.hasPermissionLevel(4))
                                                                .then(literal("CONFIRM")
                                                                        .executes(context -> clear(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ")))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int create(ServerCommandSource source) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }

        Optional<DreamtwirlStage> stageOptional = dreamtwirlStageManager.createNewStage();
        if(stageOptional.isPresent()) {
            RegionPos regionPos = stageOptional.get().getRegionPos();
            source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.create.success", regionPos.regionX, regionPos.regionZ), true);
            return 1;
        } else {
            throw FAILED_CREATE_NO_CANDIDATE_EXCEPTION.create();
        }
    }

    private static int create(ServerCommandSource source, int regionX, int regionZ) throws CommandSyntaxException {
        if((regionX & 0x1) != 0 || (regionZ & 0x1) != 0) {
            throw FAILED_CREATE_INVALID_POSITION_EXCEPTION.create();
        }

        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }

        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage != null) {
            throw FAILED_CREATE_ALREADY_EXISTS_EXCEPTION.create();
        } else {
            dreamtwirlStageManager.getOrCreateDreamtwirlStage(dreamtwirlRegionPos);
            source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.create.success", regionX, regionZ), true);
            return 1;
        }
    }

    private static int list(ServerCommandSource source) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }

        int count = dreamtwirlStageManager.getDreamtwirlStageCount();

        if(count > 0) {
            source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.list.count", count), false);
            dreamtwirlStageManager.forEach((id, dreamtwirlStage) -> {
                RegionPos regionPos = new RegionPos(id);
                source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.list.entry", regionPos.regionX, regionPos.regionZ), false);
            });
        } else {
            source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.list.none_exist"), false);
        }

        return count;
    }

    private static int generate(ServerCommandSource source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }
        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage == null) {
            throw FAILED_DOES_NOT_EXIST_EXCEPTION.create();
        } else {
            long time = System.nanoTime();

            dreamtwirlStage.generate(source.getWorld());

            long time2 = System.nanoTime();
            long dif = time2 - time;
            long ms = dif / 1000000;
            MirthdewEncore.LOGGER.info("Generated in {}ms", ms);

            source.sendFeedback(() -> Text.literal("Generated Dreamtwirl"), true);
            return 1;
        }
    }

    private static int clear(ServerCommandSource source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }
        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage == null) {
            throw FAILED_DOES_NOT_EXIST_EXCEPTION.create();
        } else {
            long time = System.nanoTime();

            try {
                dreamtwirlStage.clear(source.getWorld());
            } catch (Exception e) {
                MirthdewEncore.LOGGER.info(e.getMessage());
            }

            long time2 = System.nanoTime();
            long dif = time2 - time;
            long ms = dif / 1000000;
            MirthdewEncore.LOGGER.info("Cleared in {}ms", ms);

            source.sendFeedback(() -> Text.literal("Cleared Dreamtwirl"), true);
            return 1;
        }
    }

    private static int joinPlayer(ServerCommandSource source, Entity entity, Collection<? extends Entity> targets) throws CommandSyntaxException {
        MirthdewEncoreEntityAttachment MEA = MirthdewEncoreEntityAttachment.fromEntity(entity);
        RegionPos regionPos = MEA.getDreamtwirlEntityData().getDreamtwirlRegion();
        if(regionPos == null) {
            throw FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION.create(entity.getName());
        } else {
            return join(source, regionPos.regionX, regionPos.regionZ, targets);
        }
    }

    private static int join(ServerCommandSource source, int regionX, int regionZ, Collection<? extends Entity> targets) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }

        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage == null) {
            throw FAILED_DOES_NOT_EXIST_EXCEPTION.create();
        }

        int successCount = 0;
        for(Entity entity : targets) {
            if(EntityDreamtwirlData.joinDreamtwirl(entity, dreamtwirlRegionPos)) {
                successCount++;
                source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.join.success", entity.getDisplayName(), regionX, regionZ), true);
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
            if(EntityDreamtwirlData.leaveDreamtwirl(entity)) {
                successCount++;
                source.sendFeedback(() -> Text.translatable("commands.mirthdew_encore.dreamtwirl.leave.success", entity.getDisplayName()), true);
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
