void x() {
    for (int a = 3, b = 5; a < 99; a++, b++) {
        if (a == 1) {
            continue;
        }
        if (a == 2) {
            break;
        }
    }
}

/* expected:
1    START  -> 2
2    FOR_IN -> 2
2    CHOICE -> end or 3 (cond: 2:28)
3    CHOICE -> 6 or 4 (cond: 3:13)
6    CHOICE -> 2 or 7 (cond: 6:13)
4    CONTIN -> 2
2    FOR_UP -> 2
7    BREAK  -> end
*/