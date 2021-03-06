package challenges;

import org.testng.annotations.Test;

public class FibonacciChallenge {

    @Test
    void fibonacciSequencing() {
        int fibNum = 356;

        for(int i = 0; i < fibNum + 1; i++) {
            System.out.println("F("+ i +")= " + getFib(i).toString() + ", " + convertToText(getFib(i)));
            if (i > 19) {
                System.out.println("This test only supports fibonacci numbers up to 20.");
                break;
            }
        }
    }

    public Integer getFib(int n) {
        if (n <= 1)
        return n;
            else
        return getFib(n - 1) + getFib(n - 2);
    }

    public String convertToText(int num) {
        String[] ones = {"one","two","three","four","five","six","seven","eight","nine"};
        String[] teens = {"ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eightteen","nineteen"};
        String[] tens = {"ten","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};

        if(num == 0) {
            return "zero";
        }
        else if(num <= 9) {
            return ones[num - 1];
        }
        else if(9 < num && num <= 19) {
            return teens[num - 10];
        }
        else if(19 < num && num <= 99) {
            int first = Integer.parseInt(String.valueOf(num).substring(0, 1));
            int second = Integer.parseInt(String.valueOf(num).substring(1,2));

            if(second==0) {
                return tens[first - 1];
            } else {
                return tens[first - 1] + "-" + ones[second - 1];
            }
        }
        else if(99 < num && num <= 999) {
            int first = Integer.parseInt(String.valueOf(num).substring(0, 1));
            int second = Integer.parseInt(String.valueOf(num).substring(1,2));
            int third = Integer.parseInt(String.valueOf(num).substring(2,3));

            if(second==0 && third==0) {
                return ones[first - 1] + " hundred";
            } else if(second==0 && third>0) {
                return ones[first - 1] + " hundred and " + ones[third - 1];
            } else if(second>0 && third==0) {
                return ones[first - 1] + " hundred and " + tens[second - 1];
            } else {
                return ones[first - 1] + " hundred and " + tens[second - 1] + "-" + ones[third - 1];
            }
        }
        else if(999 < num && num <= 9999) {
            int first = Integer.parseInt(String.valueOf(num).substring(0, 1));
            int second = Integer.parseInt(String.valueOf(num).substring(1,2));
            int third = Integer.parseInt(String.valueOf(num).substring(2,3));
            int fourth = Integer.parseInt(String.valueOf(num).substring(3,4));

            if(second==0 && third==0 && fourth==0) {
                return ones[first - 1] + " thousand";
            }else if(second>0 && third==0 && fourth==0) {
                return ones[first - 1] + " thousand " + ones[second - 1] + " hundred";
            } else if(second==0 && third>0 && fourth==0) {
                return ones[first - 1] + " thousand and " + tens[third - 1];
            } else if (second==0 && third==0 && fourth>0) {
                return ones[first - 1] + " thousand and" + ones[fourth - 1];
            } else if (second>0 && third>0 && fourth==0) {
                return ones[first - 1] + " thousand " + ones[second - 1] + " hundred and " + tens[third - 1];
            } else if(second>0 && third==0 && fourth>0) {
                return ones[first - 1] + " thousand " + ones[second - 1] + " hundred and " + ones[fourth - 1];
            } else if(second==0 && third>0 && fourth>0) {
                return ones[first - 1] + " thousand " + tens[third -1] + "-" + ones[fourth - 1];
            }
            else {
                return ones[first - 1] + " thousand " + ones[second - 1] + " hundred and " + tens[third - 1] + "-" + ones[fourth - 1];
            }
        }
        return null;
    }

    @Test
    void test() {
        int num = 78;
        String first = String.valueOf(num).substring(0,1);
        System.out.println(first);
        String second = String.valueOf(num).substring(1,2);
        System.out.println(second);
    }

}
