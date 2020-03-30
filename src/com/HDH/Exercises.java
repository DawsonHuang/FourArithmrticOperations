package com.HDH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * @author 92
 * @Date 2020/3/28 - 20:33
 */

public class Exercises {
    public static HashSet<String> puzzle(int n, int r) throws IOException {
        //生成题目的函数
        File f = new File("ExerciseFile.txt");
        FileOutputStream fos = new FileOutputStream(f);
        //字节输出流
        HashSet<String> formula = new HashSet<>();
        Maker m = new Maker();
        //调用第一个类
        Random a = new Random();
        ArrayList list1,list2;
        //list1存放随机数，list2存放随机字符
        int k;
        //选择字符的个数
        int count = 1;
        //用来标记题号
        String str;
        for(;n>0;n--){
            k = a.nextInt(3);
            if(k==0){
                //只有一个运算符
                list1 = m.number(k+2,r);
                list2 = m.symbol(k+1);

                str = count+"."+list1.get(0)+" "+list2.get(0)+" "+list1.get(1)+" "+"=\n";
                if(Calculate.result(str).equals("-1")){
                    n++;
                    continue;
                }
                try{
                    fos.write(str.getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }
                list1.clear();
                list2.clear();
                formula.add(str);
            }
            else if(k==1){
                //有两个运算符
                list1 = m.number(k+2,r);
                list2 = m.symbol(k+1);
                boolean temp = false;
                for(int l=0;l<=k;l++){
                    //检测是否有左括号
                    if(a.nextBoolean()&& !temp){
                        list1.set(l,"("+list1.get(k));
                        temp = true;
                    }
                    else if (a.nextBoolean()&&temp){
                        //如果有左括号则用随机数判读是否加右括号
                        list1.set(l,list1.get(k+1)+")");
                        temp = false;
                    }
                    if(l==k&&temp){
                        //如果前面有左括号但是已经遍历到最后一个元素时加右括号
                        list1.set(k+1,list1.get(k+1)+")");
                    }
                }
                str = count+"."+list1.get(0)+" "+list2.get(0)+" "+list1.get(1)+" "+list2.get(1)+" "+list1.get(2)+"=\n";
                if(Calculate.result(str).equals("-1")){
                    n++;
                    continue;
                }
                try{
                    fos.write(str.getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }
                list1.clear();
                list2.clear();
                formula.add(str);
            }
            else{
                //有三个运算符
                list1 = m.number(k+2,r);
                //4个数
                list2 = m.symbol(k+1);
                boolean temp = false;
                for(int l=0;l<=k;l++){
                    //检测是否有左括号
                    if(a.nextBoolean()&& !temp){
                        list1.set(l,"("+list1.get(k));
                        temp = true;
                    }
                    else if (a.nextBoolean()&&temp){
                        //如果有左括号则用随机数判读是否加右括号
                        list1.set(l,list1.get(k+1)+")");
                        temp = false;
                    }
                    if(l==k&&temp){
                        //如果前面有左括号但是已经遍历到最后一个元素时加右括号
                        list1.set(k+1,list1.get(k+1)+")");
                    }
                }
                str = count+"."+list1.get(0)+" "+list2.get(0)+" "+list1.get(1)+" "+list2.get(1)+" "+list1.get(2)+
                        " "+list2.get(2)+" "+list1.get(3)+" "+"=\n";
                if(Calculate.result(str).equals("-1")){
                    n++;
                    continue;
                }
                try{
                    fos.write(str.getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                }
                list1.clear();
                list2.clear();
                formula.add(str);
            }
            count++;
        }
        try {
            fos.close();
            //关闭输出
        } catch (IOException e){
            e.printStackTrace();
        }
        return formula;
    }
}
class Maker {
    public ArrayList<String> number(int n, int range) {
        //生成n个数且每个数要小于r，
        int numerator,denominator;
        int num1,num2;
        //定义分子分母
        ArrayList<String> list = new ArrayList<>();
        Random a = new Random();
        for(;n>0;n--){
            //循环n次得到n个数
            if(a.nextBoolean()){
                //如果是true就生成整数，否则生成分数
                list.add(a.nextInt(range)+1+"");
                //生成一个整数
            }
            else {
                numerator = a.nextInt(range)+1;
                denominator = a.nextInt(range)+1;
                if(numerator>=denominator&&numerator%denominator==0){
                    //如果是整数的情况
                    num1 = numerator/denominator;
                    list.add(num1+"");
                }
                else if(numerator/denominator==0){
                    //判断分数是否大于1
                    list.add(numerator+"/"+denominator);
                }
                else{
                    num1 = numerator/denominator;
                    num2 = numerator-denominator*num1;
                    list.add(num1+"'"+num2+"/"+denominator);
                }
            }
        }
        return list;
    }

    public ArrayList<String> symbol(int n){
        int k;
        ArrayList<String> list = new ArrayList<>();
        Random a = new Random();
        while(n>0){
            k=a.nextInt(4);
            switch (k){
                case 0:list.add("+");break;
                case 1:list.add("-");break;
                case 2:list.add("*");break;
                case 3:list.add("÷");break;
                default: break;
            }
            n--;
        }
//            System.out.println(list);
        return list;
    }
}