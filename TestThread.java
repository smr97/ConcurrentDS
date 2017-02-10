import java.lang.*;
import java.util.*;

public class TestThread extends Thread
{
    private int id;
    private TOLock tOLock;
    TestThread(int i, TOLock t)
    {
        this.tOLock = t;
        this.id = i;
    }
    public void run()
    {
        Random r = new Random(1000);
        //try
        {
            while(true)
            {
                if(tOLock.tryLock())
                {    
                    int temp = TestDriver.counter;
                    temp++;
                    TestDriver.counter = temp;
                    System.out.println("Thread # "+id+" counter is "+TestDriver.counter);
                    tOLock.unlock();
                }
                /*if(Thread.currentThread().getId()%2==1)
                    Thread.sleep(r.nextInt(100))
                    ;
                else
                    Thread.sleep(r.nextInt(1000))
                    ;*/
            }
        }
        //catch(InterruptedException e){}
    }
}