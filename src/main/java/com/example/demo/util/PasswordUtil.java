package com.example.demo.util;

import org.springframework.util.DigestUtils;

import java.util.Random;

public class PasswordUtil {

    private static final Random rnd = new Random();

    private static byte[] addSalt(byte[] bs, byte[] salt) {
        byte[] full = new byte[bs.length + 4];
        byte[] tail = new byte[4];

        if (salt == null) {
            rnd.nextBytes(tail);
        } else {
            tail = salt;
        }

        System.arraycopy(bs, 0, full, 0, bs.length);
        System.arraycopy(tail, 0, full, bs.length, tail.length);

        return full;
    }

    public static String encryptPwd(String pwd) {
        byte[] bsPwd = pwd.getBytes();

        byte[] saltPwd = addSalt(bsPwd, null);
        String digestHex = DigestUtils.md5DigestAsHex(saltPwd);

        StringBuilder sb = new StringBuilder();
        for (int i = bsPwd.length; i < saltPwd.length; i++) {
            String byteHex = Integer.toHexString(saltPwd[i] & 0xff);

            int fillCount = 2 - byteHex.length();

            for (int j = 0; j < fillCount; j++) {
                sb.append('0');
            }
            sb.append(byteHex);
        }

        sb.append(digestHex);

        return sb.toString();
    }

    public static boolean verifyPassword(String pwd, String cipher) {
        String salt = cipher.substring(0, 8);
        String md5 = cipher.substring(8);

        assert md5.length() == 32;
        assert salt.length() == 8;

        byte[] bsSalt = new byte[4];
        int bi = 0;
        for (int i = 0; i < salt.length(); i+=2) {
            String byteHex = salt.substring(i, i+2);
            byte b = (byte) Integer.parseInt(byteHex, 16);
            bsSalt[bi++] = b;
        }

        byte[] saltPwd = addSalt(pwd.getBytes(), bsSalt);

        return DigestUtils.md5DigestAsHex(saltPwd)
                .equals(md5);
    }
}
