void abc(int b) {
    x:
    while (b > 0) {
        y:
        while (a > 0) {
            while (a > 0) {
                continue y;
            }
            while (a > 0) {
                continue x;
            }
        }
    }
}
/* expected:
1    START  -> 3
3    CHOICE -> end or 5 (cond: 3:12)
5    CHOICE -> 3 or 6 (cond: 5:16)
6    CHOICE -> 9 or 7 (cond: 6:20)
9    CHOICE -> 5 or 10 (cond: 9:20)
7    CONTIN -> 5
10   CONTIN -> 3
*/