/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.util;

import java.util.Vector;

/**
 *
 * @author ruego
 */
public class Search_Util {
    
    
        public static int binarySearch (int a[], int p, int r, int k) {
            
		int q, s = -1;
		if (p < r) {
			q = (p+r)/2;
			if (k < a[q])
				s = binarySearch(a, p, q-1, k);
			if (k > a[q])
				s = binarySearch(a, q+1, r, k);
			if (k == a[q])
				s = q;
		}
		if (p == r)
			if (a[p] == k)
				s = p;
		if (p > r)
			s = -1;
		return s;
                
	}

          
        public static int binaryIntegerVectorSearch (Vector a, int p, int r, int k) {
            
		int q, s = -1;
                
		if (p < r) {
			q = (p+r)/2;
			if (k < (((Integer)a.elementAt(q))).intValue())
				s = binaryIntegerVectorSearch(a, p, q-1, k);
			if (k > (((Integer)a.elementAt(q))).intValue())
				s = binaryIntegerVectorSearch(a, q+1, r, k);
			if (k == (((Integer)a.elementAt(q))).intValue())
				s = q;
		}
		if (p == r)
			if (((((Integer)a.elementAt(p))).intValue()) == k)
				s = p;
		if (p > r)
			s = -1;
		return s;
	}

        
        
     public static Vector getMatchingIntegerVector(Vector first,Vector second){
     
         Vector results=new Vector();
         
         int sizefirst=first.size();
         int sizesecond=second.size();
     
         int index=0;
         
         for (int f = 0; f < sizefirst; f++) {

            index=binaryIntegerVectorSearch(second, 0,(sizesecond-1), ((Integer)first.elementAt(f)).intValue() );     

            if (index>-1)
                results.addElement(first.elementAt(f));
            
         }
         
         return results;     
    
     }
    

}
