import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.*;
/*	This is a fine grained lazy locking implementation of a binary search tree.
*/
public class BinarySearchTree
{
	private Node root;
	public BinarySearchTree()
	{
		root = new Node();
	}
	public boolean insert(int data)
	{
		root.lock();		//can be made reader??
		if(root.data == -1)
		{
			root.data = data;
			root.unlock();
			return;
		}
		root.unlock();
		//traversal for insert
		while(true)
		{
			Node temp = root;
			while(true)
			{
				if(temp.rightChild==null&&temp.leftChild==null)
				{
					break;
				}
				if(temp.rightChild==null)
				{
					temp = temp.leftChild;
					continue;
				}
				if(temp.leftChild==null)
				{
					temp = temp.leftChild;
					continue;
				}
				if(data<temp.data)
				{
					temp = temp.leftChild;
					continue;
				}
				if(data>temp.data)
				{
					temp = temp.rightChild;
					continue;
				}
				else
				{
					return false;
				}
			}
			//temp is a leaf, insert after it if it has data, else insert into it.
			temp.lock();
				//temp is a leaf and holds no data.
				if(temp.data==-1&&temp.rightChild==null&&temp.leftChild==null)
				{
					temp.data = input;
					temp.unlock();
					return true;
				}
				//temp is no longer a leaf, retraverse. <<Starvation>>
				else if(temp.rightChild!=null||temp.leftChild!=null)
				{
					temp.unlock();
					continue;
				}
				//temp is a leaf with data
				else
				{
					Node ins = new Node();
					ins.data = input;
					ins.parent = temp;
					if(input<temp.data)
					{
						
						temp.leftChild = ins;
						temp.unlock();
						return true;
					}
					if(input>temp.data)
					{
						temp.rightChild = ins;
						temp.unlock();
						return true;
					}
					else
					{
						temp.unlock();
						return false;
					}
				}
		}
	}
	/*traversal rules: 
	*data = -1 will only be visible in case we have reached an empty leaf. (see remove stage 2)
	*If the current node has been marked for stage 1 removal, go to its right child.
	*/
	private Node find(int data)
	{
		Node itr = root;
		while(itr!=null)
		{
			if(itr.data==data)
			{
				return itr;
			}
			if(itr.r1==true)
			{
				itr = itr.rightChild;
				continue;
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
		return null;
	}
	private void removeRoot()
	{
		root.lock();
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
		root.unlock();
		Node itr = right;
		while(itr.leftChild!=null)
		{
			if(itr.leftChild.data==-1)
			{
				itr = itr.rightChild;
				continue;
			}
			itr = itr.leftChild;
		}
		//link left subtree of the root to leftmost leaf of right subtree of root
		itr.lock();
				root.leftChild.lock();
					itr.leftChild = root.leftChild;
					root.leftChild.parent = itr;
				root.leftChild.unlock();
		itr.unlock();
		//move root reference
		root = root.rightChild;
	}
	public boolean remove(int data)
	{
		if(data == root.data)
		{
			removeRoot();
			return true;
		}
		
		while(true)
		{
			//traverse and find the node to be removed.
			Node itr = find(data);
			if(itr==null)
				return false;
			//removal for less than 2 subtrees.
			itr.parent.lock();
			itr.lock();
				if(itr.leftChild==null&&itr.rightChild==null)
				{
					itr.data = -1;
					itr.unlock();
					itr.parent.unlock();
					return true;
				}
				if(itr.rightChild==null)
				{
						if(itr.parent.leftChild==itr)
						{
							itr.parent.leftChild = itr.leftChild;
						}
						else
						{
							itr.parent.rightChild = itr.leftChild;
						}
					itr.data = -1;
					itr.unlock();
					itr.parent.unlock();
					return true;
				}
				if(itr.leftChild==null)
				{
					itr.parent.lock();
						if(itr.parent.leftChild==itr)
						{
							itr.parent.leftChild = itr.rightChild;
						}
						else
						{
							itr.parent.rightChild = itr.rightChild;
						}
					itr.data = -1;
					itr.unlock();
					itr.parent.unlock();
					return true;
				}
				if(itr.leftChild.data==-1&&itr.rightChild.data==-1)
				{
					itr.parent.lock();
						if(itr.parent.leftChild==itr)
						{
							itr.parent.leftChild = itr.rightChild;
						}
						else
						{
							itr.parent.rightChild = itr.rightChild;
						}
					itr.data = -1;
					itr.unlock();
					itr.parent.unlock();
					return true;
				}
			itr.unlock();
			//find the leftmost leaf
			Node temp = itr.rightChild;
			while(temp.leftChild!=null)
			{
				if(temp.leftChild.data==-1)
				{
					if(temp.rightChild==null)
					{
						break;	//found leaf
					}
					if(temp.rightChild.data==-1)
					{
						break;	//found leaf
					}
					temp = temp.rightChild;
					continue;
				}
				temp = temp.leftChild;
			}
			//Stage 1 of removal.
			temp.lock();
				itr.leftChild.lock();
					//validate that it is still a leaf of the tree and the left child-to-be is not marked for removal and the node to be removed hasnt already undergone stage 1 removal.
					if(temp.data==-1||itr.leftChild.data==-1||itr.r1==true)
					{
						temp.unlock();
						itr.leftChild.unlock();
						continue;
					}
					//add links
					temp.leftChild = itr.leftChild;
					itr.leftChild.parent = temp;
					itr.r1 = true;
				itr.leftChild.unlock();
			temp.unlock();
			//Stage 2 of removal
			while(true)
			{
				parent = itr.parent;
				parent.lock();
					if(parent.data==-1||parent.r1==true)	//parent is going to be or has been removed.
					{
						parent.unlock();
						continue;
					}
					itr.lock();
						if(parent.leftChild==itr)
						{
							parent.leftChild = itr.rightChild;
						}
						else
						{
							parent.rightChild = itr.rightChild;
						}
						itr.data=-1;
						itr.rightChild.parent = parent;
					itr.unlock();
				parent.unlock();
			}
		}
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
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public volatile Node leftChild;
	public volatile Node rightChild;
	public volatile Node parent;
	public volatile int data;
	public volatile boolean r1;
	/* r1 is marked true when the node has undergone stage 1 removal.
	*  data is set to -1 iff node has undergone stage 2 removal. Important for find operations.
	*/
	public Node()
	{
		data = -1;
		leftChild = null;
		rightChild = null;
		parent = null;
		r1 = false;
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