import java.lang.*;
import java.util.*;

class TransactionThread extends Thread
{
    private int id;
    private Account acct;
    TransactionThread(int i, Account a)
    {
        this.acct = a;
        this.id = i;
    }
    public void run()
    {
        Random r = new Random(1000);
        try
        {
            while(true)
            {
                acct.deposit(r.nextInt(1000), id);
                if(Thread.currentThread().getId()%2==1)
                    Thread.sleep(r.nextInt(100));
                else
                    Thread.sleep(r.nextInt(1000));
                acct.withdraw(r.nextInt(1000), id);
            }
        }
        catch(InterruptedException e){}
    }
    public int getID()
    {
        return id;
    }
}

class BankDriver
{
    public static Account a;
    public static void main(String args[])
    {
        Account a = new Account();
        TransactionThread[] threadArr = new TransactionThread[100]; 
        for(int i = 0; i<100; i++)
        {
            threadArr[i] = new TransactionThread(i, a);
            threadArr[i].start();
        }
    }
}