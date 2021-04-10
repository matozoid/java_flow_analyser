int x() {
        while (true) {
            try {
                // when using continue from a try-finally, the finally has to be executed every time.
                continue;
            } finally {
                System.out.println("xxx");
            }
            System.out.println("yyy");
        }
}
/* expected:
1    START  -> 2
2    CHOICE -> end or 5
5    CONTIN -> 7
7    STEP   -> 2
*/