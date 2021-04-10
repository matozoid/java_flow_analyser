void a() {
    try {
            throw new RuntimeException();
    } catch (IOException a) {
    }
    throw new RuntimeException();
}
/* expected:
1    START  -> 3
3    THROW  -> end *** Cannot define a throws-flow without the symbol solver. ***
*/