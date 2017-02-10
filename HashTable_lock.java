import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
//Monitors based implementation
class HashTable
{
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock rLock = lock.readLock();
	private Lock wLock = lock.writeLock();
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
		if(bucket[index] != -1)
		{
			System.out.println("Thread "+Thread.currentThread().getId()+" gonna wait");
		}
		while(true)
		{
			wLock.lock();
			if(bucket[index]==-1)
			{
				bucket[index] = input;
				wLock.unlock();
				break;
			}
			wLock.unlock();
			/*
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException e){System.out.println(e.getMessage());}
			*/
		}
	}
	private int find(int input)
	{
		for(int i = 0; i<100; i++)
		{
			if(bucket[i] == input)
				return i;
		}
		return -1;		//no such record
	}
	public int remove(int input)		//this is wrong.
	{
		rLock.lock();
			int index = find(input);
		rLock.unlock();
		if(index!=-1)	//we found it
		{
			wLock.lock();
			bucket[index] = -1;
			wLock.unlock();
			return 0;
		}
		return -1;
	}
	public void printTable()
	{
		rLock.lock();
		for(int i = 0; i<100; i++)
			System.out.print(bucket[i]+" ");
		rLock.unlock();
	}
}