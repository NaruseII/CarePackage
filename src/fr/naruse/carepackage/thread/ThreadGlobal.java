package fr.naruse.carepackage.thread;

import com.google.common.collect.Sets;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.Set;
import java.util.concurrent.*;

public class ThreadGlobal {

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private static boolean isStopping = false;

    public static void launch() {
        ScheduledFuture future = EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            Set<Runnable> set = null;

            if(!CollectionManager.SECOND_THREAD_RUNNABLE_SET.isEmpty()){
                set = Sets.newHashSet(CollectionManager.SECOND_THREAD_RUNNABLE_SET);
                CollectionManager.SECOND_THREAD_RUNNABLE_SET.clear();
            }

            if(set != null){
                set.forEach(Runnable::run);
            }

        }, 50, 50, TimeUnit.MILLISECONDS);

        EXECUTOR.submit(() -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                if(!(e.getCause() instanceof IllegalPluginAccessException) && !(e.getCause() instanceof RejectedExecutionException)){
                    e.printStackTrace();
                }
                if(!isStopping){
                    launch();
                }
            }
        });
    }

    public static ScheduledExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    public static void shutdown() {
        isStopping = true;
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR.shutdown();
    }

}
