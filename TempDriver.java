import java.lang.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class TempDriver
{
    public static Lock printl = new ReentrantLock();
	public static void main(String ar[])
	{
        Random r = new Random(100);
		BinarySearchTree bst = new BinarySearchTree();
		AddThread[] atArr = new AddThread[100];
		for(int i = 0; i<3; i++)
		{
			atArr[i] = new AddThread(i, bst);
			atArr[i].start();
		}
        RemoveThread[] rtArr = new RemoveThread[100];
        for(int i = 0; i<3; i++)
        {
            rtArr[i] = new RemoveThread(i, bst);
            rtArr[i].start();
        }
        PrintThread pt = new PrintThread(bst);
        //pt.start();
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
    private BinarySearchTree bst;
    public PrintThread(BinarySearchTree bst)
    {
        this.bst = bst;
    }
    public void run()
    {
        try
        {
            while(true)
            {
                bst.print();
                System.out.flush();
                System.out.println("\n");
                Thread.sleep(80);
            }
        }
        catch(InterruptedException ie){}
    }
}

class AddThread extends Thread
{
    private int id;
    private BinarySearchTree bst;
    private int counter = 0;
    Random r = new Random(100);
    AddThread(int i, BinarySearchTree bst)
    {
        this.id = i;
        this.bst = bst;
    }
    public void run()
    {
        Random r = new Random();
        //try
        {
            while(true)
            {
                if(bst.insert(id+counter++))
				{
                    //System.out.println("thread "+Thread.currentThread().getId()+" INSERTED "+ (id+counter-1));
                    //TempDriver.printl.lock();
                    //bst.print();
                    //System.out.flush();
                    //System.out.println("\n");
                    //TempDriver.printl.unlock();
                }
                else
                {
                    //TempDriver.printl.lock();
                    //System.out.println("thread "+Thread.currentThread().getId()+" DIDN'T insert "+ (id+counter-1));
                    //System.out.flush();
                    //TempDriver.printl.unlock();*/
                }
                counter = r.nextInt(10000)%100;
                //Thread.sleep(r.nextInt(50));
        	}
        }
        //catch(InterruptedException e){}
    }
}

class RemoveThread extends Thread
{
    private int id;
    private BinarySearchTree bst;
    private int counter = 0;
    Random r = new Random(100);
    RemoveThread(int i, BinarySearchTree bst)
    {
        this.id = i;
        this.bst = bst;
    }
    public void run()
    {
        Random r = new Random();
        //try
        {
            while(true)
            {
                if(!(bst.remove(id+counter-1))||(id+counter-1<=0))
                {
                    //TempDriver.printl.lock();
                    //System.out.println("thread "+Thread.currentThread().getId()+" DIDNT'T remove "+(id+counter-1));
                    //System.out.flush();
                    //TempDriver.printl.unlock();*/
                    //System.out.println("\n");
                }
                else
                    {
                        System.out.println("thread "+Thread.currentThread().getId()+" REMOVED "+(id+counter-1));
                        //System.out.flush();
                        //TempDriver.printl.lock();
                        //bst.print();
                        //System.out.flush();
                        //System.out.println("\n");
                        //TempDriver.printl.unlock();
                    }
                counter = r.nextInt(10000)%100;
                //Thread.sleep(r.nextInt(100));
            }
        }
        //catch(InterruptedException e){}
    }
}