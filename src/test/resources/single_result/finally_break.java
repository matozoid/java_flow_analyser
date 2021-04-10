/** when breaking from a try-finally, the finally has to be executed. */
int x() {
        while (true) {
            try {
                break;
            } finally {
                System.out.println("xxx");
            }
            System.out.println("yyy");
        }
}
/* expected:
1    START  -> 2
2    CHOICE -> end or 4
4    BREAK  -> 6
6    STEP   -> end
*/