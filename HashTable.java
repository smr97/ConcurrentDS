import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
//Monitors based implementation
public class HashTable
{
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock rLock = lock.readLock();
	private Lock wLock = lock.writeLock();
	private Condition condition = wLock.newCondition();
	private volatile int[] bucket = new int[100];
	public HashTable()
	{
		for(int i = 0; i<100; i++)
		{
			bucket[i] = -1;
		}
	}
	private int hashFunction(int input)		//preferrably less than or equal to 1K
	{
		int index = input%100;
		return index;
	}
	public void insert(int input)
	{
		int index = hashFunction(input);
		wLock.lock();
		try
		{
			while(bucket[index] != -1)
			{
				System.out.println("Thread "+Thread.currentThread().getId()+" gonna wait");
				condition.await();
			}	
			bucket[index] = input;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			wLock.unlock();
		}
	}
	private int find(int input)
	{
		for(int i = 0; i<100; i++)
		{
			if(bucket[i] == input)
				return i;
		}
		return -1;
	}
	public int remove(int input)	//is this correct?
	{
		rLock.lock();
		try
		{
			int index = find(input);
			if(index!=-1)
			{
				bucket[index] = -1;
				condition.signalAll();
				return 0;
			}
			return -1;
		}
		catch(Exception e)
		{
			return -1;
		}
		finally
		{
			rLock.unlock();
		}
	}
	public void printTable()
	{
		rLock.lock();
		for(int i = 0; i<100; i++)
			System.out.print(bucket[i]+" ");
		rLock.unlock();
	}
}