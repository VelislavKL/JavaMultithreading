public class ElapsedTime {

	    private final long start;
	 
	    public ElapsedTime() {
	        start = System.currentTimeMillis();
	    }
	 
	    public double elapsed() {
	        long end = System.currentTimeMillis();
	        return end- this.start;
	}
}
