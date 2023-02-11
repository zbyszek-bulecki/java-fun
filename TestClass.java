public class TestClass {
    public static void main(String[] args) {
	System.out.println("Hello World!");
	System.out.println("Hello from EMACS!");
    }
}

// Now it's time for the internal class ;-)

class Animal {
    String name;

    public String getName(){
	return name;
    }

    public void setName(String newName){
	name = newName;
    }
}

/*
* Multi
* line*
* comment
* time */

/**
* It's Javadoc time!
* @author Zbyszek
*/
