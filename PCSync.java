import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class PCSync {
  private static final int BUFFER_SIZE = 4;
  private static ArrayList<String> buffer = new ArrayList<>();
  private static boolean isProducerFinished = false;
  private static final ReentrantLock mutex = new ReentrantLock();
  private static final Semaphore empty = new Semaphore(BUFFER_SIZE);
  private static final Semaphore full = new Semaphore(0);

  public static void main(String[] args) {
    Thread producer = new Thread(new Producer());
    Thread consumer1 = new Thread(new Consumer("Consumer-1"));
    Thread consumer2 = new Thread(new Consumer("Consumer-2"));

    producer.start();
    consumer1.start();
    consumer2.start();

    try {
      producer.join();
      consumer1.join();
      consumer2.join();
    } catch (InterruptedException e) {
      System.out.println("ERROR 💀: " + e.getMessage());
    }
  }

  static class Producer implements Runnable {
    @Override
    public void run() {
      try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.contains(")")) {
            line = line.substring(line.indexOf(")") + 1);
          }
          empty.acquire();
          mutex.lock();
          try {
            buffer.add(line);
            System.out.println("Producer: added '" + line + "' to buffer");
          } finally {
            mutex.unlock();
          }
          full.release(); 
        }
        mutex.lock();
        try {
          isProducerFinished = true;
          System.out.println("Producer: EOF 🦭");
          full.release(2); 
        } finally {
          mutex.unlock();
        }
      } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
      } catch (InterruptedException e) {
        System.out.println("Producer interrupted: " + e.getMessage());
      }
    }
  }

  static class Consumer implements Runnable {
    private final String name;

    public Consumer(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      while (true) {
        try {
          full.acquire(); 
          mutex.lock();
          try {
            if (buffer.isEmpty() && isProducerFinished) {
              System.out.println(name + ": DONE! 😎");
              return; 
            }
            if (!buffer.isEmpty()) {
              String data = buffer.get(0);
              System.out.print(name + " read: ");
              for (char c : data.toCharArray()) {
                System.out.print(c + " "); 
                Thread.sleep(10);
              }
              System.out.println();
              buffer.remove(0); 
              empty.release(); 
            }
          } finally {
            mutex.unlock();
          }
        } catch (InterruptedException e) {
          System.out.println(name + " interrupted: " + e.getMessage());
        }
      }
    }
  }
}