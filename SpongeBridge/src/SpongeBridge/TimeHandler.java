package SpongeBridge;

import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.Sponge;

/**
 * Created by solum on 7/17/2016.
 */
public class TimeHandler {
    private static int counter = 0;
    public static void init() {
        Scheduler scheduler = Sponge.getScheduler();
        Task.Builder taskBuilder = scheduler.createTaskBuilder();
        taskBuilder.execute(new Runnable() {
            public void run() {
                System.out.println("here");
                TimeHandler.tick();
            }
        });
        taskBuilder.intervalTicks(1);
    }

    public static void tick() {
        TimeHandler.counter ++;
    }

    public static int getTick() {
        return TimeHandler.counter;
    }
}
