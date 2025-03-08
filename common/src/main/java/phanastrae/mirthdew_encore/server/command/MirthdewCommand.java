package phanastrae.mirthdew_encore.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.BasicStageData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.destroy.StageNuker;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceableRoom;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MirthdewCommand {
    private static final SimpleCommandExceptionType FAILED_NO_MANAGER_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.failed.no_manager")
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.join.failed.target_not_in_dreamtwirl", playerName)
    );
    private static final SimpleCommandExceptionType FAILED_DOES_NOT_EXIST_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.failed.does_not_exist")
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.join.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_JOIN_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.join.failed.multiple", playerCount)
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.create.failed.already_exists")
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_NO_CANDIDATE_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.create.failed.no_candidate")
    );
    private static final SimpleCommandExceptionType FAILED_CREATE_INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.create.failed.invalid_position")
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_SINGLE_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.leave.failed.single", playerName)
    );
    private static final DynamicCommandExceptionType FAILED_LEAVE_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType(
            playerCount -> Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.leave.failed.multiple", playerCount)
    );
    private static final SimpleCommandExceptionType FAILED_SDG_ALREADY_CLEAR = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.edit.clear.sdg.failed.already_cleared")
    );
    private static final SimpleCommandExceptionType FAILED_ROOM_STORAGE_ALREADY_CLEAR = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.edit.clear.rooms.failed.already_cleared")
    );
    private static final SimpleCommandExceptionType FAILED_ACHERUNES_ALREADY_CLEAR = new SimpleCommandExceptionType(
            Component.translatableEscape("commands.mirthdew_encore.dreamtwirl.edit.clear.acherunes.failed.already_cleared")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("mirthdew")
                        .requires(source -> source.hasPermission(2))
                        .then(literal("dreamtwirl")
                                .then(literal("join")
                                        .then(literal("region")
                                                .then(argument("regionX", IntegerArgumentType.integer())
                                                        .then(argument("regionZ", IntegerArgumentType.integer())
                                                                .executes(context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), ImmutableList.of(context.getSource().getEntityOrException())))
                                                                .then(
                                                                        argument("targets", EntityArgument.entities())
                                                                                .executes(
                                                                                        context -> join(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"), EntityArgument.getEntities(context, "targets"))
                                                                                )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(literal("player")
                                                .then(argument("targetPlayer", EntityArgument.player())
                                                        .executes(context -> joinPlayer(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), ImmutableList.of(context.getSource().getEntityOrException())))
                                                        .then(
                                                                argument("targets", EntityArgument.entities())
                                                                        .executes(
                                                                                context -> joinPlayer(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), EntityArgument.getEntities(context, "targets"))
                                                                        )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("leave")
                                        .executes(context -> leave(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException())))
                                        .then(
                                                argument("targets", EntityArgument.entities())
                                                        .executes(
                                                                context -> leave(context.getSource(), EntityArgument.getEntities(context, "targets"))
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
                                                        .then(literal("clear")
                                                                .then(literal("allChunks")
                                                                        .requires(source -> source.hasPermission(4))
                                                                        .then(literal("CONFIRM")
                                                                                .executes(context -> clearAllChunks(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ")))
                                                                        )
                                                                )
                                                                .then(literal("stageDesignGenerator")
                                                                        .executes(context -> clearSDG(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ")))
                                                                )
                                                                .then(literal("roomStorage")
                                                                        .executes(context -> clearRoomStorage(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ")))
                                                                )
                                                                .then(literal("acherunes")
                                                                        .executes(context -> clearAcherunes(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ")))
                                                                )
                                                        )
                                                        .then(literal("placeAllRooms")
                                                                .executes(
                                                                        context -> placeAllRooms(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("info")
                                        .then(argument("regionX", IntegerArgumentType.integer())
                                                .then(argument("regionZ", IntegerArgumentType.integer())
                                                        .executes(
                                                                context -> info(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"))
                                                        )
                                                        .then(literal("acherunes")
                                                                .executes(
                                                                        context -> acheruneInfo(context.getSource(), IntegerArgumentType.getInteger(context, "regionX"), IntegerArgumentType.getInteger(context, "regionZ"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("mirth")
                                .then(literal("add")
                                        .then(argument("targets", EntityArgument.players())
                                                .then(argument("amount", LongArgumentType.longArg(0))
                                                        .executes(
                                                                context -> addMirth(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "amount"))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("remove")
                                        .then(argument("targets", EntityArgument.players())
                                                .then(argument("amount", LongArgumentType.longArg(0))
                                                        .executes(
                                                                context -> removeMirth(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "amount"))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("set")
                                        .then(argument("targets", EntityArgument.players())
                                                .then(argument("value", LongArgumentType.longArg(0))
                                                        .executes(
                                                                context -> setMirth(context.getSource(), EntityArgument.getPlayers(context, "targets"), LongArgumentType.getLong(context, "value"))
                                                        )
                                                )
                                        )
                                )
                                .then(literal("query")
                                        .then(argument("target", EntityArgument.player())
                                                .executes(
                                                        context -> queryMirth(context.getSource(), EntityArgument.getPlayer(context, "target"))
                                                )
                                        )
                                )
                        )
        );
    }

    private static int create(CommandSourceStack source) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);

        Optional<DreamtwirlStage> stageOptional = dreamtwirlStageManager.createNewStage();
        if(stageOptional.isPresent()) {
            RegionPos regionPos = stageOptional.get().getRegionPos();
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.create.success", regionPos.regionX, regionPos.regionZ), true);
            return 1;
        } else {
            throw FAILED_CREATE_NO_CANDIDATE_EXCEPTION.create();
        }
    }

    private static int create(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        if((regionX & 0x1) != 0 || (regionZ & 0x1) != 0) {
            throw FAILED_CREATE_INVALID_POSITION_EXCEPTION.create();
        }

        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);

        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage != null) {
            throw FAILED_CREATE_ALREADY_EXISTS_EXCEPTION.create();
        } else {
            dreamtwirlStageManager.getOrCreateDreamtwirlStage(dreamtwirlRegionPos);
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.create.success", regionX, regionZ), true);
            return 1;
        }
    }

    private static int list(CommandSourceStack source) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);

        int count = dreamtwirlStageManager.getDreamtwirlStageCount();

        if(count > 0) {
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.list.count", count), false);

            long levelTime = source.getLevel().getGameTime();

            for(BasicStageData BSD : dreamtwirlStageManager.getBasicStageDatas().values().stream().sorted(Comparator.comparingLong(BasicStageData::getTimestamp)).toList()) {
                RegionPos regionPos = BSD.getRegionPos();

                Component dreamtwirlComponent = getDreamtwirlComponent();
                Component regionXComponent = getRegionXComponent(regionPos.regionX);
                Component regionZComponent = getRegionZComponent(regionPos.regionZ);
                Component ageComponent = BSD.getAgeTextComponentFromLevelTime(levelTime).copy().withStyle(ChatFormatting.YELLOW);

                source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.list.entry", dreamtwirlComponent, regionXComponent, regionZComponent, ageComponent), false);
            }
        } else {
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.list.none_exist"), false);
        }

        return count;
    }

    private static int generate(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        // TODO add a seed parameter to the command
        //long dreamtwirlSeed = source.getLevel().getSeed() ^ regionPos.id;
        long dreamtwirlSeed = source.getLevel().getRandom().nextLong();
        dreamtwirlStage.generate(dreamtwirlSeed, source.getLevel());

        source.sendSuccess(() -> Component.literal("Begun Dreamtwirl Generation"), true); // TODO translations
        return 1;
    }

    private static int clearAllChunks(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        long time = System.nanoTime();

        try {
            StageNuker.clear(source.getLevel(), dreamtwirlStage);
        } catch (Exception e) {
            MirthdewEncore.LOGGER.info(e.getMessage());
        }

        long time2 = System.nanoTime();
        long dif = time2 - time;
        long ms = dif / 1000000;
        MirthdewEncore.LOGGER.info("Cleared in {}ms", ms);

        source.sendSuccess(() -> Component.literal("Cleared Dreamtwirl"), true);
        return 1;
    }

    private static int clearSDG(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        if(dreamtwirlStage.clearDesignGenerator()) {
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.edit.clear.sdg.success", getDreamtwirlComponent(), getRegionXComponent(regionX), getRegionZComponent(regionZ)), true);
            return 1;
        } else {
            throw FAILED_SDG_ALREADY_CLEAR.create();
        }
    }

    private static int clearRoomStorage(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        if(dreamtwirlStage.clearRoomStorage()) {
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.edit.clear.rooms.success", getDreamtwirlComponent(), getRegionXComponent(regionX), getRegionZComponent(regionZ)), true);
            return 1;
        } else {
            throw FAILED_ROOM_STORAGE_ALREADY_CLEAR.create();
        }
    }

    private static int clearAcherunes(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        if(dreamtwirlStage.clearAcherunes()) {
            source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.edit.clear.acherunes.success", getDreamtwirlComponent(), getRegionXComponent(regionX), getRegionZComponent(regionZ)), true);
            return 1;
        } else {
            throw FAILED_ACHERUNES_ALREADY_CLEAR.create();
        }
    }

    private static int placeAllRooms(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        dreamtwirlStage.beginPlacingAllRooms();

        Component dreamtwirlComponent = getDreamtwirlComponent();
        Component regionXComponent = getRegionXComponent(regionX);
        Component regionZComponent = getRegionZComponent(regionZ);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.edit.place_all_rooms", dreamtwirlComponent, regionXComponent, regionZComponent), true);
        return 1;
    }

    private static int info(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        Component dreamtwirlComponent = getDreamtwirlComponent();
        Component regionXComponent = getRegionXComponent(regionX);
        Component regionZComponent = getRegionZComponent(regionZ);

        Component ageComponent = dreamtwirlStage.getAgeTextComponent().copy().withStyle(ChatFormatting.YELLOW);

        List<PlaceableRoom> rooms = dreamtwirlStage.getRoomStorage().getRooms();

        int roomCount = rooms.size();
        Component roomCountComponent = Component.literal(String.valueOf(roomCount)).withStyle(ChatFormatting.AQUA);

        int placedRooms = (int)rooms.stream().filter(PlaceableRoom::isRoomPlaced).count();
        Component placedRoomsComponent = Component.literal(String.valueOf(placedRooms)).withStyle(ChatFormatting.GOLD);

        int placedPercentage = (int)Math.floor(roomCount == 0 ? 100 : (100F * placedRooms) / roomCount);

        int acheruneCount = dreamtwirlStage.getStageAcherunes().getAcheruneCount();
        Component acheruneCountComponent = Component.literal(String.valueOf(acheruneCount)).withStyle(ChatFormatting.LIGHT_PURPLE);

        Component SDGStatus = (dreamtwirlStage.getStageDesignGenerator() == null
                ? Component.translatable("commands.mirthdew_encore.dreamtwirl.info.sdg.status.idle").withStyle(ChatFormatting.DARK_BLUE)
                : Component.translatable("commands.mirthdew_encore.dreamtwirl.info.sdg.status.generating").withStyle(ChatFormatting.GOLD)
        );

        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.summary", dreamtwirlComponent, regionXComponent, regionZComponent), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.time_since_creation", ageComponent), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.rooms.total",roomCountComponent), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.rooms.placed", placedRoomsComponent, placedPercentage), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.acherunes.total", acheruneCountComponent), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.sdg.status", SDGStatus), true);

        return 1;
    }

    private static int acheruneInfo(CommandSourceStack source, int regionX, int regionZ) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        Component dreamtwirlComponent = getDreamtwirlComponent();
        Component regionXComponent = getRegionXComponent(regionX);
        Component regionZComponent = getRegionZComponent(regionZ);

        int acheruneCount = dreamtwirlStage.getStageAcherunes().getAcheruneCount();
        Component acheruneCountComponent = Component.literal(String.valueOf(acheruneCount)).withStyle(ChatFormatting.LIGHT_PURPLE);

        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.acherunes.summary", dreamtwirlComponent, regionXComponent, regionZComponent), true);
        source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.info.acherunes.total", acheruneCountComponent), true);

        for(Acherune acherune : dreamtwirlStage.getStageAcherunes().getAcherunes().stream().sorted(Comparator.comparingLong(ac -> ac.getId().timestamp())).toList()) {
            BlockPos acPos = acherune.getPos();
            BlockPosDimensional linkedPos = acherune.getLinkedPos();

            Component component;
            if(linkedPos == null) {
                component = Component.translatable("commands.mirthdew_encore.dreamtwirl.info.acherunes.single.unlinked", acPos.getX(), acPos.getY(), acPos.getZ());
            } else {
                component = Component.translatable("commands.mirthdew_encore.dreamtwirl.info.acherunes.single.linked", acPos.getX(), acPos.getY(), acPos.getZ(), linkedPos.getPos().getX(), linkedPos.getPos().getY(), linkedPos.getPos().getZ(), linkedPos.dimensionId().toString());
            }

            source.sendSuccess(() -> component, true);
        }

        return 1;
    }

    private static int joinPlayer(CommandSourceStack source, Entity entity, Collection<? extends Entity> targets) throws CommandSyntaxException {
        MirthdewEncoreEntityAttachment MEA = MirthdewEncoreEntityAttachment.fromEntity(entity);
        RegionPos regionPos = MEA.getDreamtwirlEntityData().getDreamtwirlRegion();
        if(regionPos == null) {
            throw FAILED_JOIN_TARGET_NOT_IN_DREAMTWIRL_EXCEPTION.create(entity.getName());
        } else {
            return join(source, regionPos.regionX, regionPos.regionZ, targets);
        }
    }

    private static int join(CommandSourceStack source, int regionX, int regionZ, Collection<? extends Entity> targets) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = getStageManager(source);
        DreamtwirlStage dreamtwirlStage = getStage(dreamtwirlStageManager, regionX, regionZ);

        int successCount = 0;
        for(Entity entity : targets) {
            if(EntityDreamtwirlData.joinDreamtwirl(entity, dreamtwirlStage.getRegionPos())) {
                successCount++;
                source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.join.success", entity.getDisplayName(), regionX, regionZ), true);
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

    private static int leave(CommandSourceStack source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int successCount = 0;
        for(Entity entity : targets) {
            if(EntityDreamtwirlData.leaveDreamtwirl(entity)) {
                successCount++;
                source.sendSuccess(() -> Component.translatable("commands.mirthdew_encore.dreamtwirl.leave.success", entity.getDisplayName()), true);
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

    private static int addMirth(CommandSourceStack source, Collection<? extends Player> targets, long amount) {
        for(Player target : targets) {
            PlayerEntityMirthData pemd = MirthdewEncorePlayerEntityAttachment.fromPlayer(target).getMirthData();

            pemd.addMirth(amount, Long.MAX_VALUE);
        }

        if (targets.size() == 1) {
            source.sendSuccess(
                    () -> Component.translatable(
                            "commands.mirthdew_encore.mirth.add.single", amount, targets.iterator().next().getDisplayName()
                    ),
                    true
            );
        } else {
            source.sendSuccess(
                    () -> Component.translatable("commands.mirthdew_encore.mirth.add.many", amount, targets.size()),
                    true
            );
        }

        return targets.size();
    }

    private static int removeMirth(CommandSourceStack source, Collection<? extends Player> targets, long amount) {
        for(Player target : targets) {
            PlayerEntityMirthData pemd = getMirthData(target);

            pemd.removeMirth(amount);
        }

        if (targets.size() == 1) {
            source.sendSuccess(
                    () -> Component.translatable(
                            "commands.mirthdew_encore.mirth.remove.single", amount, targets.iterator().next().getDisplayName()
                    ),
                    true
            );
        } else {
            source.sendSuccess(
                    () -> Component.translatable("commands.mirthdew_encore.mirth.remove.many", amount, targets.size()),
                    true
            );
        }

        return targets.size();
    }

    private static int setMirth(CommandSourceStack source, Collection<? extends Player> targets, long value) {
        for(Player target : targets) {
            PlayerEntityMirthData pemd = getMirthData(target);

            pemd.setMirth(value);
        }

        if (targets.size() == 1) {
            source.sendSuccess(
                    () -> Component.translatable(
                            "commands.mirthdew_encore.mirth.set.single", value, targets.iterator().next().getDisplayName()
                    ),
                    true
            );
        } else {
            source.sendSuccess(
                    () -> Component.translatable("commands.mirthdew_encore.mirth.set.many", value, targets.size()),
                    true
            );
        }

        return targets.size();
    }

    private static int queryMirth(CommandSourceStack source, Player target) {
        PlayerEntityMirthData pemd = getMirthData(target);
        long mirth = pemd.getMirth();

        source.sendSuccess(
                () -> Component.translatable("commands.mirthdew_encore.mirth.query", target.getDisplayName(), mirth),
                false
        );
        return Math.clamp(mirth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static DreamtwirlStageManager getStageManager(CommandSourceStack source) throws CommandSyntaxException {
        DreamtwirlStageManager dreamtwirlStageManager = DreamtwirlStageManager.getMainDreamtwirlStageManager(source.getServer());
        if(dreamtwirlStageManager == null) {
            throw FAILED_NO_MANAGER_EXCEPTION.create();
        }
        return dreamtwirlStageManager;
    }

    private static DreamtwirlStage getStage(DreamtwirlStageManager dreamtwirlStageManager, int regionX, int regionZ) throws CommandSyntaxException {
        RegionPos dreamtwirlRegionPos = new RegionPos(regionX, regionZ);
        DreamtwirlStage dreamtwirlStage = dreamtwirlStageManager.getDreamtwirlIfPresent(dreamtwirlRegionPos);
        if(dreamtwirlStage == null) {
            throw FAILED_DOES_NOT_EXIST_EXCEPTION.create();
        }

        return dreamtwirlStage;
    }

    private static PlayerEntityMirthData getMirthData(Player player) {
        return MirthdewEncorePlayerEntityAttachment.fromPlayer(player).getMirthData();
    }

    private static Component getDreamtwirlComponent() {
        return Component.translatable("commands.mirthdew_encore.dreamtwirl.info.dreamtwirl").withStyle(ChatFormatting.AQUA);
    }

    private static Component getRegionXComponent(int regionX) {
        return Component.literal(String.valueOf(regionX)).withStyle(ChatFormatting.RED);
    }

    private static Component getRegionZComponent(int regionZ) {
        return Component.literal(String.valueOf(regionZ)).withStyle(ChatFormatting.BLUE);
    }
}
