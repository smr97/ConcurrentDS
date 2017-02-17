import java.lang.*;
import java.util.*;

public class TempDriver
{
	public static void main(String ar[])
	{
        Random r = new Random(100);
		HashTable ht = new HashTable();
		AddThread[] atArr = new AddThread[400];
		for(int i = 0; i<400; i++)
		{
			atArr[i] = new AddThread(i, ht);
			atArr[i].start();
		}
        RemoveThread[] rtArr = new RemoveThread[400];
        for(int i = 0; i<400; i++)
        {
            rtArr[i] = new RemoveThread(i, ht);
            rtArr[i].start();
        }
        PrintThread pt = new PrintThread(ht);
        pt.start();
        /*for(int i = 0; i<20; i++)
        {
            if(i%5==2)
            {
                bst.remove(r.nextInt(10));
            }
            bst.insert(r.nextInt(20));
            bst.print();
            System.out.println("");
        }*/
	}
}

class PrintThread extends Thread
{
    private HashTable ht;
    public PrintThread(HashTable ht)
    {
        this.ht = ht;
    }
    public void run()
    {
        try
        {
            while(true)
            {
                ht.printTable();
                System.out.println();
                Thread.sleep(50);
            }
        }
        catch(InterruptedException ie){}
    }
}

class AddThread extends Thread
{
    private int id;
    private HashTable hT;
    private int counter = 0;
    AddThread(int i, HashTable hT)
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
                if(hT.insert(id+counter++))
				    ;//System.out.println("thread "+Thread.currentThread().getId()+" inserted "+ (id+counter-1));
                else
                    ;//System.out.println("thread "+Thread.currentThread().getId()+" DIDN'T insert "+ (id+counter-1));
                Thread.sleep(r.nextInt(90));
        	}
        }
        catch(InterruptedException e){}
    }
}

class RemoveThread extends Thread
{
    private int id;
    private HashTable hT;
    private int counter = 0;
    RemoveThread(int i, HashTable hT)
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
                if(!(hT.remove(id+counter-1))&&(id+counter-1>0))
                {
                    //System.out.println("thread "+Thread.currentThread().getId()+" DIDNT'T remove "+(counter-1));
                }
                else
                    counter++;//System.out.println("thread "+Thread.currentThread().getId()+" removed "+(id+counter-1));
                Thread.sleep(r.nextInt(100));
            }
        }
        catch(InterruptedException e){}
    }
}