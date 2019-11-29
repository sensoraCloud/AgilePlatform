/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.util;

import de.enough.polish.util.Comparable;
import de.enough.polish.util.Comparator;
import java.util.Vector;

/**
 *
 * @author Rocco
 */
public class HeapSorter
{
      
   /**
     * Standard heapsort.
     * @param a an array of objects if class comparator is null else array of Comparable items.
     * @param a an array of vector to sincronized swap from sort array (can null).
     * @param a Class comparator(can null).
     */
    public static void heapsort( Vector a ,Vector swapVectors, Comparator comp)
    {
       
        for( int i = a.size() / 2; i >= 0; i-- )  /* buildHeap */
            percDown( a, i, a.size(),swapVectors ,comp);
        for( int i = a.size() - 1; i > 0; i-- )
        {
            swapReferences( a, 0, i, swapVectors );            /* deleteMax */
            percDown( a, 0, i,swapVectors,comp );
        }
    }

    /**
     * Internal method for heapsort.
     * @param i the index of an item in the heap.
     * @return the index of the left child.
     */
    private static int leftChild( int i )
    {
        return 2 * i + 1;
    }





    /**
     * Internal method for heapsort that is used in
     * deleteMax and buildHeap.
     * @param a an array of Comparable items.
     * @index i the position from which to percolate down.
     * @int n the logical size of the binary heap.
     */
    private static void percDown( Vector a, int i, int n ,Vector swapVectors, Comparator comp )
    {
        int child;

        Comparable tmpComp=null;
        Object tmpObj=null;

        Object tmp;

        if (comp!=null)
            tmp=tmpObj;
        else tmp=tmpComp;
       
        Vector temp=null;

        if (swapVectors != null) {

            temp = new Vector();

            int size = swapVectors.size();

            for (int j = 0; j < size; j++) {

                temp.addElement((Object) ((Vector) swapVectors.elementAt(j)).elementAt(i));

            }

        }

        for( tmp = a.elementAt(i); leftChild( i ) < n; i = child )
        {

            if (swapVectors != null) {

                temp=new Vector();

                int size = swapVectors.size();

                for (int j = 0; j < size; j++) {

                    temp.addElement( (Object) ((Vector) swapVectors.elementAt(j)).elementAt(i));

                }

            }

            child = leftChild( i );

         
            if (comp != null) {

                if (child != n - 1 && comp.compare( a.elementAt(child) , a.elementAt(child+1) ) < 0) {
                    child++;
                }

                if ( comp.compare(tmp, a.elementAt(child) ) < 0) {

                    a.setElementAt( a.elementAt(child) , i) ;

                    if (swapVectors != null) {

                        int size = swapVectors.size();

                        for (int j = 0; j < size; j++) {

                            ((Vector) swapVectors.elementAt(j)).setElementAt((Object) ((Vector) swapVectors.elementAt(j)).elementAt(child), i);

                        }

                    }

                } else {
                    break;
                }


            } else {

                if (child != n - 1 && ((Comparable)  a.elementAt(child)).compareTo( a.elementAt(child+1)) < 0) {
                    child++;
                }

                if ( ((Comparable)tmp).compareTo( a.elementAt(child)) < 0) {

                    a.setElementAt( a.elementAt(child) , i) ;
                   
                    if (swapVectors != null) {

                        int size = swapVectors.size();

                        for (int j = 0; j < size; j++) {

                            ((Vector) swapVectors.elementAt(j)).setElementAt((Object) ((Vector) swapVectors.elementAt(j)).elementAt(child), i);

                        }

                    }

                } else {
                    break;
                }


            }




        }

        a.setElementAt( tmp , i) ;

        if (swapVectors != null) {

            int size = swapVectors.size();

            for (int j = 0; j < size; j++) {

                ((Vector) swapVectors.elementAt(j)).setElementAt((Object)temp.elementAt(j),i);

            }

        }
        

    }


    /**
     * Method to swap to elements in an array.
     * @param a an array of objects.
     * @param index1 the index of the first object.
     * @param index2 the index of the second object.
     */
    public static final void swapReferences( Vector a, int index1, int index2 ,Vector swapVectors)
    {

        Object tmp =a.elementAt(index1);
        a.setElementAt( a.elementAt(index2) , index1) ;
        a.setElementAt( tmp , index2) ;
      

        if (swapVectors != null) {

            int size = swapVectors.size();

            for (int i = 0; i < size; i++) {

                Object temp = (Object) ((Vector) swapVectors.elementAt(i)).elementAt(index1);

                ((Vector) swapVectors.elementAt(i)).setElementAt((Object) ((Vector) swapVectors.elementAt(i)).elementAt(index2), index1);
                ((Vector) swapVectors.elementAt(i)).setElementAt(temp, index2);

            }

        }


    }

     

    

}    // end class HeapSorter

