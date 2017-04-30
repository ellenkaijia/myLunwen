package org.easypoint;

import java.util.Random;

/**
 * description
 *
 * @author cqt
 * @version 1.0
 * @since 2017/3/23
 */
public class ZcRandomUtil {

    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERCHAR = "0123456789";
    public static final String LOWERCHAR = "abcdefghijkllmnopqrstuvwxyz";

    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満瀛楃涓�(鍙寘鍚暟瀛�)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateNumber(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERCHAR.charAt(random.nextInt(NUMBERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満瀛楃涓�(鍙寘鍚ぇ灏忓啓瀛楁瘝銆佹暟瀛�)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満绾瓧姣嶅瓧绗︿覆(鍙寘鍚ぇ灏忓啓瀛楁瘝)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満绾ぇ鍐欏瓧姣嶅瓧绗︿覆(鍙寘鍚ぇ灏忓啓瀛楁瘝)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }

    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満绾皬鍐欏瓧姣嶅瓧绗︿覆(鍙寘鍚ぇ灏忓啓瀛楁瘝)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateUpperString(int length) {
        return generateMixString(length).toUpperCase();
    }

    /**
     * 鐢熸垚涓�涓畾闀跨殑绾�0瀛楃涓�
     *
     * @param length
     *            瀛楃涓查暱搴�
     * @return 绾�0瀛楃涓�
     */
    public static String generateZeroString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    /**
     * 鏍规嵁鏁板瓧鐢熸垚涓�涓畾闀跨殑瀛楃涓诧紝闀垮害涓嶅鍓嶉潰琛�0
     *
     * @param num
     *            鏁板瓧
     * @param fixdlenth
     *            瀛楃涓查暱搴�
     * @return 瀹氶暱鐨勫瓧绗︿覆
     */
    public static String toFixdLengthString(long num, int fixdlenth) {
        StringBuffer sb = new StringBuffer();
        String strNum = String.valueOf(num);
        if (fixdlenth - strNum.length() >= 0) {
            sb.append(generateZeroString(fixdlenth - strNum.length()));
        } else {
            throw new RuntimeException("灏嗘暟瀛�" + num + "杞寲涓洪暱搴︿负" + fixdlenth
                    + "鐨勫瓧绗︿覆鍙戠敓寮傚父锛�");
        }
        sb.append(strNum);
        return sb.toString();
    }

    /**
     * 姣忔鐢熸垚鐨刲en浣嶆暟閮戒笉鐩稿悓
     *
     * @param param
     * @return 瀹氶暱鐨勬暟瀛�
     */
    public static int getNotSimple(int[] param, int len) {
        Random rand = new Random();
        for (int i = param.length; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = param[index];
            param[index] = param[i - 1];
            param[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < len; i++) {
            result = result * 10 + param[i];
        }
        return result;
    }
    
    /**
     * 杩斿洖涓�涓畾闀跨殑鍏ㄦ槸灏忓啓瀛楁瘝鐨勫瓧绗︿覆(鍏ㄦ槸灏忓啓瀛楁瘝)
     * @param length
     * @return
     */
    public static String generateAllLowerString(int length) {
    	return generateLowMixString(length);
    }
    
    /**
     * 杩斿洖涓�涓畾闀跨殑闅忔満绾瓧姣嶅瓧绗︿覆(鍙寘鍚皬鍐欏瓧姣�)
     *
     * @param length
     *            闅忔満瀛楃涓查暱搴�
     * @return 闅忔満瀛楃涓�
     */
    public static String generateLowMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(LOWERCHAR.charAt(random.nextInt(LOWERCHAR.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
    	Random random = new Random();
    	System.out.println((double)(random.nextInt(99) + 1.0));
    	System.out.println(random.nextDouble());
    	System.out.println(98L/(double)10.00);
    }

}
