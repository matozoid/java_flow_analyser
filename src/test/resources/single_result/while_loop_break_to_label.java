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
3    CHOICE -> end or 5
5    CHOICE -> 8 or 6
8    CHOICE -> 3 or 9
6    BREAK  -> 8
9    BREAK  -> end
*/