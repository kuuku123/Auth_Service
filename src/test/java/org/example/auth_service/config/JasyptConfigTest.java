package org.example.auth_service.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

class JasyptConfigTest {

  @Test
  void jasycrpt() {
    String url = "jdbc:mysql://mysql-test:3306/test?serverTimezone=UTC&characterEncoding=UTF-8";
    String username = "root";
    String password = "1234";
    String mail = "tonydevpc123@gmail.com";
//        String mailpassword = "oirhttfuzgfsonyq";
    String mailpassword = "udnyoanloarjiucu";
    System.out.println(jasyptEncoding(url));
    System.out.println(jasyptEncoding(username));
    System.out.println(jasyptEncoding(password));
    System.out.println(jasyptEncoding(mail));
    System.out.println(jasyptEncoding(mailpassword));
  }

  public String jasyptEncoding(String value) {
    String key = "my_jasypt_key";
    StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
    pbeEnc.setAlgorithm("PBEWithMD5AndDES");
    pbeEnc.setPassword(key);
    return pbeEnc.encrypt(value);
  }

}