package cpsc457;

import cpsc457.doNOTmodify.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList<T> implements Iterable<T> {
 
	//####################
	//# Static Functions #
	//####################
	
	//We do not want the testers to have the freedom of specifying the comparison function
	//Thus, we will create wrappers for them that they can use and inside these wrappers
	//we will have the comparison function predefined
		//These two static wrappers, will simply call the sort method in the list passed as parameter,
		//and they pass the comparison function as well
	
	public static <T extends Comparable<T>> void par_sort(LinkedList<T> list) {
		list.par_sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static <T extends Comparable<T>> void sort(LinkedList<T> list){
        list.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }
 
    static class Node<T> {
		public T data;
		public Node<T> next;
		
		public Node (T data){
			this.data = data;
			this.next = null;
		}
	}
	//############
	//# LinkList #
	//############
	
	//Variables (attributes)
		//Head
		//Tail
		//Size (not required)
		//Critical Section
 		private Node<T> head;
    	private Node<T> tail;
    	private int size;
    	private static Lock mutex = null;
 
	//Constructor
    public LinkedList() {
    	//Set head and tail to null
		//Set size to zero
		//Create new instance for the critical section
    	head = null;
    	tail = null;
    	size = 0;
    	mutex = new ReentrantLock(true);
    }

	//Returns the size of the list
    public int size() {
        return size; //either iterate through all the list and count
					//or create an attribute that stores the size and changes
					//every time we add or remove a node
    }
	
	//Checks if the list is empty
	public boolean isEmpty() {
        if(size == 0){
        	return true;
        }else{
        	return false;
        }
    }
	
	//Deletes all the nodes in the list
	public void clear() {
		head = tail = null;
		//just set the head and tail to null (the garbage collector takes care of the rest)
			//cpp developers: be careful, you have to destroy them first
		
		//What if the merge sort is running now in a thread
			//I should not be able to delete the nodes (and vice versa)
			//Thus run this and everything else in a critical section
    }
	
	//Adds a new node to the list at the end (tail)
    public void append(T t) {
    	Node<T> temp = new Node(t);
        if(head == null){
        	head = tail = temp;
        	size += 1;
        }else{
        	tail.next = temp;
        	tail = temp;
        	size += 1;
        }
		//Check if it is empty 
			//head = tail = t
		//Else add to the tail and move the tail to the end
			//tail.next = t    then		tail = t
		
		//Do not forget to increment the size by 1 (if you have it as an attribute)
    }

	//Gets a node's value at a specific index
    public Node<T> get(int index) {
		if(index > size){
			return null;
		}else{
			Node<T> n = head;
			for(int i = 1; i < index; i++){
				n = n.next;
			}
			return n;
		}
		//Iterate through the list
			//Create a new pointer that starts at the head
			//Keeps moving forward (pt = pt.next) for index times
			//then return that object
		
		//Make sure not to exceed the size of the list (else return null)
    }
	
	@Override
    public Iterator<T> iterator() {
		Iterator<T> iter = new Iterator<T>(){
			
			private LinkedList.Node<T> curNode = head, prevNode = null;
			
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				if(head == null)return false;
				if (curNode.next != null) return true;
				else return false;
			}

			@Override
			public T next() {
				// TODO Auto-generated method stub
				prevNode = curNode;
				curNode = curNode.next;
				return curNode.next.data;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				prevNode.next = curNode.next;
				curNode = curNode.next;
			}
			
		};
		return iter;
    }
	
	//The next two functions, are being called by the static functions at the top of this page
	//These functions are just wrappers to prevent the static function from deciding which
	//sorting algorithm should it use.
	//This function will decide which sorting algorithm it should use 
	//(we only have merge sort in this assignment)
	
	//Sorts the link list in serial
    private void sort(Comparator<T> comp) {
	
		new MergeSort<T>(comp).sort(this); //Run this within the critical section (as discussed before)
		
		//It might not allow you to use this inside critical
			//Create a final pointer = this then use that pointer
    }

	//Sorts the link list in parallel (using multiple threads)
    private void par_sort(Comparator<T> comp) {
		new MergeSort<T>(comp).parallel_sort(this); //Run this within the critical section (as discussed before)
    }

	//Merge sort
    static class MergeSort<T> {
	
		//Variables (attributes)
			//ExecutorService
			//Depth limit
    	ExecutorService exec = Executors.newFixedThreadPool(25);
    	int threadsLeft = 25;
	
		//Comparison function
		final Comparator<T> comp;

		//Constructor
		public MergeSort(Comparator<T> comp) {
			this.comp = comp;
		}

		//#####################
		//# Sorting functions #
		//#####################
		//The next two functions will simply call the correct function 
		//to merge sort the link list and then they will fix its 
		//attributes (head and tail pointers)


		public LinkedList<T> merge(LinkedList<T> list1, LinkedList<T> list2){
			Node<T> l = list1.head, r = list2.head;
			LinkedList<T> retList = new LinkedList<T>();
			Node<T> curNode = null;
			
			while(l != null && r != null ){
				int result = this.comp.compare(l.data, r.data);
				
				if(result <= 0){
					curNode = l;
					l = l.next;
				}else{
					curNode = r;
					r = r.next;
				}
				
				retList.append(curNode.data);
			}
			
			if (r !=  null){
				while(r != null){
					retList.append(r.data);
					r = r.next;
				}
			}
			if(l != null){
				while(l != null){
					retList.append(l.data);
					l = l.next;
				}
			}
			
			return retList;
		}
		
		public void sort(LinkedList<T> list) {
			this.sort1(list);
		}
		
		public LinkedList<T> sort1(LinkedList<T> list){
			if(list.size <= 1) return list;
			
			int half = (list.size / 2);
			
			LinkedList<T> newlist = new LinkedList<T>();
			
			Node<T> halfNode = list.get(half);
			newlist.head = halfNode.next;
			newlist.tail = list.tail;
			newlist.size = list.size - half;
			
			list.tail = halfNode;
			list.size = half;
			
			halfNode.next = null;
			
			LinkedList<T> firstList = this.sort1(list);
			LinkedList<T> secondList = this.sort1(newlist);
			
			list = this.merge(firstList, secondList);
			return list;
		}

		public void parallel_sort(LinkedList<T> list) {
			this.parallel_sort(list);
		}
		
		public LinkedList<T> parallel_sort1(LinkedList<T> list){
			mutex.lock(); //lock the critical section first
			if(list.size <= 1) return list;
			Future<LinkedList<T>> result1 = null, result2 = null;		
			int half = (list.size / 2) - 1;
			
			LinkedList<T> newlist = null;
			
			Node<T> halfNode = list.get(half);
			newlist.head = halfNode.next;
			halfNode.next = null;
			if (threadsLeft != 0){
				//result1 = exec.submit(this.parallel_sort1(list)); //how to submit a task that isn't run();
				threadsLeft --;
			}else{
				//sequential sort
			}
			
			if (threadsLeft != 0){
				//result2 = exec.submit(this.parallel_sort1(list)); //how to submit a task that isn't run();
				threadsLeft --;
			}else{
				//sequential sort
			}
			
			LinkedList<T> firstList = this.sort1(list);
			LinkedList<T> secondList = this.sort1(newlist);
			
			while(!result1.isDone()||!result2.isDone()); //wait for threads to finish
			
			if (result1 != null){
				try {
					firstList = result1.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(result2 != null){
				try {
					secondList = result2.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			LinkedList<T> retList = this.merge(firstList, secondList);
			
			mutex.unlock(); //release the lock
			return retList;
		}

		
		//#########
		//# Steps #
		//#########
		
		//The main merge sort function (parrallel_msort and msort)
			//Split the list to two parts
			//Merge sort each part
			//Merge the two sorted parts together
		
		//Splitting function
			//Run two pointers and find the middle of the a specific list
			//Create two new lists (and break the link between them)
			//It should return pair (the two new lists)
		
		//Merging function
			//1- Keep comparing the head of the two link lists
			//2- Move the smallest node to the new merged link list
			//3- Move the head on the list that lost this node
			
			//4- Once one of the two lists is done, append the rest of the 
			//	 second list to the tail of the new merged link list
	}

 
}
