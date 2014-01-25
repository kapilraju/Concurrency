package example1;

/**
 * Created with IntelliJ IDEA.
 * User: kapilr
 * Date: 1/25/14
 * Time: 8:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProducerConsumer {

    public static void main(String[] args) {

        int poolSize = 3;
        Pool<Integer> integerPool = new Pool<Integer>(poolSize);

        Thread p = new Thread(new Producer(integerPool), "Producer");

        Thread c = new Thread(new Consumer(integerPool), "Consumer");

        p.start();
        c.start();

    }

    /**
     * Shared object
     *
     * @param <Item>
     */
    private static class Pool<Item> {

        private Item[] arr;
        private int counter;
        private int poolSize;

        private Pool(int size) {
            this.poolSize = size;
            this.arr = (Item[])new Object[size];
            counter = -1;
        }

        private boolean isFull() {
            return counter >= poolSize-1;
        }

        private synchronized void add(Item item) {
            while(isFull()) {
                try {
                    System.out.println(Thread.currentThread().getName() + " going to wait");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            arr[++counter] = item;
            notifyAll();
        }

        private synchronized Item remove() {
            while (counter < 0){
                try {
                    System.out.println(Thread.currentThread().getName() + " going to wait");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Item item =  arr[counter--];
            notifyAll();
            return item;
        }

    }

    /**
     * Producer thread
     */
    private static class Producer implements Runnable {

        private Pool<Integer> integerPool;

        private Producer(Pool<Integer> integerPool) {
            this.integerPool = integerPool;
        }

        public void run() {

            for (int i=0; true; i++) {
                integerPool.add(i);
                System.out.println(Thread.currentThread().getName()+" added Item "+i);

                try {Thread.sleep(getRandom());} catch (InterruptedException e) {}


            }
        }
    }

    /**
     * Consumer thread
     */
    private static class Consumer implements Runnable {

        private Pool<Integer> integerPool;

        private Consumer(Pool<Integer> integerPool) {
            this.integerPool = integerPool;
        }

        public void run() {

            for (int i=0; true; i++) {
                Integer val =integerPool.remove();
                System.out.println(Thread.currentThread().getName()+" consumed Item "+val);
                try {Thread.sleep(getRandom());} catch (InterruptedException e) {}
            }
        }
    }

    private static int getRandom() {
        return (int)(Math.random() * 2000);
    }


}
