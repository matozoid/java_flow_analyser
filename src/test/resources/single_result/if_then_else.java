int abc(int a, int b) {
    if (a == 0) {
        a = 15;
        b = 20;
    } else {
        a = 44;
        b = 66;
    }
    return 22;
}
/* expected:
1    START  -> 2
2    CHOICE -> 6 or 3
6    STEP   -> 7
3    STEP   -> 4
7    STEP   -> 9
4    STEP   -> 9
9    RETURN -> end
*/