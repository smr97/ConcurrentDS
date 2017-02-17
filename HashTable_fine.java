import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
//Monitors based implementation
class Node
{
	public volatile Node next;
	public volatile int data;
	private Lock lock = new ReentrantLock();
	public volatile boolean removed;
	public Node()
	{
		next = null;
		removed = false;
		data = -1;
	}
	public void lock()
	{
		lock.lock();
	}
	public void unlock()
	{
		lock.unlock();
	}
}
class HashTable
{
	private volatile Node[] bucket = new Node[10];
	public HashTable()
	{
		for(int i = 0; i<10; i++)
		{
			bucket[i] = new Node();
		}
	}
	private int hashFunction(int input)		//preferrably less than or equal to 1K
	{
		int index = input%10;
		return index;
	}
	private boolean validate(Node a, Node b)
	{
		if(b == null)
		{
			return a.next==b&&a.removed==false;
		}
		return (a.next==b)&&(a.removed==false)&&(b.removed==false);
	}
	public boolean insert(int input)
	{
		int index = hashFunction(input);
		if(index<0)
			return false;
		while(true)
		{
			Node curr = bucket[index];
			Node pred = curr;
			while(curr!=null)
			{
				if(curr.data>input)
					break;
				if(curr.data==input&&curr.removed==false)
				{
					return false;
				}
				pred = curr;
				curr = curr.next;
			}
			//System.out.println(Thread.currentThread().getId()+" gonna lock 1 ");
			pred.lock();
				if(curr == null)
				{
					if(!(validate(pred, curr)))
					{
						pred.unlock();
						continue;
					}
					Node temp = new Node();
					temp.data = input;
					pred.next = temp;
					pred.unlock();
					return true;
				}
			//System.out.println(Thread.currentThread().getId()+" gonna lock 2 ");
				curr.lock();
					if(validate(pred, curr))
					{
						if(curr.data == input)
						{
							curr.unlock();
							pred.unlock();
							return false;
						}
						else
						{
							Node temp = new Node();
							temp.data = input;
							temp.next = curr;
							pred.next = temp;
						}
						curr.unlock();
						pred.unlock();
						return true;
					}
				curr.unlock();
			pred.unlock();
		}
	}
	public boolean remove(int input)
	{
		int index = hashFunction(input);
		if(index<0)
			return false;
		while(true)
		{
			Node curr = bucket[index];
			Node pred = curr;
			while(curr!=null&&curr.data<input)
			{
				pred = curr;
				curr = curr.next;
			}
			if(curr == null||curr.removed == true)
			{
				return false;
			}
			//System.out.println(Thread.currentThread().getId()+" gonna lock 1r ");
			pred.lock();
			//System.out.println(Thread.currentThread().getId()+" gonna lock 2r ");
				curr.lock();
					if(validate(pred, curr))
					{
						if(curr.data != input)
						{
							curr.unlock();
							pred.unlock();
							return false;
						}
						curr.removed = true;
						pred.next = curr.next;
						curr.unlock();
						pred.unlock();
						return true;
					}
				curr.unlock();
			pred.unlock();
		}
	}
	public void printTable()
	{
		for(int i = 0; i<10; i++)
		{
			Node itr = bucket[i];
			while(itr!=null)
			{
				if(itr.removed!=true)
					System.out.print(itr.data+" ");
				itr = itr.next;
			}
			System.out.println();
		}	
	}
}