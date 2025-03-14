package phanastrae.mirthdew_encore.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntitySleepData {
    private static final long DEEP_SLEEP_TIME = 600;
    private static final long DREAM_COOLDOWN = 6000;
    private static final long ATTEMPT_DREAM_COOLDOWN = 100;
    private static final int DREAM_RECIPROCAL_CHANCE = 8;

    private final LivingEntity entity;

    private long sleepStartTime = -1L;
    private long lastDreamTime = -1L;

    public EntitySleepData(LivingEntity entity) {
        this.entity = entity;
    }

    public void writeNbt(CompoundTag nbt) {
        if(this.sleepStartTime >= 0) {
            nbt.putLong("SleepStartTime", this.sleepStartTime);
        }
        if(this.lastDreamTime >= 0) {
            nbt.putLong("LastDreamTime", this.lastDreamTime);
        }
    }

    public void readNbt(CompoundTag nbt) {
        if(nbt.contains("SleepStartTime", Tag.TAG_LONG)) {
            this.sleepStartTime = nbt.getLong("SleepStartTime");
        }
        if(nbt.contains("LastDreamTime", Tag.TAG_LONG)) {
            this.lastDreamTime = nbt.getLong("LastDreamTime");
        }
    }

    public void tick() {
        Level world = this.entity.level();
        if (!world.isClientSide()) {
            boolean sleeping = entity.isSleeping();
            if(sleeping) {
                if(this.sleepStartTime < 0) {
                    this.startSleeping();
                }

                long sleepTime = world.getDayTime() - this.sleepStartTime;
                if(sleepTime % ATTEMPT_DREAM_COOLDOWN == 0) {
                    if(this.entity.getRandom().nextInt(DREAM_RECIPROCAL_CHANCE) == 0) {
                        if(this.canEmitDreamspecks()) {
                            this.emitDreamspecks();
                        }
                    }
                }
            } else {
                if(this.sleepStartTime >= 0) {
                    this.stopSleeping();
                }
            }
        }
    }

    public void startSleeping() {
        this.sleepStartTime = this.entity.level().getDayTime();
    }

    public void stopSleeping() {
        if(this.canEmitDreamspecks()) {
            this.emitDreamspecks();
        }
        this.sleepStartTime = -1L;
    }

    public boolean canEmitDreamspecks() {
        return this.isDeepSleeping() && this.readyToDream();
    }

    public boolean isDeepSleeping() {
        if(this.sleepStartTime < 0) return false;

        long timeSlept = this.entity.level().getDayTime() - this.sleepStartTime;

        return timeSlept >= DEEP_SLEEP_TIME;
    }

    public boolean readyToDream() {
        if(this.lastDreamTime < 0) return true;

        long timeSinceLastDream = this.entity.level().getGameTime() - this.lastDreamTime;

        return timeSinceLastDream >= DREAM_COOLDOWN;
    }

    public void emitDreamspecks() {
        Level world = this.entity.level();
        this.lastDreamTime = world.getGameTime();

        DreamspeckEntity dreamspeckEntity = MirthdewEncoreEntityTypes.DREAMSPECK.create(world);
        if(dreamspeckEntity != null) {
            dreamspeckEntity.setPos(this.entity.getEyePosition());
            dreamspeckEntity.absRotateTo(this.entity.getYRot(), this.entity.getXRot());
            world.addFreshEntity(dreamspeckEntity);
        }
    }
}
