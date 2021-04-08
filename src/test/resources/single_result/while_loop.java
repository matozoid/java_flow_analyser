void abc(int b) {
    int a = 10;
    while (a > 0) {
        a--;
        b++;
    }
    return b;
}
/* expected:
1    START  -> 2
2    STEP   -> 3
3    CHOICE -> 7 or 4
7    RETURN -> end
4    STEP   -> 5
5    STEP   -> 3
*/