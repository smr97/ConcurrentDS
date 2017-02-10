import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;

public class MCSLock
{
	private AtomicReference<QNode> tail = new AtomicReference<QNode>();
	public MCSLock()
	{
		tail.set(null);
	}
	private ThreadLocal<QNode> self = new ThreadLocal<QNode>()
	{
		protected QNode initialValue()
		{
			return new QNode();
		}
	};
	public void lock()
	{
		QNode me = self.get();
		QNode parent = tail.getAndSet(me);
		if(parent==null)
			return;
		parent.child = me;
		while(!me.locked){};	//wait for parent.
	}
	public void unlock()
	{
		QNode me = self.get();
		me.locked = false;
		if(tail.compareAndSet(me, null)&&me.child==null)
			return;
		while(me.child == null){};
		me.child.locked = true;
		me.child = null;
	}
}