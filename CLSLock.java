import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;

public class CLSLock
{
	private AtomicReference<QNode> tail = new AtomicReference<QNode>();
	private ThreadLocal<QNode> myFather = new ThreadLocal<QNode>()
	{
		protected QNode initialValue()
		{
			return null;
		}
	};
	private ThreadLocal<QNode> mySelf = new ThreadLocal<QNode>()
	{
		protected QNode initialValue()
		{
			return new QNode();
		}
	};
	public CLSLock()
	{
		tail.set(new QNode());
	}
	public void lock()
	{
		QNode me = mySelf.get();
		me.locked = true;
		myFather.set(tail.getAndSet(me));
		if(myFather.get()==null)
			return;
		while(myFather.get().locked){};

	}
	public void unlock()
	{
		QNode me = mySelf.get();
		me.locked = false;
		mySelf.set(myFather.get());
	}
}

class QNode
{
	public volatile boolean locked = false;
	public volatile QNode predecessor = null;
}