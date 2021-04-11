int x(){
    switch (a) {
        case 0:
                return 15;
        case 1:
                return 66;
        case 2:
        case 3:
                return 99;
        default:
                throw new RuntimeException();
    }
    a=99;
}
/* expected:
1    START  -> 3
3    CHOICE -> 5 or 4 (cond: 3:14)
5    CHOICE -> 7 or 6 (cond: 5:14)
4    RETURN -> end
7    CHOICE -> 8 or 9 (cond: 7:14)
6    RETURN -> end
8    CHOICE -> 11 or 9 (cond: 8:14)
9    RETURN -> end
11   THROW  -> end *** Cannot define a throws-flow without the symbol solver. ***
*/