import java.lang.*;
import java.util.*;

public class TempDriver
{
	public static void main(String ar[])
	{
        Random r = new Random(100);
		BinarySearchTree bst = new BinarySearchTree();
		/*TestThread[] tArr = new TestThread[500];
		for(int i = 0; i<500; i++)
		{
			tArr[i] = new TestThread(i, hT);
			tArr[i].start();
		}*/
        for(int i = 0; i<20; i++)
        {
            if(i%5==2)
            {
                bst.remove(r.nextInt(10));
            }
            bst.insert(r.nextInt(20));
            bst.print();
            System.out.println("");
        }
	}
}

class TestThread extends Thread
{
    private int id;
    private HashTable hT;
    private int counter = 0;
    TestThread(int i, HashTable hT)
    {
        this.id = i;
        this.hT = hT;
    }
    public void run()
    {
        Random r = new Random(1000);
        try
        {
            while(true)
            {
				if(hT.remove(id+counter-1)==-1);
				else
					System.out.println("thread "+id+" removed "+(counter-1));
				hT.insert(id+counter++);
				System.out.println("thread "+id+" inserted "+ (id+counter-1));
				hT.printTable();
				System.out.println("\n");
            	if(Thread.currentThread().getId()%2==1)
            		Thread.sleep(r.nextInt(100));
            	else
                	Thread.sleep(r.nextInt(1000));
        	}
        }
        catch(InterruptedException e){}
    }
}