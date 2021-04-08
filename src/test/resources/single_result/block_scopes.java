{
        a=1;
        {
            b=2;
        }
        c=3;
}
/* expected:
1    START  -> 2
2    STEP   -> 4
4    STEP   -> 6
6    STEP   -> end
*/