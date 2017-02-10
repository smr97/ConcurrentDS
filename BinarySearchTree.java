import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
public class BinarySearchTree
{
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock rLock = lock.readLock();
	private Lock wLock = lock.writeLock();
	private Node root;
	public BinarySearchTree()
	{
		root = new Node();
	}
	public void insert(int data)
	{
		wLock.lock();		//can be made reader??
		if(root.data == -1)
		{
			root.data = data;
			wLock.unlock();
			return;	
		}
		Node temp = root;
		while(true)
		{
			if(data<temp.data)
			{
				if(temp.leftChild==null||temp.leftChild.data==-1)
				{
					temp.leftChild = new Node();
					temp.leftChild.data = data;
					wLock.unlock();
					return;
				}
				temp = temp.leftChild;
				continue;
			}
			if(data>temp.data)
			{
				if(temp.rightChild==null||temp.rightChild.data==-1)
				{
					temp.rightChild = new Node();
					temp.rightChild.data = data;
					wLock.unlock();
					return;
				}
				temp = temp.rightChild;
				continue;	
			}
			else
			{
				wLock().unlock();	
				return;
			}
		}
		wLock.unlock();
	}
	private Node find(int data)
	{
		rLock.lock();
		Node itr = root;
		while(itr!=null)
		{
			if(data<itr.data)
				itr = itr.leftChild;
			else if(data>itr.data)
				itr = itr.rightChild;
			else
			{
				rLock.unlock();
				return itr;
			}
		}
		rLock.unlock();
		return null;
	}
	private void removeRoot()
	{
		Node left = root.leftChild;
		if(left == null)
		{
			if(root.rightChild == null)
			{
				root.data = -1;
				return;
			}
			root.data = -1;
			root = root.rightChild;
			return;
		}
		Node right = root.rightChild;
		if(right == null)
		{
			root.data = -1;
			root = root.leftChild;
			return;
		}
		Node itr = right;
		while(itr.leftChild!=null)
			itr = itr.leftChild;
		itr.leftChild = left;
		root.leftChild = null;
		root.data = -1;
		root = root.rightChild;
	}
	public void remove(int data)	//verify
	{	
		wLock.lock();
		if(data == root.data)
		{
			removeRoot();
			wLock().unlock();
			return;
		}
		wLock.unlock();
		Node itr = find(data);
		if(itr==null)
			return;
		rLock.lock();
		if(itr.leftChild==null&&itr.rightChild==null)
		{	
			itr.data = -1;
			rLock.unlock();
			return;
		}
		rLock.unlock();		//caveat...it can revert to first condition...
		wLock.lock();
		if(itr.rightChild==null)
		{
			itr.data = itr.leftChild.data;
			itr.rightChild = itr.leftChild.rightChild;
			itr.leftChild = itr.leftChild.leftChild;
			wLock.unlock();
			return;
		}
		Node temp = itr.rightChild;
		while(temp.leftChild!=null)
		{
			temp = temp.leftChild;
		}
		temp.leftChild = itr.leftChild;
		itr.data = itr.rightChild.data;
		itr.leftChild = itr.rightChild.leftChild;
		itr.rightChild = itr.rightChild.rightChild;
		wLock.unlock();
	}
	public void print()
	{
		rLock.lock();
		printActual(root);
		rLock.unlock();
	}
	private void printActual(Node n)
	{
		if(n!=null)
		{
			printActual(n.leftChild);
			printActual(n.rightChild);
			System.out.println(n.data);
		}
		else
			System.out.println(0);
	}
}

class Node
{
	public volatile Node leftChild;
	public volatile Node rightChild;
	public volatile int data;
	public Node()
	{
		data = -1;
		leftChild = null;
		rightChild = null;
	}
}