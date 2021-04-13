void abc(int b) {
    x:
    while (b > 0) {
        try {
            continue x;
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
5    CONTIN -> 7
7    STEP   -> 3
*/