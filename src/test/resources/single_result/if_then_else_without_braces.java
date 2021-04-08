int abc(int a, int b) {
    if (a == 0)
        a = 15;
     else
        a = 44;
    return 22;
}
/* expected:
1    START  -> 2
2    CHOICE -> 5 or 3
5    STEP   -> 6
3    STEP   -> 6
6    RETURN -> end
*/