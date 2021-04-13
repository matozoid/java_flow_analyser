int x() {
    try {
        return;
    } finally {
        System.out.println("xxx");
    }
    System.out.println("yyy");
}
/* expected:
1    START  -> 3
3    RETURN -> 5
5    STEP   -> end
*/