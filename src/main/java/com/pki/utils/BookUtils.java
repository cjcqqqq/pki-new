package com.pki.utils;

import com.pki.entity.Cabook;
import com.pki.entity.User;
import com.pki.enums.CAState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author by twjitm on 2019/3/12/10:54
 */
public class BookUtils {
    private static Logger logger = LoggerFactory.getLogger(BookUtils.class);

    /**
     * 生成密钥
     */
    public static void genkey(Cabook cabook) {
        String keytool = PropertiesUtils.getKeytool();
        String[] arstringCommand = new String[]{
                "cmd ", "/k",
                "start", // cmd Shell命令
                keytool,
                "-genkey", // -genkey表示生成密钥
                "-validity", // -validity指定证书有效期(单位：天)，这里是36500天
                "36500",
                "-keysize",//     指定密钥长度
                "1024",
                "-alias", // -alias指定别名，这里是ss
                "ss",
                "-keyalg", // -keyalg 指定密钥的算法 (如 RSA DSA（如果不指定默认采用DSA）)
                "RSA",
                "-keystore", // -keystore指定存储位置，这里是d:/demo.keystore
                cabook.getCaUrl(),
                "-dname",// CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称),
                // ST=(州或省份名称), C=(单位的两字母国家代码)"
                "CN=(" + cabook.getCaCn() + "), OU=(" + cabook.getCaOu() + "), O=(" + cabook.getCaO() + "), L=(" + cabook.getCaL() + "),ST=("
                        + cabook.getCaSt() + "), C=(" + cabook.getCaC() + ")",
                "-storepass", // 指定密钥库的密码(获取keystore信息所需的密码)
                PropertiesUtils.getLibraryPsd(),
                "-keypass",// 指定别名条目的密码(私钥的密码)
                cabook.getCaKeypass(),
                "-v"// -v 显示密钥库中的证书详细信息
        };

        execCommand(arstringCommand);
    }

    /**
     * 管理员 导出证书文件
     */
    public static void export(Cabook cabook, User user) {
        String keytool = PropertiesUtils.getKeytool();
        String url = PropertiesUtils.getBookPath() + user.getUName() + cabook.getCaId() + ".cer";
        String[] arstringCommand = new String[]{

                "cmd ", "/k",
                "start", // cmd Shell命令
                keytool,
                "-export", // - export指定为导出操作
                "-keystore", // -keystore指定keystore文件，这里是d:/demo.keystore
                cabook.getCaUrl(),
                "-alias", // -alias指定别名，这里是ss
                "ss",
                "-file",//-file指向导出路径
                "d:/" + user.getUName() + cabook.getCaId() + ".cer",
                "-storepass",// 指定密钥库的密码
                "123456"
        };
        execCommand(arstringCommand);
        cabook.setCaStart(CAState.PASS.getDiscribe());
        cabook.setCaUrl(url);


    }

    private static void execCommand(String[] arstringCommand) {
        for (int i = 0; i < arstringCommand.length; i++) {
            logger.debug(arstringCommand[i] + " ");
        }
        try {
            Runtime.getRuntime().exec(arstringCommand);

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }


}
