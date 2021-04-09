import java.io.IOException;

class X {
    void a() {
        try {
            if (a == 0) {
                throw new RuntimeException();
            }
            if (a == 1) {
                throw new IOException();
            }
        } catch (IOException a) {
            x = 99;
        } catch (Throwable a) {
            x = 98;
        } catch (RuntimeException a) {
            x = 97;
        } finally {
            q = 333;
        }
        throw new RuntimeException();
    }
}
/* expected:
4    START  -> 6
6    CHOICE -> 9 or 7
9    CHOICE -> 19 or 10
7    STEP   -> 9
19   STEP   -> 21
10   STEP   -> 19
21   STEP   -> end
*/