/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agile.util;

import de.enough.polish.util.Comparator;

/**
 *
 * @author Rocco
 */
public class IntegerComparator implements Comparator{

public int compare(Object int1, Object int2){

int first = ( (Integer) int1).intValue();

int second = ( (Integer) int2).intValue();

if( first > second )

return 1;

else if( first < second )

return -1;

else

return 0;

}

}
