

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public final class ConcurrentCacheHashMap<K, V>{

    private final Map<K, V> map = new HashMap<K,V>();

    private final Semaphore readCountMutex = new Semaphore(1);
    private final Semaphore writeCountMutex = new Semaphore(1);
    private final Semaphore readTryMutex = new Semaphore(1);
    private final Semaphore criticalSectionMutex = new Semaphore(1);
    private final Semaphore preventReaderStarvationMutex = new Semaphore(1);
    private int readcount = 0;
    private int writecount = 0;

    public V get(K key) {

        V value = null;
        try {
            enterReader();
            value = map.get(key);
            escapeReader();
        } catch (InterruptedException e) {
            System.err.println("thread error:" + e.getMessage());
        }
        return value;

    }

    public void put(K key, V value) {

        try {
            enterWriter();
            map.put(key, value);
            escapeWriter();
        } catch (InterruptedException e) {
            System.err.println("thread error: " + e.getMessage());
        }

    }

    private void enterReader() throws InterruptedException {

        readTryMutex.acquire();
        preventReaderStarvationMutex.acquire();
        readCountMutex.acquire();
        readcount++;
        if (readcount == 1) {
            criticalSectionMutex.acquire();
        }
        readCountMutex.release();
        preventReaderStarvationMutex.release();
        readTryMutex.release();

    }

    private void escapeReader() throws InterruptedException {

        readCountMutex.acquire();
        readcount--;
        if (readcount == 0) {
            criticalSectionMutex.release();
        }
        readCountMutex.release();

    }

    private void enterWriter() throws InterruptedException {

        writeCountMutex.acquire();
        writecount++;
        if (writecount == 1) {
            preventReaderStarvationMutex.acquire();
        }
        writeCountMutex.release();
        criticalSectionMutex.acquire();

    }

    private void escapeWriter() throws InterruptedException {

        criticalSectionMutex.release();
        writeCountMutex.acquire();
        writecount--;
        if (writecount == 0) {
            preventReaderStarvationMutex.release();
        }
        writeCountMutex.release();

    }

}
