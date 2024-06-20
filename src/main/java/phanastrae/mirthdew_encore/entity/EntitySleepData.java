package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;

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

    public void writeNbt(NbtCompound nbt) {
        if(this.sleepStartTime >= 0) {
            nbt.putLong("SleepStartTime", this.sleepStartTime);
        }
        if(this.lastDreamTime >= 0) {
            nbt.putLong("LastDreamTime", this.lastDreamTime);
        }
    }

    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("SleepStartTime", NbtElement.LONG_TYPE)) {
            this.sleepStartTime = nbt.getLong("SleepStartTime");
        }
        if(nbt.contains("LastDreamTime", NbtElement.LONG_TYPE)) {
            this.lastDreamTime = nbt.getLong("LastDreamTime");
        }
    }

    public void tick() {
        World world = this.entity.getWorld();
        if (!world.isClient()) {
            boolean sleeping = entity.isSleeping();
            if(sleeping) {
                if(this.sleepStartTime < 0) {
                    this.startSleeping();
                }

                long sleepTime = world.getTimeOfDay() - this.sleepStartTime;
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
        this.sleepStartTime = this.entity.getWorld().getTimeOfDay();
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

        long timeSlept = this.entity.getWorld().getTimeOfDay() - this.sleepStartTime;

        return timeSlept >= DEEP_SLEEP_TIME;
    }

    public boolean readyToDream() {
        if(this.lastDreamTime < 0) return true;

        long timeSinceLastDream = this.entity.getWorld().getTime() - this.lastDreamTime;

        return timeSinceLastDream >= DREAM_COOLDOWN;
    }

    public void emitDreamspecks() {
        World world = this.entity.getWorld();
        this.lastDreamTime = world.getTime();

        DreamspeckEntity dreamspeckEntity = MirthdewEncoreEntityTypes.DREAM_SPECK.create(world);
        if(dreamspeckEntity != null) {
            dreamspeckEntity.setPosition(this.entity.getEyePos());
            dreamspeckEntity.setAngles(this.entity.getYaw(), this.entity.getPitch());
            world.spawnEntity(dreamspeckEntity);
        }
    }
}
