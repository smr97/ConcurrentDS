import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;

public class TOLock
{
	private AtomicReference<QNode> tail = new AtomicReference<QNode>();
	private QNode avl;
	public long patience = 50000;
	private ThreadLocal<QNode> mySelf = new ThreadLocal<QNode>()
	{
		protected QNode initialValue()
		{
			return new QNode();
		}
	};
	public TOLock()
	{
		avl = new QNode();
		tail.set(avl);
	}
	public boolean tryLock()
	{
		long initTime = System.currentTimeMillis();
		QNode me = new QNode();
		me.predecessor = null;
		mySelf.set(me);
		QNode pred = tail.getAndSet(me);
		if(pred == avl)
			return true;
		while(pred!=avl)
		{
			if(pred.predecessor!=null)
				pred = pred.predecessor;
			if(System.currentTimeMillis()-initTime>=patience)
			{
				me.predecessor = pred;
				return false;	
			}
		}
		return true;
	}
	public void unlock()
	{
		QNode me = mySelf.get();
		if(!tail.compareAndSet(me, avl))
			me.predecessor = avl;
	}
}