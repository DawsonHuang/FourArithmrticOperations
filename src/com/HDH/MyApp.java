package com.HDH;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.HDH.Calculate.Compare;

/**
 * @author Dawson_Huang
 * @Date 2020/3/21 - 15:37
 */

public class MyApp {
    public static void main(String[] args) throws IOException {
        Help();
        Scanner in = new Scanner(System.in);
        System.out.println("要生成题目请输入1：");
        System.out.println("要比对答案请输入2：");
        int i = in.nextInt();
        if(i==1){
            System.out.println("请输入要生成题目的个数：");
            System.out.print("MyApp.exe -n ");
            int a = in.nextInt();
            System.out.println("请输入题目中数值的范围：");
            System.out.print("MyApp.exe -r ");
            int b = in.nextInt();
            Test.puzzle(a,b);
            FileInputStream file = new FileInputStream("ExerciseFile.txt");
            InputStreamReader is = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(is);

            File f1 = new File("Answer.txt");
            FileWriter fileWriter =new FileWriter(f1);
            fileWriter.write("");
            fileWriter.flush();
            //fileWriter.close();
            FileOutputStream fos=new FileOutputStream(f1,true);
            int count=1;
            String st;
            while((st=br.readLine())!=null) {
                fos.write((count+"."+Calculate.result(st)+"\n").getBytes());
                count++;
            }
        }else if(i==2){
            System.out.println("请输入题目文件和答案文件：");
            System.out.print("MyApp.exe ");
            in.nextLine();
            Compare("Answer.txt","MyAnswer.txt");
        }
    }
    public static void Help(){
        System.out.println("**************************************************************");
        System.out.println("**                         HELP                             **");
        System.out.println("**  1. MyApp.exe -n num  //num控制生成题目的个数             **");
        System.out.println("**  2. MyApp.exe -r num  //num控制题目中数值的范围           **");
        System.out.println("**  3. MyApp.exe -e <ExerciseFile>.txt -a <AnswerFile>.txt  **");
        System.out.println("**  //表示根据题目文件和答案文件批改答案                       **");
        System.out.println("***************************************************************");
    }
}
class Calculate{
    public static String result(String st) throws IOException {

            //把表达式用数组形式分隔开
            String s = st.replace(" ","");  //去掉表达式中空格
            Matcher m = Pattern.compile("(?<=[\\d+]\\.)+.+(?=\\=)").matcher(s);   //
            String expression = "";
            while(m.find()){
                expression = expression+m.group();
            }
            //System.out.println(expression);
            //数值部分
            m = Pattern.compile("(\\d+'\\d+/\\d+)|(\\d+/\\d+)|(\\d+)").matcher(expression);
            String f = null;
            while (m.find()) {
                if(f==null)
                    f = m.group();
                else
                    f = f+" "+m.group();
            }
            String[] str1 = f.split(" ");
            //System.out.println(Arrays.toString(str1));
            //操作符
            for(String s1:str1) {
                expression = expression.substring(0,expression.indexOf(s1))+expression.substring(expression.indexOf(s1)+s1.length());
            }
            //System.out.println(num);
            String[] str2 = expression.split("");
            //System.out.println(Arrays.toString(str2));
            //操作符插入操作数中
            String[] str = new String[str1.length+str2.length];
            for(int i=0,j=0,k=0;i<str1.length+str2.length;){
                if(j<str1.length) {
                    str[i] = str1[j];
                    j++;   i++;
                }
                if(k<str2.length) {
                    str[i] = str2[k];
                    i++;    k++;
                    if((k!=str2.length && k!=0) && (str2[k].equals("(") || str2[k-1].equals(")"))){
                        str[i] = str2[k];
                        k++;
                        i++;
                    }
                }
            }
            //得到中缀表达式的数组
            //System.out.println(Arrays.toString(str));
            //*************************************************************
            //表达式中缀转后缀
            Stack<String> operation = new Stack<>();   //操作符的栈
            Stack<String> number = new Stack<>();   //操作数的栈
            for(String token:str){
                //判断是符号还是数字
                if(isNumber(token)){
                    number.push(token);
                } else if(isOperation(token)){
                    //操作符需要入栈的情况
                    if(operation.isEmpty() || operation.peek().equals("(") ||
                            priority(token)>priority(operation.peek())){
                        operation.push(token);
                    } else {  //否则将栈中元素出栈如队，直到遇到大于当前操作符或者遇到左括号时
                        while(!operation.isEmpty() && priority(operation.peek())>=priority(token)
                                && !"(".equals(operation.peek())){
                            number.push(operation.pop());
                        }
                        operation.push(token);
                    }
                } else if("(".equals(token)){
                    operation.push(token);
                } else if(")".equals(token)){
                    while(!operation.isEmpty()){
                        if ("(".equals(operation.peek())) {
                            operation.pop();
                            break;
                        } else {
                            number.add(operation.pop());
                        }
                    }
                }
            }
            while(!operation.isEmpty()){
                number.push(operation.pop());
            }
            //System.out.println(number);
            //中缀转后缀完成****************************************************
            //后缀表达式求值
            Stack<String> stack = new Stack<>();
            while(!number.isEmpty()){
                stack.push(number.pop());
            }
            Stack<String> num = new Stack<>();
            while (!stack.isEmpty()) {
                if (isNumber(stack.peek())) {
                    num.push(stack.pop());
                } else if (isOperation(stack.peek())) {
                    String op = stack.pop();
                    String right = num.pop();
                    String left = num.pop();
                    num.push(compute(left, right, op));
                    if(compute(left,right,op).equals("-1"))
                        return -1+"";
                }
            }
            return num.pop();
    }
    private static boolean isNumber(String str){
        switch (str){
            case "+":
            case "-":
            case "*":
            case "÷":
            case "(":
            case ")":
                return false;
            default:
                return true;
        }
    }
    private static boolean isOperation(String str){
        if(str.matches("[+|\\-|*|÷]"))
            return true;
        else
            return false;
    }
    private static int priority(String op){
        if(op.equals("*") || op.equals("÷"))
            return 1;
        else if(op.equals("+") || op.equals("-"))
            return 0;
        return -1;
    }
    public static String compute(String left, String right, String op){   //＜（＾－＾）＞
        String str = null;
        //取出分母，求最小公倍数，通分
        Matcher i,j;
        String f = "";
        String g = "";
        String left_fenmu = "";
        String right_fenmu = "";
        if(left.matches("^\\d+$")) {  //整数：分母为1
            left_fenmu = 1+"";
        } else{    //分数
            i = Pattern.compile("(?<=([\\d+]')?[\\d+]/)+\\d+").matcher(left);
            while (i.find()){
                f = f + i.group();
            }
            left_fenmu = f;
        }
        if(right.matches("^\\d+$")){    //整数：分母为1
            right_fenmu = 1+"";
        } else{
            j = Pattern.compile("(?<=([\\d+]')?[\\d+]/)+\\d+").matcher(right);
            while (j.find()){
                g = g + j.group();
            }
            right_fenmu = g;
        }
        //______________________________________________________________________________
        int GBS = GongBeiShu(Integer.parseInt(left_fenmu),Integer.parseInt(right_fenmu));
        int[] a = HandleFraction(left);
        int[] b = HandleFraction(right);
        int numerator = 0;
        int denominator = 0;
        switch (op){
            case "+":
                if(a[2]!=0&&b[2]!=0)
                    numerator = (a[0]*a[2]+a[1])*(GBS/a[2])+(b[0]*b[2]+b[1])*(GBS/b[2]);
                denominator = GBS;
                break;
            case "-":
                if(a[2]!=0&&b[2]!=0)
                    numerator = (a[0]*a[2]+a[1])*(GBS/a[2])-(b[0]*b[2]+b[1])*(GBS/b[2]);
                denominator = GBS;
                if(numerator<0)
                    return -1+"";
                break;
            case "*":
                if(a[2]!=0&&b[2]!=0)
                    numerator = (a[0]*a[2]+a[1])*(b[0]*b[2]+b[1]);
                denominator = a[2]*b[2];
                break;
            case "÷":
                numerator = (a[0]*a[2]+a[1])*b[2];
                denominator = a[2]*(b[0]*b[2]+b[1]);
                break;
        }
        str = Simplify(numerator,denominator);
        return str;
    }
    public static int[] HandleFraction(String num){
        int[] digit = new int[3];
        //整数部分
        Matcher m = Pattern.compile("([\\d+]+(?=('\\d+/\\d+)))|^[\\d+]*$").matcher(num);
        String f = "";
        while (m.find()) {
            f = f + m.group();
        }
        if (f!="") {
            digit[0] = Integer.parseInt(f);
        } else {
            digit[0] = 0;
        }
        //分子部分
        m = Pattern.compile("(?<=')?+[\\d+]+(?=/)").matcher(num);
        f = "";
        while (m.find()) {
            f = f + m.group();
        }
        if (f!="") {
            digit[1] = Integer.parseInt(f);
        } else {
            digit[1] = 0;
        }
        //分母部分
        m = Pattern.compile("(?<=/)+\\d+").matcher(num);
        f = "";
        while (m.find()) {
            f = f + m.group();
        }
        if (f!="") {
            digit[2] = Integer.parseInt(f);
        } else {
            digit[2] = 1;
        }
        return digit;
    }
    //求分子分母的最小公倍数
    public static int GongBeiShu(int a,int b){
        int c,d;
        d=a*b;
        while(b!=0&&a%b!=0) {   //
            c=a%b;
            a=b;
            b=c;
        }
        if(b!=0)
            d=d/b;
        return d;
    }
    //求分子分母的最小公约数
    public static int GongYueShu(int a,int b){
        if (b == 0) return a;
        return a % b == 0 ? b : GongYueShu(b, a % b);
    }
    public static String Simplify(int numerator,int denominator){
        if(numerator==0){
            return 0+"";
        }else {
            int g = GongYueShu(numerator, denominator);
            numerator /= g;
            denominator /= g;
            int a=0;
            int b=0;
            if(denominator!=0) {
                a = numerator / denominator;
            }
            if(denominator!=0) {
                b = numerator % denominator;
            }
            if (a >= 1 && b == 0) {
                return a + "";
            } else if (a >= 1 && b > 0) {
                return a + "'" + b + "/" + denominator;
            } else if (a == 0) {
                return b + "/" + denominator;
            }
            return null;
        }
    }
    //批改答案：
    public static void Compare(String fileName1,String fileName2) throws IOException {
        File file01 = new File(fileName1);
        File file02 = new File(fileName2);
        FileInputStream file1 = new FileInputStream(file01.getAbsoluteFile());
        FileInputStream file2 = new FileInputStream(file02.getAbsoluteFile());
        InputStreamReader is1 = new InputStreamReader(file1);
        InputStreamReader is2 = new InputStreamReader(file2);
        BufferedReader br1 = new BufferedReader(is1);
        BufferedReader br2 = new BufferedReader(is2);
        List wrong = new ArrayList();
        List right = new ArrayList();
        String a;
        String b;
        Matcher m,n;
        while((a=br1.readLine())!=null && (b=br2.readLine())!=null){
            m = Pattern.compile("\\d+(?=\\..*)").matcher(a);
            n = Pattern.compile("\\d+(?=\\..*)").matcher(b);
            if(a.equals(b)){
                while(m.find()) {
                    right.add(m.group());
                }
            } else {
                while(n.find()) {
                    wrong.add(n.group());
                }
            }
        }
        String str1 = "wrong:(";
        String str2 = "right:(";
        for(int i=0;i<wrong.size();i++){
            str1 = str1+wrong.get(i);
            if(i==wrong.size()-1)
                str1 = str1+")";
            else
                str1 = str1+",";
        }
        for(int i=0;i<right.size();i++){
            str2 = str2+right.get(i);
            if(i==right.size()-1)
                str2 = str2+")";
            else
                str2 = str2+",";
        }
        System.out.println(str1);
        System.out.println(str2);
    }
}