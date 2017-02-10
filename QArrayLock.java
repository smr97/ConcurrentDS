import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;

public class QArrayLock
{
	private AtomicInteger tail = new AtomicInteger(0);
	volatile boolean[] flags;
	ThreadLocal<Integer> myToken = new ThreadLocal<Integer>() 
	{
		protected Integer initialValue()
		{
			return 0;
		}
	};
	int size;
	public QArrayLock(int numThreads)
	{
		flags = new boolean[numThreads];
		this.size = numThreads;
		flags[0] = true;
	}
	public void lock()
	{
		int slot;
		slot = tail.getAndIncrement() % size;
		myToken.set(slot);
		while(!flags[slot]){};
	}
	public void unlock()
	{
		int slot = myToken.get();
		flags[slot] = false;
		flags[(slot+1)%size] = true;
	}
}