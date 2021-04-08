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
3    CHOICE -> end or 5
5    CHOICE -> 3 or 6
6    CHOICE -> 9 or 7
9    CHOICE -> 5 or 10
7    CONTIN -> 5
10   CONTIN -> 3
*/