void abc(int b) {
    int a = 10;
    // the CHOICE is oddly placed at the next line.
    // That's because the first line of the DoStmt node is printed.
    do {
        a--;
        b++;
    } while (a > 0);
    return b;
}
/* expected:
1    START  -> 2
2    STEP   -> 6
6    STEP   -> 7
7    STEP   -> 5
5    CHOICE -> 9 or 6
9    RETURN -> end
*/