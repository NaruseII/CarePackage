package fr.naruse.carepackage.thread;

import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Set;

public class CollectionManager {

    public static final AntiConcurrentBufferSet<Runnable> SECOND_THREAD_RUNNABLE_SET = new AntiConcurrentBufferSet();

    public static class AntiConcurrentBufferSet<T> implements Iterable<T>{

        private final Set<T> set = Sets.newHashSet();

        public void add(T key){
            ThreadGlobal.getExecutorService().submit(() -> {
                set.add(key);
            });
        }

        public boolean contains(T key){
            return set.contains(key);
        }

        public void remove(T key){
            ThreadGlobal.getExecutorService().submit(() -> {
                set.remove(key);
            });
        }

        public boolean isEmpty(){
            return set.isEmpty();
        }

        public void clear(){
            set.clear();
        }

        @Override
        public Iterator<T> iterator() {
            return set.iterator();
        }
    }
}
