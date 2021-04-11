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
3    CHOICE -> 5 or 4
5    CHOICE -> 8 or 6
4    RETURN -> end
8    CHOICE -> 10 or 9
6    RETURN -> end
10   CHOICE -> end or 11
9    RETURN -> end
11   THROW  -> end *** Cannot define a throws-flow without the symbol solver. ***
*/