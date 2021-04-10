void abc() {
    List<String> string= Arrays.asList("a", "b");
    for(String x: string){
        System.out.println(x);
    }
}
/* expected:
1    START  -> 2
2    STEP   -> 3
3    CHOICE -> end or 4
4    STEP   -> 3
*/