package repurp;



/**
 * A class used to access variables frequently called and/or modified in a
 * variety of locations.
 * 
 * @author dtlehrer
 */
public final class Variables{
	/**
	 * The number of unique results returned by a given EDirect command line
	 * query.
	 */
  public static double queryCount;
	/** The user-supplied input disease entered to the repurp script. */
  public static String originalDisease;
}