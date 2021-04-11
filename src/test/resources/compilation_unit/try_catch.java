import java.io.IOException;

class X {
    void a() {
        try {
            try {
                if (a == 0) {
                    throw new RuntimeException();
                }
                if (a == 1) {
                    throw new IOException();
                }
            } catch (IOException a) {
                x = 99;
            } catch (RuntimeException a) {
                x = 97;
            } catch (Throwable a) {
                x = 98;
            } finally {
                q = 333;
            }
        } catch (RuntimeException a) {
            x = 0;
        }
        throw new RuntimeException();
    }
}
/* expected:
4    START  -> 7
7    CHOICE -> 10 or 8 (cond: 7:21)
10   CHOICE -> 20 or 11 (cond: 10:21)
8    THROW  -> 16
20   STEP   -> 25
11   THROW  -> 14
16   STEP   -> 20
25   THROW  -> end
14   STEP   -> 20
20   STEP   -> 25
*/