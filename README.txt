Process Synchronization

The Critical-Section Problem
ปัญหาเกี่ยวข้องกับการเข้าถึงทรัพยากรร่วม (shared resources) โดยกระบวนการหลายตัว (processes) พร้อมกัน 
ซึ่งเรียกว่าส่วน Critical Section 

KeyWord
Semaphore: ตัวแปรพิเศษ ที่ใช้ควบคุมจำนวนการเข้าถึงทรัพยากรที่ใช้ร่วมกัน เช่น buffer
Mutex Locks: เครื่องมือที่ใช้ควบคุม การเข้าถึงทรัพยากรร่วมแบบทีละหนึ่ง process หรือ thread เท่านั้น
Race Condition: สถานการณ์ที่เกิดขึ้นเมื่อ หลาย process หรือ thread เข้าถึงและแก้ไขทรัพยากรร่วม (shared resource) พร้อมกัน โดยที่ลำดับการทำงาน (execution order) มีผลต่อผลลัพธ์สุดท้าย
Deadlock: สถานการณ์ที่ process หลายตัวรอกันเองแบบไม่มีวันจบ เพราะแต่ละ process ครอบครองทรัพยรอยู่ และต้องการทรัพยากรจากอีก process ทำให้ไม่มีใครสามารถทำงานต่อได้
Starvation: สถานการณ์ที่ process ไม่เคยได้ใช้ทรัพยากรเลย เพราะทรัพยากรถูก process อื่นแย่งใช้ไปตลอด ทำให้ process ที่รออยู่นานมาก