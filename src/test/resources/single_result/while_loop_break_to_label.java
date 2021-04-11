void abc(int b) {
    x:
    while (b > 0) {
        y:
        while (a > 0) {
            break y;
        }
        while (a > 0) {
            break x;
        }
    }
}
/* expected:
1    START  -> 3
3    CHOICE -> end or 5 (cond: 3:12)
5    CHOICE -> 8 or 6 (cond: 5:16)
8    CHOICE -> 3 or 9 (cond: 8:16)
6    BREAK  -> 8
9    BREAK  -> end
*/