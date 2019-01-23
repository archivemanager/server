package org.archivemanager.data;
import java.util.Comparator;

/**
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 */
public class TreeNodeComparator implements Comparator<TreeNode>
{
    private final boolean isDigit(char ch)
    {
        return ((ch >= 48) && (ch <= 57));
    }
    private final String getChunk(String s, int slength, int marker)
    {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c))
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (!isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        } else
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    public int compare(TreeNode s1, TreeNode s2)
    {
    	if ((s1 == null) || (s2 == null)) 
    	{
    		return 0;
    	}

        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.getTitle().length();
        int s2Length = s2.getTitle().length();

        while (thisMarker < s1Length && thatMarker < s2Length)
        {
            String thisChunk = getChunk(s1.getTitle(), s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2.getTitle(), s2Length, thatMarker);
            thatMarker += thatChunk.length();

            int result = 0;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0)))
            {
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                if (result == 0)
                {
                    for (int i = 0; i < thisChunkLength; i++)
                    {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0)
                        {
                            return result;
                        }
                    }
                }
            } 
            else
            {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0)
                return result;
        }

        return s1Length - s2Length;
    }
    
}
