package com.mayikt.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/8/20 0020 上午 9:35
 * @version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FtpConfig {
    public String fileHost;

    public String fileUser;

    public String filePwd;

    public Integer filePort;

    public Integer timeOut;

}
