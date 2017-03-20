import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
/*	This is a fine grained lazy locking implementation of a binary search tree.
*/
class BinarySearchTree
{
	private Node root;
	public BinarySearchTree()
	{
		root = new Node();
		root.data = 50;
	}
	public boolean insert(int data)
	{
		if(data<0)
			return false;
			try{
			//traversal for insert
			Node temp = root;
			while(true)
			{
				if(data>temp.data)
				{
					if(temp.rightChild!=null)
					{
						temp = temp.rightChild;
						continue;
					}
					else
					{
						temp.lock();
						if(temp.rightChild!=null)
						{
							temp.unlock();
							continue;
						}
						Node ins = new Node();
						ins.data = data;
						temp.rightChild= ins;
						temp.unlock();
						return true;
					}
				}
				else if(data<temp.data)
				{
					if(temp.leftChild!=null)
					{
						temp = temp.leftChild;
						continue;
					}
					else
					{
						//temp is a leaf or a node with one child null, insert after it.
						temp.lock();
						//temp is a leaf with data or a node with only one subtree.
						if(temp.leftChild!=null)
						{
							temp.unlock();
							continue;
						}
						Node ins = new Node();
						ins.data = data;
						temp.leftChild= ins;
						temp.unlock();
						return true;
					}
				}
				else
					return false;
			}
		}
			catch(NullPointerException e){System.out.println("Insert NUll"); return false;}
	}
	/*traversal rules: 
	*data = -1 will only be visible in case we have reached an empty leaf. (see remove stage 2)
	*If the current node has been marked for stage 1 removal, go to its right child.
	*/
	private Node find(int data)
	{
		Node itr = root;
		try{
		while(itr!=null)
		{
			if(itr.data==data)
			{
				return itr;
			}
			if(itr.leftChild==null)
			{
				itr = itr.rightChild;
				continue;
			}
			if(itr.leftChild.data==-1)
			{
				itr = itr.rightChild;
				continue;
			}
			if(itr.rightChild==null)
			{
				itr = itr.leftChild;
				continue;
			}
			if(itr.rightChild.data==-1)
			{
				itr = itr.leftChild;
				continue;
			}
			if(data<itr.data)
				itr = itr.leftChild;
			else if(data>itr.data)
				itr = itr.rightChild;
		}
		}
		catch(NullPointerException e)
		{
			return null;
		}
		return null;
	}

