void abc(int b) {
    x:
    while (b > 0) {
        try {
            break x;
        } finally {
            a++;
        }
    }
    a++;
}
/* expected:
1    START  -> 3
3    CHOICE -> 10 or 5 (cond: 3:12)
10   STEP   -> end
5    BREAK  -> 7
7    STEP   -> 10
*/