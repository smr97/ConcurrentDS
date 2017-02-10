import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.*;

class Account
{
	Lock lockInternal;
	Condition condition;
	private int balance;
	public Account()
	{
		lockInternal = new ReentrantLock();
		condition = lockInternal.newCondition();
		balance = 0;
	}
	private int getBal()
	{
		return balance;
	}
	public void deposit(int amount, int id)
	{
		lockInternal.lock();
		try
		{
			balance+=amount;
			condition.signalAll();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			System.out.println("Thread # " + id + " deposited, bal is " + getBal());
			lockInternal.unlock();
		}
	}
	public void withdraw(int amount, int id)
	{
		lockInternal.lock();
		try
		{
			while(balance == 0)
			{
				condition.await();
			}
			if(balance >= amount)
				balance -= amount;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			System.out.println("Thread # " + id + " withdrew, bal is " + getBal());
			lockInternal.unlock();
		}
	}
}