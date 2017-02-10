import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.*;
import java.lang.*;
import java.lang.ThreadLocal ;
import java.lang.Object;

public class TASlock
{
	private AtomicBoolean locked = new AtomicBoolean(false);
	public void lock()
	{
		while(locked.getAndSet(true)){}
	}
	public void unlock()
	{
		locked.set(false);
	}
}