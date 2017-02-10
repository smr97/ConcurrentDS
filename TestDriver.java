import java.lang.*;
import java.util.*;

public class TestDriver
{
	public static int counter = 0;
	public static void main(String args[])
	{
		TOLock tL = new TOLock();
		TestThread[] threadArr = new TestThread[100]; 
		for(int i = 0; i<100; i++)
		{
			threadArr[i] = new TestThread(i, tL);
			threadArr[i].start();
		}
	}
}