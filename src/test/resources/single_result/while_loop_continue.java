void abc(int b) {
    while (b > 0) {
        b--;
        continue;
        b++;
    }
    return b;
}
/* expected:
1    START  -> 2
2    CHOICE -> 7 or 3
7    RETURN -> end
3    STEP   -> 4
4    CONTIN -> 2
*/