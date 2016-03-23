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
 
    class Node<T> {
		public T data;
		public Node<T> next;
		int size;
		
		public Node (T data){
			this.data = data;
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
 
	//Constructor
    public LinkedList() {
    	//Set head and tail to null
		//Set size to zero
		//Create new instance for the critical section
    	head = null;
    	tail = null;
    	size = 0;
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
		if(index > size-1){
			return null;
		}else{
			Node<T> n = head;
			for(int i = 0; i <= index; i++){
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
			
			private LinkedList<T>.Node<T> curNode = head, prevNode = null;
			
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
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

		public Pair<LinkedList<T>, LinkedList<T>> split(LinkedList<T> list){
			return null;

		}

		public LinkedList<T> merge(LinkedList<T> list1, LinkedList<T> list2){
			return null;

		}
		
		public void sort(LinkedList<T> list) {

			if(list.head == null || list.head.next == null){
				//return list.head;
			}
		}

		public void parallel_sort(LinkedList<T> list) {			
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