	public boolean remove(int data)
	{
		if(data == root.data)
		{
			return false;
		}
		outer: while(true)
		{
			Node parent = root;
				Node itr = root;
				//traversal
				while(itr!=null)
				{
					if(data>itr.data)
					{
						if(itr.rightChild!=null)
						{
							parent = itr;
							itr = itr.rightChild;
						}
						else
						{
							return false;
						}
					}
					else if(data<itr.data)
					{
						if(itr.leftChild!=null)
						{
							parent = itr;
							itr = itr.leftChild;
						}
						else
						{
							return false;
						}
					}
					else if(data==itr.data)
					{
						/*itr is to be removed and parent is its parent*/
						parent.lock();
						itr.lock();
						break;
					}
				}
				//if(itr == null)
				//	return false;
					if(itr.rightChild==null&&itr.leftChild==null)
					{
						if(parent.leftChild == itr)
							parent.leftChild = null;
						else if(parent.rightChild == itr)
							parent.rightChild = null;
						else
						{
							itr.unlock();
							parent.unlock();
							return false;
						}
						itr.unlock();
						parent.unlock();
						return true;
					}
					else if(itr.rightChild==null)
					{
						if(parent.leftChild == itr)
							parent.leftChild = itr.leftChild;
						else if(parent.rightChild == itr)
							parent.rightChild = itr.leftChild;
						else
						{
							itr.unlock();
							parent.unlock();
							return false;
						}
						itr.unlock();
						parent.unlock();
						return true;	
					}
					else if(itr.leftChild==null)
					{
						if(parent.leftChild == itr)
							parent.leftChild = itr.rightChild;
						else if(parent.rightChild == itr)
							parent.rightChild = itr.rightChild;
						else
						{
							itr.unlock();
							parent.unlock();
							return false;
						}
						itr.unlock();
						parent.unlock();
						return true;
					}
				itr.unlock();
				parent.unlock();
				Node temp = itr.rightChild;
				while(true)
				{
					if(temp==null)
					{
						return false;
					}
					if(temp.leftChild!=null)
					{
						temp = temp.leftChild;
					}
					else
					{
						//stage 1 removal.
						temp.lock();
							if(itr.r1.get()==true)
							{	
								temp.unlock();
								return false;
							}
							if(temp.leftChild!=null)
							{
								temp.unlock();
								continue;
								//return false;
							}
							itr.r1.set(true);
							temp.leftChild = itr.leftChild;
						temp.unlock();
						break;
					}
				}
				//stage 2 removal.
				parent.lock();
				itr.lock();
						if(parent.rightChild==itr)
						{
							parent.rightChild = itr.rightChild;
							itr.unlock();
							parent.unlock();
							return true;
						}
						else if(parent.leftChild==itr)
						{
							parent.leftChild = itr.rightChild;
							itr.unlock();
							parent.unlock();
							return true;
						}
						else
						{
							itr.unlock();
							parent.unlock();
							parent = root;
							itr = root;
							inner:while(true)
							{
								if(itr == null)
								{
									return false;
								}
								else if(data>itr.data)
								{
									parent = itr;
									itr = itr.rightChild;
								}
								else if(data<itr.data)
								{
									parent = itr;
									itr = itr.leftChild;
								}
								else
								{
									parent.lock();
									itr.lock();
									if(parent.rightChild==itr)
									{
										parent.rightChild = itr.rightChild;
										itr.unlock();
										parent.unlock();
										return true;
									}
									else if(parent.leftChild==itr)
									{
										parent.leftChild = itr.rightChild;
										itr.unlock();
										parent.unlock();
										return true;
									}
									else
									{
										itr.unlock();
										parent.unlock();
										/*parent = root;
										itr = root;
										continue inner;*/
										return false;
									}
								}
							}
						}
		}
	}
	public void print()
	{
		printActual(root);
	}
	int counter = 0;
	private void printActual(Node n)
	{
		try{
			counter++;
			if(n==root)
			{
				counter = 0;
				System.out.println(n.data);
				if(n.leftChild!=null&&n.rightChild!=null)
				{
					System.out.print(n.leftChild.data+" "+n.rightChild.data);
					System.out.println();
					printActual(n.leftChild);
					printActual(n.rightChild);
				}
				else if(n.leftChild!=null)
				{
					System.out.println(n.leftChild.data+" null");
					printActual(n.leftChild);
				}
				else if(n.rightChild!=null)
				{
					System.out.println("null "+n.rightChild.data);
					printActual(n.rightChild);
				}
			}
			else if(counter>100)
				return;
			else
			{
				if(n.r1.get()==true)
				{
					System.out.print("parent is "+n.data+"\t");
					System.out.print(n.leftChild.data+" "+n.rightChild.data);
					System.out.println();
					printActual(n.rightChild);
				}
				else if(n.leftChild!=null&&n.rightChild!=null)
				{
					System.out.print("parent is "+n.data+"\t");
					System.out.print(n.leftChild.data+" "+n.rightChild.data);
					System.out.println();
					printActual(n.leftChild);
					printActual(n.rightChild);
				}
				else if(n.leftChild!=null)
				{
					System.out.print("parent is "+n.data+"\t");
					System.out.print(n.leftChild.data+" null\n");
					printActual(n.leftChild);
				}
				else if(n.rightChild!=null)
				{
					System.out.print("parent is "+n.data+"\t");
					System.out.print("null "+n.rightChild.data);
					System.out.println();
					printActual(n.rightChild);
				}
			}
		}
		catch(NullPointerException e)
		{
			return;
		}
	}
}

class Node
{
	private Lock lock;
	public volatile Node leftChild;
	public volatile Node rightChild;
	public volatile int data;
	public AtomicBoolean r1;
	/* r1 is marked true when the node has undergone stage 1 removal.
	*  data is set to -1 iff node has undergone stage 2 removal. Important for find operations.
	*/
	public Node()
	{
		data = -1;
		leftChild = null;
		rightChild = null;
		r1 = new AtomicBoolean();
		lock = new ReentrantLock();
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