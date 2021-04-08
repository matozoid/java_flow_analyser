void abc(int b) {
    while (b > 0)
        while (c > 0)
            break;
    b = 5;
}
/* expected:
1    START  -> 2
2    CHOICE -> 5 or 3
5    STEP   -> end
3    CHOICE -> 2 or 4
4    BREAK  -> 2
*/