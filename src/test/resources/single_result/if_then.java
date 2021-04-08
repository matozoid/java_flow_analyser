int abc(int a, int b) {
    if (a == 0) {
        a = 15;
        b = 20;
    }
    return 22;
}
/* expected:
1    START  -> 2
2    CHOICE -> 6 or 3
6    RETURN -> end
3    STEP   -> 4
4    STEP   -> 6
*/