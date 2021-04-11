void x(){
    int a=0;
    switch (a) {
        case 0:
                // empty case at start
        case 1:
                System.out.println("01");
        case 2:
                System.out.println("012");
                if(a==16) {
                        // conditional break
                        break;
                }
        case 3:
                // fall through an empty case
        case 4:
                System.out.println("3");
                // fall through to default
        default:
                System.out.println("3 default");
    }
    a=99;
}
/* expected:
1    START  -> 2
2    STEP   -> 4
4    CHOICE -> 6 or 7
6    CHOICE -> 8 or 7
7    STEP   -> 9
8    CHOICE -> 14 or 9
9    STEP   -> 10
14   CHOICE -> 16 or 17
10   CHOICE -> 17 or 12
16   CHOICE -> 20 or 17
17   STEP   -> 20
12   BREAK  -> 22
20   STEP   -> 22
22   STEP   -> end
*/