package cora.util;

public class StringUtil {
    public static String lowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String upperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //merge two string
    public static String merge(String str1,String str2){
        int left1 = 0;
        int right1 = 0;
        int left2 = 0;
        int right2 = 0;
        for(int i = 0;i<str1.length();i++){
            if(str1.charAt(i)!=str2.charAt(i)){
                left1 = left2 = i;
                break;
            }
        }
        right1 = str1.charAt(')');
        right2 = str2.charAt(')');
        for(int i = right1;i>=0;i--){
            if(str1.charAt(right1)==str2.charAt(right2)){
                right1--;
                right2--;
            }
        }
        StringBuffer sb = new StringBuffer();
        String a = str1.substring(left1,right1);
        String b = str2.substring(left2,right2);
        sb.append(str1.substring(0,left1)).append(a).append(b).append(str1.substring(right1));
        return sb.toString();
    }
}
