import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PCNoSync {
  // Buffer มีขนาดจำกัด 4 บรรทัด
  private static ArrayList<String> buffer = new ArrayList<>();
  private static final int BUFFER_SIZE = 4;

  // บอกว่า producer ทำงานเสร็จแล้วหรือยัง
  private static boolean isProducerFinished = false;

  public static void main(String[] args) {
    // สร้าง thread: 1 producer และ 2 consumer
    Thread producer = new Thread(new Producer());
    Thread consumer1 = new Thread(new Consumer("Consumer-1"));
    Thread consumer2 = new Thread(new Consumer("Consumer-2"));

    // เริ่มการทำงานของ thread ทั้งหมด
    producer.start();
    consumer1.start();
    consumer2.start();

    // รอให้ thread ทั้งหมดทำงานเสร็จ
    try {
      producer.join();
      consumer1.join();
      consumer2.join();
    } catch (InterruptedException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
  }

  // อ่านข้อมูลจากไฟล์ใส่ลงในบัฟเฟอร์
  static class Producer implements Runnable {
    @Override
    public void run() {
      try {
        // เปิดไฟล์ data.txt อ่านข้อมูล
        BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
        String line;

        // อ่านทีละบรรทัดจนกว่าจะหมดไฟล์
        while ((line = reader.readLine()) != null) {
          // แยกเอาเฉพาะข้อความหลังเครื่องหมาย )
          if (line.contains(")")) {
            line = line.substring(line.indexOf(")") + 1);
          }

          // รอถ้าบัฟเฟอร์เต็ม (ไม่มีการจัดการ critical section)
          while (buffer.size() >= BUFFER_SIZE) {
            Thread.yield(); // สลับให้ thread อื่นทำงาน
          }

          // เพิ่มข้อมูลลงบัฟเฟอร์
          buffer.add(line);
          System.out.println("Producer: add '" + line + "' put in Buffer");
        }

        reader.close();

        isProducerFinished = true;
        System.out.println("EOF 💡");

      } catch (IOException e) {
        System.out.println("error read file: " + e.getMessage());
      }
    }
  }

  // อ่านข้อมูลจากบัฟเฟอร์แล้วแสดงผล
  static class Consumer implements Runnable {
    private String name;

    public Consumer(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      while (true) {
        String data = null;

        // ถ้ายังมีข้อมูลในบัฟเฟอร์
        if (!buffer.isEmpty()) {
          // อ่านและลบข้อมูลออกจากบัฟเฟอร์
          data = buffer.get(0);
          buffer.remove(0);

          // แสดงผลทีละตัวอักษร เว้นวรรค และหน่วงเวลา 10ms
          System.out.print(name + " Read: ");
          for (char c : data.toCharArray()) {
            System.out.print(c + " ");
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              System.out.println("Error: " + e.getMessage());
            }
          }
          System.out.println();

        } else if (isProducerFinished && buffer.isEmpty()) {
          // ถ้า producer ทำงานเสร็จและบัฟเฟอร์ว่าง ให้จบการทำงาน
          System.out.println(name + ": DONE 👍");
          break;
        } else {
          // ถ้าบัฟเฟอร์ว่างแต่ producer ยังไม่เสร็จ ให้รอ
          Thread.yield();
        }
      }
    }
  }
}